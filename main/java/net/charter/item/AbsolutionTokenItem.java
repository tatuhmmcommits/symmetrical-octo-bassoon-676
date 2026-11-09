package net.charter.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * A pendant of cream tablet-stone and gold, worn in the necklace slot. The first time a verdict of Charter is
 * about to fall on its wearer, it is turned away and the pendant crumbles (see {@link net.charter.logic.Immunity#absolve}).
 * It does not stop the chains from closing around its wearer, only the sentence.
 */
public class AbsolutionTokenItem extends TrinketItem {
	public AbsolutionTokenItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("tooltip.charter.token_of_absolution.1").formatted(Formatting.GOLD));
		tooltip.add(Text.translatable("tooltip.charter.token_of_absolution.2").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("tooltip.charter.token_of_absolution.3").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
	}
}
