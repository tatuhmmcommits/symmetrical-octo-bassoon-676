package net.charter.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.charter.config.CharterConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * Dusk's Epitaph: an enormous rusted greatsword. Against a creature that is down to its last half heart, a fully
 * charged blow is not damage at all: golden chains seize the target, the blade turns into a beam of light over the
 * wielder's head and is brought down on it. A player is banned; any other creature is slain. Against anything
 * healthier it is only a slow, heavy blade, and a blow that would kill leaves the creature on half a heart so that
 * the next full blow can bring the chains down. The blow itself is handled in {@code CharterRuntime}, before any
 * damage is dealt.
 */
public class DuskEpitaphItem extends SwordItem implements CharterWeapon {
	public DuskEpitaphItem(Settings settings) {
		super(EpitaphMaterial.INSTANCE, 6, -3.0f, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CharterConfig config = CharterConfig.get();
		if (config.epitaphHalfHeartOnly) {
			tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.gated.1").formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.gated.2").formatted(Formatting.GRAY));
			if (config.epitaphLeavesHalfHeart) {
				tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.spares").formatted(Formatting.GOLD));
			}
		} else {
			tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.1").formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.2").formatted(Formatting.GRAY));
		}
		if (config.weaponsRequireContract) {
			tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.contract").formatted(Formatting.GOLD));
		}
		tooltip.add(Text.translatable("tooltip.charter.dusk_epitaph.3").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
	}
}
