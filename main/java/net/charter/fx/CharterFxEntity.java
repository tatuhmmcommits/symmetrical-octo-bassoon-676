package net.charter.fx;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

/**
 * The single invisible entity behind every golden visual of the mod (apart from the pillar of Divine Dominance,
 * which is an entity of its own). The server only fills in the tracked values; the client renderer turns them
 * into the sigil, chains, beam or blade. All animation is derived from the world time, so nothing has to be
 * synced every tick.
 */
public class CharterFxEntity extends Entity {
	/** A flat golden charter sigil lying on the ground. */
	public static final int SIGIL = 0;
	/** A column of golden light from the ground to the sky. */
	public static final int BEAM = 1;
	/** Golden chains wound around the followed entity. */
	public static final int CHAINS = 2;
	/** The blade of Dusk's Epitaph turning into a beam of light and coming down on the chained target. */
	public static final int BLADE = 4;

	/** The sigil of Charter itself, drawn under everything that Dusk's Epitaph, the Lesser Divinity and Divine Dominance judge. */
	public static final int STYLE_CHARTER = 0;
	/** The ornate magic circle of the Sigil Staff. */
	public static final int STYLE_STAFF = 1;

	/**
	 * Set by the client at start-up: told about every blade effect once per tick, so that the player model of the
	 * wielder can raise its arms in time with the beam. Never set on a dedicated server.
	 */
	public static volatile Consumer<CharterFxEntity> bladeHook;

	private static final TrackedData<Integer> KIND = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> STYLE = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> START = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> LIFETIME = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> ARM = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> SHRINK = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> FOLLOW = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> SIZE = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> SIZE_END = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> SOURCE = DataTracker.registerData(CharterFxEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public CharterFxEntity(EntityType<?> type, World world) {
		super(type, world);
		this.noClip = true;
		this.setInvulnerable(true);
		this.setNoGravity(true);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(KIND, SIGIL);
		this.dataTracker.startTracking(STYLE, STYLE_CHARTER);
		this.dataTracker.startTracking(START, 0);
		this.dataTracker.startTracking(LIFETIME, -1);
		this.dataTracker.startTracking(ARM, 0);
		this.dataTracker.startTracking(SHRINK, 1);
		this.dataTracker.startTracking(FOLLOW, 0);
		this.dataTracker.startTracking(SIZE, 4.0f);
		this.dataTracker.startTracking(SIZE_END, 1.0f);
		this.dataTracker.startTracking(SOURCE, 0);
	}

	// ------------------------------------------------------------------ configuration (server side)
	public CharterFxEntity kind(int kind) {
		this.dataTracker.set(KIND, kind);
		return this;
	}

	/** Which sigil is drawn: {@link #STYLE_CHARTER} or {@link #STYLE_STAFF}. */
	public CharterFxEntity style(int style) {
		this.dataTracker.set(STYLE, style);
		return this;
	}

	/** World time (in ticks) at which the effect begins; every animation counts from here. */
	public CharterFxEntity startAt(long worldTime) {
		this.dataTracker.set(START, (int) worldTime);
		return this;
	}

	/** Ticks the entity lives for; negative means until it is removed by its owner. */
	public CharterFxEntity lifetime(int ticks) {
		this.dataTracker.set(LIFETIME, ticks);
		return this;
	}

	public CharterFxEntity size(float size) {
		this.dataTracker.set(SIZE, size);
		return this;
	}

	public CharterFxEntity follow(Entity entity) {
		this.dataTracker.set(FOLLOW, entity == null ? 0 : entity.getId());
		return this;
	}

	/** How much wider and taller than a player the followed creature is: the chains are drawn that much bigger. */
	public CharterFxEntity scale(float horizontal, float vertical) {
		this.dataTracker.set(SIZE, horizontal);
		this.dataTracker.set(SIZE_END, vertical);
		return this;
	}

	/** The blade of the Epitaph: who swings it, how long it rises and falls, and how tall the target is. */
	public CharterFxEntity blade(Entity wielder, int chargeTicks, int slamTicks, float targetHeight) {
		this.dataTracker.set(SOURCE, wielder == null ? 0 : wielder.getId());
		this.dataTracker.set(ARM, chargeTicks);
		this.dataTracker.set(SHRINK, Math.max(1, slamTicks));
		this.dataTracker.set(SIZE, targetHeight);
		return this;
	}

	// ------------------------------------------------------------------ readers (both sides)
	public int getKind() {
		return this.dataTracker.get(KIND);
	}

	public int getStyle() {
		return this.dataTracker.get(STYLE);
	}

	public int getStart() {
		return this.dataTracker.get(START);
	}

	public int getLifetime() {
		return this.dataTracker.get(LIFETIME);
	}

	public float getSize() {
		return this.dataTracker.get(SIZE);
	}

	public float getSizeEnd() {
		return this.dataTracker.get(SIZE_END);
	}

	public int getArm() {
		return this.dataTracker.get(ARM);
	}

	public int getShrink() {
		return this.dataTracker.get(SHRINK);
	}

	public int getFollowId() {
		return this.dataTracker.get(FOLLOW);
	}

	/** The entity id of the wielder whose blade this is (kind {@link #BLADE}). */
	public int getSource() {
		return this.dataTracker.get(SOURCE);
	}

	/** Ticks since the effect began, with the fraction supplied by the renderer. */
	public float age(float tickDelta) {
		return (float) (this.getWorld().getTime() - (long) getStart()) + tickDelta;
	}

	// ------------------------------------------------------------------ life cycle
	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			Consumer<CharterFxEntity> hook = bladeHook;
			if (hook != null && getKind() == BLADE) {
				hook.accept(this);
			}
			return;
		}
		int life = getLifetime();
		if (life >= 0 && this.getWorld().getTime() - (long) getStart() >= (long) life) {
			this.discard();
			return;
		}
		int followId = getFollowId();
		if (followId != 0) {
			Entity target = this.getWorld().getEntityById(followId);
			if (target == null || target.isRemoved()) {
				this.discard();
				return;
			}
			this.setPosition(target.getX(), target.getY(), target.getZ());
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		// never saved
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		// never saved
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
