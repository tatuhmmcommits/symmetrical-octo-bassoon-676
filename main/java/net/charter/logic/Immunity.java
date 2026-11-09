package net.charter.logic;

import java.util.List;
import java.util.Optional;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.charter.config.CharterConfig;
import net.charter.item.UnboundArmorItem;
import net.charter.registry.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

/**
 * Who Charter cannot touch. Every way the mod has of judging somebody asks here first.
 *
 * <ul>
 * <li>The Regalia of the Unbound makes its wearer immune to the golden chains and to every verdict: nothing
 * seizes them, and a ban, a kick or an exile does not fall on them ({@link #protects}).</li>
 * <li>A Token of Absolution turns away one verdict, once ({@link #absolve}).</li>
 * </ul>
 */
public final class Immunity {
	private Immunity() {
	}

	/** How many pieces of the Regalia of the Unbound this creature is wearing (0 to 4). */
	public static int regaliaWorn(LivingEntity entity) {
		int worn = 0;
		for (ItemStack stack : entity.getArmorItems()) {
			if (stack.getItem() instanceof UnboundArmorItem) {
				worn++;
			}
		}
		return worn;
	}

	/** True when this creature wears enough of the Regalia of the Unbound to be beyond the reach of Charter. */
	public static boolean protects(LivingEntity entity) {
		CharterConfig config = CharterConfig.get();
		return config.armorGrantsImmunity && regaliaWorn(entity) >= config.armorPiecesRequired;
	}

	/**
	 * Called at the very moment a verdict is about to fall: if the creature wears a Token of Absolution, the
	 * verdict is turned away and the token is used up (unless the config says otherwise).
	 *
	 * @return true when the verdict has been turned away
	 */
	public static boolean absolve(LivingEntity entity) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
		if (component.isEmpty()) {
			return false;
		}
		List<Pair<SlotReference, ItemStack>> tokens = component.get().getEquipped(ModItems.TOKEN_OF_ABSOLUTION);
		if (tokens.isEmpty()) {
			return false;
		}
		if (CharterConfig.get().absolutionTokenConsumed) {
			SlotReference slot = tokens.get(0).getLeft();
			slot.inventory().setStack(slot.index(), ItemStack.EMPTY);
		}
		return true;
	}
}
