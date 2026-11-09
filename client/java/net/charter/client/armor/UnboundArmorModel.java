package net.charter.client.armor;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

/**
 * The geometry of the Regalia of the Unbound, one model per piece. Every box points into the texture atlas
 * {@code assets/charter/textures/models/armor/unbound.png}, whose layout it was generated together with:
 * if you repaint the atlas, keep the boxes where they are; if you move a box, move its square of pixels too.
 * Boxes with a "{@code mirrored}" flag are the left-hand twins of right-hand boxes and share their pixels.
 */
public final class UnboundArmorModel {
	/** Size of the texture atlas {@code unbound.png} that every UV below points into. */
	public static final int TEXTURE_WIDTH = 128;
	public static final int TEXTURE_HEIGHT = 48;

	private UnboundArmorModel() {
	}

	/** The seven parts every biped armour model needs; the pieces add their boxes to them. */
	private static ModelData skeleton(ModelPartData[] parts) {
		ModelData data = new ModelData();
		ModelPartData root = data.getRoot();
		parts[0] = root.addChild("head", ModelPartBuilder.create(), ModelTransform.NONE);
		parts[1] = root.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
		parts[2] = root.addChild("body", ModelPartBuilder.create(), ModelTransform.NONE);
		parts[3] = root.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
		parts[4] = root.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
		parts[5] = root.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));
		parts[6] = root.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
		return data;
	}

	/** Helm of the Unbound: 14 boxes. */
	public static TexturedModelData helmet() {
		ModelPartData[] p = new ModelPartData[7];
		ModelData data = skeleton(p);
		p[0].addChild("helm_shell", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.9F)), ModelTransform.NONE);
		p[0].addChild("helm_band", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -6.4F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(1.35F, 0.0F, 1.35F)), ModelTransform.NONE);
		p[0].addChild("helm_gem", ModelPartBuilder.create().uv(26, 38).cuboid(-1.5F, -6.9F, -5.95F, 3.0F, 3.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[0].addChild("horn1", ModelPartBuilder.create().uv(92, 26).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, Dilation.NONE), ModelTransform.of(-4.9F, -6.0F, -1.0F, 0.151F, -1.153F, 0.0F));
		p[0].addChild("horn2", ModelPartBuilder.create().uv(74, 33).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, Dilation.NONE), ModelTransform.of(-7.61F, -6.452F, 0.205F, 0.467F, -0.594F, 0.0F));
		p[0].addChild("horn3", ModelPartBuilder.create().uv(84, 33).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, Dilation.NONE), ModelTransform.of(-9.11F, -7.802F, 2.424F, 0.412F, -0.109F, 0.0F));
		p[0].addChild("horn4", ModelPartBuilder.create().uv(68, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, Dilation.NONE), ModelTransform.of(-9.41F, -9.003F, 5.157F, 0.252F, 0.207F, 0.0F));
		p[0].addChild("horn1_l", ModelPartBuilder.create().mirrored(true).uv(92, 26).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, Dilation.NONE), ModelTransform.of(4.9F, -6.0F, -1.0F, 0.151F, 1.153F, 0.0F));
		p[0].addChild("horn2_l", ModelPartBuilder.create().mirrored(true).uv(74, 33).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, Dilation.NONE), ModelTransform.of(7.61F, -6.452F, 0.205F, 0.467F, 0.594F, 0.0F));
		p[0].addChild("horn3_l", ModelPartBuilder.create().mirrored(true).uv(84, 33).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, Dilation.NONE), ModelTransform.of(9.11F, -7.802F, 2.424F, 0.412F, 0.109F, 0.0F));
		p[0].addChild("horn4_l", ModelPartBuilder.create().mirrored(true).uv(68, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, Dilation.NONE), ModelTransform.of(9.41F, -9.003F, 5.157F, 0.252F, -0.207F, 0.0F));
		p[0].addChild("helm_tail1", ModelPartBuilder.create().uv(50, 26).cuboid(-3.0F, 0.0F, 0.0F, 2.0F, 6.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, -3.5F, 4.9F, 0.14F, 0.0F, 0.0F));
		p[0].addChild("helm_tail2", ModelPartBuilder.create().uv(86, 16).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 8.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, -3.5F, 4.9F, 0.17F, 0.0F, 0.0F));
		p[0].addChild("helm_tail3", ModelPartBuilder.create().uv(116, 26).cuboid(1.0F, 0.0F, 0.0F, 2.0F, 5.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, -3.5F, 4.9F, 0.2F, 0.0F, 0.0F));
		return TexturedModelData.of(data, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}

	/** Cuirass of the Unbound: 30 boxes. */
	public static TexturedModelData chestplate() {
		ModelPartData[] p = new ModelPartData[7];
		ModelData data = skeleton(p);
		p[2].addChild("chest_shell", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.9F, 0.25F, 0.9F)), ModelTransform.NONE);
		p[2].addChild("chest_plate", ModelPartBuilder.create().uv(92, 16).cuboid(-3.0F, 1.0F, -3.9F, 6.0F, 7.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[2].addChild("chest_gem", ModelPartBuilder.create().uv(18, 38).cuboid(-1.5F, 2.0F, -4.55F, 3.0F, 3.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[2].addChild("chest_sash_a", ModelPartBuilder.create().uv(88, 0).cuboid(-1.5F, -7.0F, -0.5F, 3.0F, 14.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, 6.0F, -3.55F, 0.0F, 0.0F, 0.62F));
		p[2].addChild("chest_sash_b", ModelPartBuilder.create().uv(96, 0).cuboid(-1.5F, -7.0F, -0.5F, 3.0F, 14.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, 6.0F, -3.72F, 0.0F, 0.0F, -0.62F));
		p[2].addChild("chest_backplate", ModelPartBuilder.create().uv(72, 16).cuboid(-3.0F, 1.5F, 2.9F, 6.0F, 8.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[2].addChild("chest_collar", ModelPartBuilder.create().uv(56, 26).cuboid(-3.0F, -1.8F, -2.0F, 6.0F, 2.0F, 4.0F, new Dilation(1.05F, 0.0F, 1.05F)), ModelTransform.NONE);
		p[2].addChild("chest_cape", ModelPartBuilder.create().uv(104, 0).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 11.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, 0.5F, 3.95F, 0.1F, 0.0F, 0.0F));
		p[3].addChild("arm_shell", ModelPartBuilder.create().uv(56, 0).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.75F, 0.05F, 0.75F)), ModelTransform.NONE);
		p[3].addChild("pauld_cap", ModelPartBuilder.create().uv(32, 16).cuboid(-4.5F, -4.4F, -3.0F, 6.0F, 3.0F, 6.0F, Dilation.NONE), ModelTransform.NONE);
		p[3].addChild("pauld_top", ModelPartBuilder.create().uv(24, 26).cuboid(-3.5F, -6.2F, -2.5F, 4.0F, 2.0F, 5.0F, Dilation.NONE), ModelTransform.NONE);
		p[3].addChild("pauld_spike_a1", ModelPartBuilder.create().uv(42, 38).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, Dilation.NONE), ModelTransform.of(-4.3F, -5.8F, -1.3F, 0.899F, -1.571F, 0.0F));
		p[3].addChild("pauld_spike_a2", ModelPartBuilder.create().uv(74, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, Dilation.NONE), ModelTransform.of(-5.544F, -7.366F, -1.3F, 1.103F, -1.571F, 0.0F));
		p[3].addChild("pauld_spike_b1", ModelPartBuilder.create().uv(50, 38).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, Dilation.NONE), ModelTransform.of(-4.3F, -5.8F, 1.3F, 0.899F, -1.571F, 0.0F));
		p[3].addChild("pauld_spike_b2", ModelPartBuilder.create().uv(80, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, Dilation.NONE), ModelTransform.of(-5.544F, -7.366F, 1.3F, 1.103F, -1.571F, 0.0F));
		p[3].addChild("shackle", ModelPartBuilder.create().uv(76, 26).cuboid(-3.0F, 6.4F, -2.0F, 4.0F, 2.0F, 4.0F, new Dilation(1.1F, 0.0F, 1.1F)), ModelTransform.NONE);
		p[3].addChild("arm_link1", ModelPartBuilder.create().uv(94, 33).cuboid(-2.5F, 9.0F, -0.5F, 3.0F, 4.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[3].addChild("arm_link2", ModelPartBuilder.create().uv(42, 26).cuboid(-1.5F, 12.0F, -1.5F, 1.0F, 4.0F, 3.0F, Dilation.NONE), ModelTransform.NONE);
		p[3].addChild("arm_link3", ModelPartBuilder.create().uv(102, 33).cuboid(-2.5F, 15.0F, -0.5F, 3.0F, 4.0F, 1.0F, Dilation.NONE), ModelTransform.of(-1.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.35F));
		p[4].addChild("arm_shell_l", ModelPartBuilder.create().mirrored(true).uv(56, 0).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.75F, 0.05F, 0.75F)), ModelTransform.NONE);
		p[4].addChild("pauld_cap_l", ModelPartBuilder.create().mirrored(true).uv(32, 16).cuboid(-1.5F, -4.4F, -3.0F, 6.0F, 3.0F, 6.0F, Dilation.NONE), ModelTransform.NONE);
		p[4].addChild("pauld_top_l", ModelPartBuilder.create().mirrored(true).uv(24, 26).cuboid(-0.5F, -6.2F, -2.5F, 4.0F, 2.0F, 5.0F, Dilation.NONE), ModelTransform.NONE);
		p[4].addChild("shackle_l", ModelPartBuilder.create().mirrored(true).uv(76, 26).cuboid(-1.0F, 6.4F, -2.0F, 4.0F, 2.0F, 4.0F, new Dilation(1.1F, 0.0F, 1.1F)), ModelTransform.NONE);
		p[4].addChild("arm_link1_l", ModelPartBuilder.create().mirrored(true).uv(94, 33).cuboid(-0.5F, 9.0F, -0.5F, 3.0F, 4.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[4].addChild("arm_link2_l", ModelPartBuilder.create().mirrored(true).uv(42, 26).cuboid(0.5F, 12.0F, -1.5F, 1.0F, 4.0F, 3.0F, Dilation.NONE), ModelTransform.NONE);
		p[4].addChild("arm_link3_l", ModelPartBuilder.create().mirrored(true).uv(102, 33).cuboid(-0.5F, 15.0F, -0.5F, 3.0F, 4.0F, 1.0F, Dilation.NONE), ModelTransform.of(1.0F, 15.0F, 0.0F, 0.0F, 0.0F, -0.35F));
		p[4].addChild("pauld_spike_a1_l", ModelPartBuilder.create().mirrored(true).uv(42, 38).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, Dilation.NONE), ModelTransform.of(4.3F, -5.8F, -1.3F, 0.899F, 1.571F, 0.0F));
		p[4].addChild("pauld_spike_a2_l", ModelPartBuilder.create().mirrored(true).uv(74, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, Dilation.NONE), ModelTransform.of(5.544F, -7.366F, -1.3F, 1.103F, 1.571F, 0.0F));
		p[4].addChild("pauld_spike_b1_l", ModelPartBuilder.create().mirrored(true).uv(50, 38).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, Dilation.NONE), ModelTransform.of(4.3F, -5.8F, 1.3F, 0.899F, 1.571F, 0.0F));
		p[4].addChild("pauld_spike_b2_l", ModelPartBuilder.create().mirrored(true).uv(80, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, Dilation.NONE), ModelTransform.of(5.544F, -7.366F, 1.3F, 1.103F, 1.571F, 0.0F));
		return TexturedModelData.of(data, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}

	/** Greaves of the Unbound: 14 boxes. */
	public static TexturedModelData leggings() {
		ModelPartData[] p = new ModelPartData[7];
		ModelData data = skeleton(p);
		p[2].addChild("belt", ModelPartBuilder.create().uv(0, 26).cuboid(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, new Dilation(1.2F, 0.0F, 1.2F)), ModelTransform.NONE);
		p[2].addChild("belt_buckle", ModelPartBuilder.create().uv(0, 38).cuboid(-2.0F, 9.0F, -4.2F, 4.0F, 3.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[5].addChild("leg_shell", ModelPartBuilder.create().uv(72, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.55F, 0.0F, 0.55F)), ModelTransform.NONE);
		p[5].addChild("thigh_trim", ModelPartBuilder.create().uv(32, 33).cuboid(-2.0F, 0.4F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.8F, 0.0F, 0.8F)), ModelTransform.NONE);
		p[5].addChild("tasset", ModelPartBuilder.create().uv(104, 26).cuboid(-2.5F, 0.0F, -0.5F, 5.0F, 5.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, -1.0F, -3.2F, -0.1F, 0.0F, 0.0F));
		p[5].addChild("knee_plate", ModelPartBuilder.create().uv(48, 33).cuboid(-2.5F, 4.5F, -4.3F, 5.0F, 3.0F, 2.0F, Dilation.NONE), ModelTransform.NONE);
		p[5].addChild("knee_spike1", ModelPartBuilder.create().uv(34, 38).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, Dilation.NONE), ModelTransform.of(0.0F, 6.0F, -4.2F, -0.05F, 3.142F, 0.0F));
		p[5].addChild("knee_spike2", ModelPartBuilder.create().uv(86, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, 6.1F, -6.198F, -0.05F, 3.142F, 0.0F));
		p[6].addChild("leg_shell_l", ModelPartBuilder.create().mirrored(true).uv(72, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.55F, 0.0F, 0.55F)), ModelTransform.NONE);
		p[6].addChild("thigh_trim_l", ModelPartBuilder.create().mirrored(true).uv(32, 33).cuboid(-2.0F, 0.4F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.8F, 0.0F, 0.8F)), ModelTransform.NONE);
		p[6].addChild("tasset_l", ModelPartBuilder.create().mirrored(true).uv(104, 26).cuboid(-2.5F, 0.0F, -0.5F, 5.0F, 5.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, -1.0F, -3.2F, -0.1F, 0.0F, 0.0F));
		p[6].addChild("knee_plate_l", ModelPartBuilder.create().mirrored(true).uv(48, 33).cuboid(-2.5F, 4.5F, -4.3F, 5.0F, 3.0F, 2.0F, Dilation.NONE), ModelTransform.NONE);
		p[6].addChild("knee_spike1_l", ModelPartBuilder.create().mirrored(true).uv(34, 38).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, Dilation.NONE), ModelTransform.of(0.0F, 6.0F, -4.2F, -0.05F, -3.142F, 0.0F));
		p[6].addChild("knee_spike2_l", ModelPartBuilder.create().mirrored(true).uv(86, 38).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, Dilation.NONE), ModelTransform.of(0.0F, 6.1F, -6.198F, -0.05F, -3.142F, 0.0F));
		return TexturedModelData.of(data, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}

	/** Sabatons of the Unbound: 14 boxes. */
	public static TexturedModelData boots() {
		ModelPartData[] p = new ModelPartData[7];
		ModelData data = skeleton(p);
		p[5].addChild("boot_shaft", ModelPartBuilder.create().uv(56, 16).cuboid(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.9F, 0.0F, 0.9F)), ModelTransform.NONE);
		p[5].addChild("boot_cuff", ModelPartBuilder.create().uv(0, 33).cuboid(-2.0F, 7.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(1.25F, 0.0F, 1.25F)), ModelTransform.NONE);
		p[5].addChild("boot_shackle", ModelPartBuilder.create().uv(16, 33).cuboid(-2.0F, 10.2F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(1.3F, 0.0F, 1.3F)), ModelTransform.NONE);
		p[5].addChild("boot_toe", ModelPartBuilder.create().uv(62, 33).cuboid(-2.0F, 9.0F, -4.6F, 4.0F, 3.0F, 2.0F, Dilation.NONE), ModelTransform.NONE);
		p[5].addChild("boot_heel", ModelPartBuilder.create().uv(58, 38).cuboid(-2.0F, 10.0F, 2.95F, 4.0F, 2.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[5].addChild("boot_link1", ModelPartBuilder.create().uv(110, 33).cuboid(-1.5F, 11.0F, 3.6F, 3.0F, 1.0F, 3.0F, Dilation.NONE), ModelTransform.NONE);
		p[5].addChild("boot_link2", ModelPartBuilder.create().uv(10, 38).cuboid(-0.5F, 11.0F, 6.2F, 1.0F, 1.0F, 3.0F, Dilation.NONE), ModelTransform.NONE);
		p[6].addChild("boot_shaft_l", ModelPartBuilder.create().mirrored(true).uv(56, 16).cuboid(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.9F, 0.0F, 0.9F)), ModelTransform.NONE);
		p[6].addChild("boot_cuff_l", ModelPartBuilder.create().mirrored(true).uv(0, 33).cuboid(-2.0F, 7.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(1.25F, 0.0F, 1.25F)), ModelTransform.NONE);
		p[6].addChild("boot_shackle_l", ModelPartBuilder.create().mirrored(true).uv(16, 33).cuboid(-2.0F, 10.2F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(1.3F, 0.0F, 1.3F)), ModelTransform.NONE);
		p[6].addChild("boot_toe_l", ModelPartBuilder.create().mirrored(true).uv(62, 33).cuboid(-2.0F, 9.0F, -4.6F, 4.0F, 3.0F, 2.0F, Dilation.NONE), ModelTransform.NONE);
		p[6].addChild("boot_heel_l", ModelPartBuilder.create().mirrored(true).uv(58, 38).cuboid(-2.0F, 10.0F, 2.95F, 4.0F, 2.0F, 1.0F, Dilation.NONE), ModelTransform.NONE);
		p[6].addChild("boot_link1_l", ModelPartBuilder.create().mirrored(true).uv(110, 33).cuboid(-1.5F, 11.0F, 3.6F, 3.0F, 1.0F, 3.0F, Dilation.NONE), ModelTransform.NONE);
		p[6].addChild("boot_link2_l", ModelPartBuilder.create().mirrored(true).uv(10, 38).cuboid(-0.5F, 11.0F, 6.2F, 1.0F, 1.0F, 3.0F, Dilation.NONE), ModelTransform.NONE);
		return TexturedModelData.of(data, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
}
