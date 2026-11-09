package net.charter.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.charter.config.CharterConfig;
import net.charter.entity.DivineDominanceEntity;
import net.charter.logic.Contracts;
import net.charter.registry.ModEntities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Divine Dominance, as an item. In the inventory it is a tablet; used on the ground it raises the pillar of
 * Divine Dominance (an entity, see {@link DivineDominanceEntity}) where the placement would go, facing the way the
 * player faces.
 */
public class DivineDominanceItem extends Item implements CharterWeapon {
	public DivineDominanceItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!(world instanceof ServerWorld)) {
			return ActionResult.SUCCESS;
		}
		ServerWorld serverWorld = (ServerWorld) world;
		PlayerEntity player = context.getPlayer();
		if (player != null && CharterConfig.get().weaponsRequireContract) {
			MinecraftServer server = world.getServer();
			if (server != null && !Contracts.isBound(server, player.getUuid())) {
				player.sendMessage(Text.literal("The tablet does not answer to you.").formatted(Formatting.GRAY, Formatting.ITALIC), true);
				return ActionResult.FAIL;
			}
		}

		BlockPos pos = new ItemPlacementContext(context).getBlockPos();
		if (pos.getY() < world.getBottomY() || pos.getY() > world.getTopY() - 3) {
			return ActionResult.FAIL;
		}
		Vec3d base = Vec3d.ofBottomCenter(pos);
		// the pillar is the same from all four sides: it simply turns to the nearest quarter of the player's view
		float yaw = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0f) + 45.0f) / 90.0f) * 90.0f;

		DivineDominanceEntity pillar = new DivineDominanceEntity(ModEntities.DIVINE_DOMINANCE, serverWorld);
		pillar.refreshPositionAndAngles(base.x, base.y, base.z, yaw, 0.0f);
		if (!serverWorld.spawnEntity(pillar)) {
			return ActionResult.FAIL;
		}
		pillar.arm(serverWorld, player);

		if (player == null || !player.getAbilities().creativeMode) {
			context.getStack().decrement(1);
		}
		return ActionResult.CONSUME;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CharterConfig config = CharterConfig.get();
		tooltip.add(Text.translatable("tooltip.charter.divine_dominance.1").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("tooltip.charter.divine_dominance.2", (int) Math.round(config.dominanceStartRadius * 2.0)).formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("tooltip.charter.divine_dominance.3").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("tooltip.charter.divine_dominance.4").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
	}
}
