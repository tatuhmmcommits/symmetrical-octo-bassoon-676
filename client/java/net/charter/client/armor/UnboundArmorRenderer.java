package net.charter.client.armor;

import java.util.EnumMap;
import java.util.Map;

import net.charter.CharterMod;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Draws the Regalia of the Unbound with its own 3D models instead of the flat vanilla armour layer: plates, horns,
 * pauldrons, chains and a cape, all built from boxes in {@link UnboundArmorModel}. Each piece has a model of its own;
 * they follow the pose of the wearer by copying it from the model that vanilla was about to use.
 *
 * <p>A second pass draws the glowing pixels (the signet gems, the visor, the buckle) at full brightness.
 */
public final class UnboundArmorRenderer implements ArmorRenderer {
	private static final Identifier TEXTURE = CharterMod.id("textures/models/armor/unbound.png");
	private static final Identifier GLOW = CharterMod.id("textures/models/armor/unbound_glow.png");

	private final Map<EquipmentSlot, BipedEntityModel<LivingEntity>> models = new EnumMap<>(EquipmentSlot.class);

	private static EntityModelLayer layerOf(EquipmentSlot slot) {
		switch (slot) {
			case HEAD:
				return UnboundArmorLayers.HELMET;
			case CHEST:
				return UnboundArmorLayers.CHESTPLATE;
			case LEGS:
				return UnboundArmorLayers.LEGGINGS;
			case FEET:
				return UnboundArmorLayers.BOOTS;
			default:
				return null;
		}
	}

	private BipedEntityModel<LivingEntity> modelOf(EquipmentSlot slot) {
		BipedEntityModel<LivingEntity> model = models.get(slot);
		if (model == null) {
			EntityModelLayer layer = layerOf(slot);
			if (layer == null) {
				return null;
			}
			model = new BipedEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(layer));
			models.put(slot, model);
		}
		return model;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
		BipedEntityModel<LivingEntity> model = modelOf(slot);
		if (model == null) {
			return;
		}
		contextModel.copyBipedStateTo(model);
		model.setVisible(true);
		LostParts.hide(model, entity);

		ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, TEXTURE);
		// the glowing pixels, whatever the light around the wearer
		model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(GLOW)),
				LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
