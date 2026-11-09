package net.charter.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/**
 * What the Regalia of the Unbound is made of: corroded netherite plate bound in gold. About as protective as
 * netherite itself; what sets it apart is {@link net.charter.logic.Immunity}.
 */
public final class UnboundArmorMaterial implements ArmorMaterial {
	public static final UnboundArmorMaterial INSTANCE = new UnboundArmorMaterial();

	/**
	 * The "namespace:name" form tells Fabric where the flat fallback textures live
	 * ({@code charter:textures/models/armor/unbound_layer_1.png} and {@code _layer_2.png}); the 3D model has its own.
	 */
	public static final String NAME = "charter:unbound";

	private static final int DURABILITY_FACTOR = 42;

	private UnboundArmorMaterial() {
	}

	@Override
	public int getDurability(ArmorItem.Type type) {
		switch (type) {
			case HELMET:
				return 11 * DURABILITY_FACTOR;
			case CHESTPLATE:
				return 16 * DURABILITY_FACTOR;
			case LEGGINGS:
				return 15 * DURABILITY_FACTOR;
			default:
				return 13 * DURABILITY_FACTOR;
		}
	}

	@Override
	public int getProtection(ArmorItem.Type type) {
		switch (type) {
			case HELMET:
				return 3;
			case CHESTPLATE:
				return 8;
			case LEGGINGS:
				return 6;
			default:
				return 3;
		}
	}

	@Override
	public int getEnchantability() {
		return 18;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(Items.NETHERITE_INGOT);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public float getToughness() {
		return 3.5f;
	}

	@Override
	public float getKnockbackResistance() {
		return 0.1f;
	}
}
