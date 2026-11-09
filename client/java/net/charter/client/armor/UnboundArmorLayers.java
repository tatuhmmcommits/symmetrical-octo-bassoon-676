package net.charter.client.armor;

import net.charter.CharterMod;
import net.charter.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

/** The four model layers of the Regalia of the Unbound, and the registration that connects them to the items. */
public final class UnboundArmorLayers {
	public static final EntityModelLayer HELMET = layer("unbound_helmet");
	public static final EntityModelLayer CHESTPLATE = layer("unbound_chestplate");
	public static final EntityModelLayer LEGGINGS = layer("unbound_leggings");
	public static final EntityModelLayer BOOTS = layer("unbound_boots");

	private UnboundArmorLayers() {
	}

	private static EntityModelLayer layer(String name) {
		return new EntityModelLayer(CharterMod.id(name), "main");
	}

	public static void register() {
		EntityModelLayerRegistry.registerModelLayer(HELMET, UnboundArmorModel::helmet);
		EntityModelLayerRegistry.registerModelLayer(CHESTPLATE, UnboundArmorModel::chestplate);
		EntityModelLayerRegistry.registerModelLayer(LEGGINGS, UnboundArmorModel::leggings);
		EntityModelLayerRegistry.registerModelLayer(BOOTS, UnboundArmorModel::boots);

		ArmorRenderer.register(new UnboundArmorRenderer(),
				ModItems.UNBOUND_HELMET, ModItems.UNBOUND_CHESTPLATE, ModItems.UNBOUND_LEGGINGS, ModItems.UNBOUND_BOOTS);
	}
}
