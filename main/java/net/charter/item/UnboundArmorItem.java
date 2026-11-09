package net.charter.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * One piece of the Regalia of the Unbound. Wearing the whole set (the number of pieces is configurable) puts the
 * wearer beyond the reach of Charter: the golden chains cannot seize them and no verdict can fall on them, whether
 * it is a ban, a kick or an exile. The worn-piece counter in the tooltip is added on the client.
 */
public class UnboundArmorItem extends ArmorItem {
	public UnboundArmorItem(Type type, Settings settings) {
		super(UnboundArmorMaterial.INSTANCE, type, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("tooltip.charter.regalia.1").formatted(Formatting.GOLD));
		tooltip.add(Text.translatable("tooltip.charter.regalia.2").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("tooltip.charter.regalia.3").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
	}
}
