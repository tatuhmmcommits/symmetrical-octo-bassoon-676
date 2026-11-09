package net.charter.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.charter.config.CharterConfig;
import net.charter.fx.Fx;
import net.charter.logic.Contracts;
import net.charter.logic.Judgement;
import net.charter.logic.Verdict;
import net.charter.registry.ModDamageTypes;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Divine Dominance: a pillar of Charter, 1.5 blocks wide and 3 blocks tall, wrapped in golden chains and marked
 * with the sigil of Charter. It is an entity rather than a block and has no hitbox (like an armor stand with the
 * marker flag): nothing can hit it, push it, stand on it or collide with it, so the only things that end it are
 * its own judgement and an operator's command.
 *
 * <p>Once raised it builds a golden square wall around itself that slowly closes in; only its owner and the souls
 * bound to Charter can cross the wall, and whoever is still inside when the wall has closed is judged.
 *
 * <p>The geometry is a square of half width {@code h} around the pillar, from {@code down} blocks below its base to
 * {@code up} blocks above it. {@code h} stays at its starting value while the pillar arms, then shrinks to its
 * final value; see {@link #halfWidth}. The client draws the pillar, the sigil under it and the wall from the
 * tracked values alone.
 */
public class DivineDominanceEntity extends Entity {
	public static final float WIDTH = 1.5f;
	public static final float HEIGHT = 3.0f;

	/** After closing, how long the pillar waits for the judgements to finish before it shatters anyway. */
	private static final long SHATTER_TIMEOUT = 900L;
	/** How far from the wall a creature is put when the wall turns it back. */
	private static final double MARGIN = 0.4;

	private static final TrackedData<Long> START = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.LONG);
	private static final TrackedData<Integer> ARM = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> SHRINK = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> START_HALF = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> END_HALF = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> UP = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DOWN = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CLOSED = DataTracker.registerData(DivineDominanceEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	private UUID owner;
	private String ownerName = "";
	private long closedAt;

	/** Which side of the wall each player was on during the previous tick (true is inside). */
	private final Map<UUID, Boolean> inside = new HashMap<>();
	private final List<UUID> sentenced = new ArrayList<>();

	public DivineDominanceEntity(EntityType<?> type, World world) {
		super(type, world);
		this.noClip = true;
		this.setNoGravity(true);
		this.setInvulnerable(true);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(START, -1L);
		this.dataTracker.startTracking(ARM, 100);
		this.dataTracker.startTracking(SHRINK, 2400);
		this.dataTracker.startTracking(START_HALF, 24.0f);
		this.dataTracker.startTracking(END_HALF, 1.0f);
		this.dataTracker.startTracking(UP, 48);
		this.dataTracker.startTracking(DOWN, 24);
		this.dataTracker.startTracking(CLOSED, false);
	}

	// ------------------------------------------------------------------ no hitbox

	/** Nothing can target it: not a crosshair, not an arrow, not a sword. */
	@Override
	public boolean canHit() {
		return false;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return true;
	}

	@Override
	public boolean isImmuneToExplosion() {
		return true;
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	// ------------------------------------------------------------------ readers (both sides)

	/** World time at which the pillar was raised; negative while it has not been armed yet. */
	public long getStart() {
		return this.dataTracker.get(START);
	}

	public int getArm() {
		return this.dataTracker.get(ARM);
	}

	public int getShrink() {
		return this.dataTracker.get(SHRINK);
	}

	public float getStartHalf() {
		return this.dataTracker.get(START_HALF);
	}

	public float getEndHalf() {
		return this.dataTracker.get(END_HALF);
	}

	public int getUp() {
		return this.dataTracker.get(UP);
	}

	public int getDown() {
		return this.dataTracker.get(DOWN);
	}

	public boolean isClosed() {
		return this.dataTracker.get(CLOSED);
	}

	/** Ticks since the pillar was raised, with the fraction supplied by the renderer. */
	public float age(float tickDelta) {
		return (float) (this.getWorld().getTime() - getStart()) + tickDelta;
	}

	/** Half width of the wall at the given age (ticks since the pillar was raised). */
	public static float halfWidth(float startHalf, float endHalf, int armTicks, int shrinkTicks, float age) {
		float progress = (age - (float) armTicks) / (float) Math.max(1, shrinkTicks);
		if (progress < 0.0f) {
			progress = 0.0f;
		} else if (progress > 1.0f) {
			progress = 1.0f;
		}
		return startHalf + (endHalf - startHalf) * progress;
	}

	public boolean isOwner(UUID id) {
		return owner != null && owner.equals(id);
	}

	/**
	 * The owner, everyone bound to Charter by a contract, and the creator of any contract the owner has signed are
	 * not touched by the wall.
	 */
	private boolean isExempt(MinecraftServer server, ServerPlayerEntity player) {
		if (isOwner(player.getUuid()) || Contracts.isBound(server, player.getUuid())) {
			return true;
		}
		return owner != null && Contracts.protectsCreator(server, owner, player.getUuid());
	}

	// ------------------------------------------------------------------ arming

	/** Called when the pillar is raised: the geometry is fixed from the config at this moment. */
	public void arm(ServerWorld world, @Nullable PlayerEntity placer) {
		CharterConfig config = CharterConfig.get();
		this.dataTracker.set(START_HALF, (float) config.dominanceStartRadius);
		this.dataTracker.set(END_HALF, (float) config.dominanceEndRadius);
		this.dataTracker.set(ARM, config.dominanceArmTicks);
		this.dataTracker.set(SHRINK, Math.max(1, config.dominanceShrinkTicks));
		this.dataTracker.set(UP, config.dominanceHeightUp);
		this.dataTracker.set(DOWN, config.dominanceHeightDown);
		this.dataTracker.set(CLOSED, false);
		this.dataTracker.set(START, world.getTime());
		this.closedAt = 0L;
		this.inside.clear();
		this.sentenced.clear();
		if (placer != null) {
			this.owner = placer.getUuid();
			this.ownerName = placer.getGameProfile().getName();
		}

		Vec3d center = this.getPos();
		Fx.sound(world, center, SoundEvents.BLOCK_BEACON_ACTIVATE, 8.0f, 0.5f);
		Fx.sound(world, center, SoundEvents.BLOCK_CHAIN_PLACE, 6.0f, 0.5f);
		Fx.sparks(world, center.add(0.0, 1.5, 0.0), 60, 0.8);
		if (placer instanceof ServerPlayerEntity) {
			placer.sendMessage(Text.literal("Divine Dominance is raised. The walls begin to close in " + (config.dominanceArmTicks / 20) + " seconds.").formatted(Formatting.GOLD), true);
		}
	}

	/** One line about this pillar for {@code /charter dominance list}. */
	public Text describe() {
		long start = getStart();
		String who = ownerName.isEmpty() ? "an unknown hand" : ownerName;
		String where = String.format(Locale.ROOT, "%d %d %d", MathHelper.floor(getX()), MathHelper.floor(getY()), MathHelper.floor(getZ()));
		String state;
		if (start < 0L) {
			state = "not armed yet";
		} else if (isClosed()) {
			state = "the wall has closed";
		} else {
			long age = this.getWorld().getTime() - start;
			if (age < (long) getArm()) {
				state = "the walls begin to close in " + (((long) getArm() - age) / 20L + 1L) + "s";
			} else {
				long left = Math.max(0L, (long) getArm() + (long) getShrink() - age) / 20L;
				state = String.format(Locale.ROOT, "the walls meet in %d:%02d", left / 60L, left % 60L);
			}
		}
		return Text.literal("Divine Dominance at " + where + ", raised by " + who + ": " + state + ".");
	}

	// ------------------------------------------------------------------ ticking

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld() instanceof ServerWorld) {
			serverTick((ServerWorld) this.getWorld());
		}
	}

	private void serverTick(ServerWorld world) {
		if (getStart() < 0L) {
			// summoned by a command rather than placed by a player: arm with the current config
			arm(world, null);
		}
		long age = world.getTime() - getStart();
		int armTicks = getArm();
		int shrinkTicks = getShrink();

		if (age >= (long) armTicks) {
			double half = (double) halfWidth(getStartHalf(), getEndHalf(), armTicks, shrinkTicks, (float) age);
			enforce(world, half, !isClosed());
			if (!isClosed() && age >= (long) armTicks + (long) shrinkTicks) {
				close(world);
			}
		}
		if (isClosed()) {
			boolean pending = false;
			for (UUID id : sentenced) {
				if (Judgement.isCondemned(id)) {
					pending = true;
					break;
				}
			}
			if (!pending || world.getTime() - closedAt > SHATTER_TIMEOUT) {
				shatter(world);
			}
		}
	}

	/**
	 * Keeps everyone on the side of the wall they were on: those inside are held in (and are carried along as it
	 * closes), those outside are kept out. Spectators, the owner and bound souls ignore the wall entirely.
	 */
	private void enforce(ServerWorld world, double half, boolean squeezing) {
		MinecraftServer server = world.getServer();
		Vec3d center = this.getPos();
		CharterConfig config = CharterConfig.get();
		double endHalf = (double) getEndHalf();
		int up = getUp();
		int down = getDown();
		boolean crush = squeezing && half <= endHalf + 3.0 && world.getTime() % 20L == 0L && config.dominanceCrushDamage > 0.0f;

		Map<UUID, Boolean> now = new HashMap<>();
		for (ServerPlayerEntity player : world.getPlayers()) {
			if (player.isSpectator() || isExempt(server, player)) {
				continue;
			}
			double height = player.getY() - center.y;
			if (height < -((double) down) - 2.0 || height > (double) up + 2.0) {
				continue;
			}
			UUID id = player.getUuid();
			double dx = player.getX() - center.x;
			double dz = player.getZ() - center.z;
			boolean in = Math.abs(dx) < half && Math.abs(dz) < half;

			Boolean was = inside.get(id);
			if (was != null && was.booleanValue() != in) {
				push(world, player, center, half, was.booleanValue());
				in = was.booleanValue();
			}
			now.put(id, in);

			if (in && crush) {
				player.damage(ModDamageTypes.source(world, ModDamageTypes.CRUSH), config.dominanceCrushDamage);
			}
		}
		inside.clear();
		inside.putAll(now);
	}

	/** Puts a player back on the side of the wall they belong on. */
	private void push(ServerWorld world, ServerPlayerEntity player, Vec3d center, double half, boolean shouldBeInside) {
		double dx = player.getX() - center.x;
		double dz = player.getZ() - center.z;
		double x = player.getX();
		double z = player.getZ();
		if (shouldBeInside) {
			double limit = Math.max(0.0, half - MARGIN);
			x = center.x + MathHelper.clamp(dx, -limit, limit);
			z = center.z + MathHelper.clamp(dz, -limit, limit);
		} else {
			// leave through whichever wall is closest
			double depthX = half - Math.abs(dx);
			double depthZ = half - Math.abs(dz);
			if (depthX <= depthZ) {
				x = center.x + (dx >= 0.0 ? 1.0 : -1.0) * (half + MARGIN);
			} else {
				z = center.z + (dz >= 0.0 ? 1.0 : -1.0) * (half + MARGIN);
			}
		}
		player.stopRiding();
		player.setVelocity(0.0, Math.min(0.0, player.getVelocity().y), 0.0);
		player.velocityModified = true;
		player.networkHandler.requestTeleport(x, player.getY(), z, player.getYaw(), player.getPitch());
		Fx.sound(world, player.getPos(), SoundEvents.BLOCK_CHAIN_HIT, 1.5f, 0.6f);
		Fx.sparks(world, player.getPos().add(0.0, 1.0, 0.0), 8, 0.4);
	}

	/** The wall has met: everyone left inside is seized and judged. */
	private void close(ServerWorld world) {
		this.dataTracker.set(CLOSED, true);
		closedAt = world.getTime();

		CharterConfig config = CharterConfig.get();
		Verdict verdict = "KILL".equals(config.dominanceVerdict) ? Verdict.KILL : Verdict.BAN;
		MinecraftServer server = world.getServer();
		ServerPlayerEntity ownerPlayer = owner == null ? null : server.getPlayerManager().getPlayer(owner);

		sentenced.clear();
		for (Map.Entry<UUID, Boolean> entry : inside.entrySet()) {
			if (!entry.getValue().booleanValue()) {
				continue;
			}
			ServerPlayerEntity target = server.getPlayerManager().getPlayer(entry.getKey());
			if (target == null) {
				continue;
			}
			sentenced.add(entry.getKey());
			Judgement.bindAndJudge(target, verdict, ownerPlayer);
		}
		Fx.sound(world, this.getPos(), SoundEvents.BLOCK_BEACON_DEACTIVATE, 8.0f, 0.5f);
		Fx.sound(world, this.getPos(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, 4.0f, 0.6f);
	}

	/** The pillar is spent. */
	public void shatter(ServerWorld world) {
		Vec3d center = this.getPos();
		Fx.sound(world, center, SoundEvents.BLOCK_BEACON_DEACTIVATE, 6.0f, 0.4f);
		Fx.sound(world, center, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 4.0f, 0.5f);
		Fx.sparks(world, center.add(0.0, 1.5, 0.0), 120, 0.9);
		this.discard();
	}

	// ------------------------------------------------------------------ saving

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (owner != null) {
			nbt.putUuid("Owner", owner);
		}
		nbt.putString("OwnerName", ownerName);
		nbt.putLong("Start", getStart());
		nbt.putFloat("StartHalf", getStartHalf());
		nbt.putFloat("EndHalf", getEndHalf());
		nbt.putInt("ArmTicks", getArm());
		nbt.putInt("ShrinkTicks", getShrink());
		nbt.putInt("Up", getUp());
		nbt.putInt("Down", getDown());
		nbt.putBoolean("Closed", isClosed());
		nbt.putLong("ClosedAt", closedAt);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		owner = nbt.containsUuid("Owner") ? nbt.getUuid("Owner") : null;
		ownerName = nbt.getString("OwnerName");
		this.dataTracker.set(START, nbt.contains("Start") ? nbt.getLong("Start") : -1L);
		if (nbt.contains("StartHalf")) {
			this.dataTracker.set(START_HALF, nbt.getFloat("StartHalf"));
			this.dataTracker.set(END_HALF, nbt.getFloat("EndHalf"));
			this.dataTracker.set(ARM, nbt.getInt("ArmTicks"));
			this.dataTracker.set(SHRINK, Math.max(1, nbt.getInt("ShrinkTicks")));
			this.dataTracker.set(UP, nbt.getInt("Up"));
			this.dataTracker.set(DOWN, nbt.getInt("Down"));
		}
		this.dataTracker.set(CLOSED, nbt.getBoolean("Closed"));
		closedAt = nbt.getLong("ClosedAt");
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
