package net.charter.client.armor;

import net.charter.body.BodyMirror;
import net.charter.body.BodyPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/** Armour is not drawn on limbs that are gone: hides the parts of an armour model that the wearer no longer has. */
public final class LostParts {
	private LostParts() {
	}

	public static void hide(BipedEntityModel<?> model, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return;
		}
		int mask = BodyMirror.mask(entity.getUuid());
		if (mask == 0) {
			return;
		}
		if (BodyPart.gone(mask, BodyPart.LEFT_ARM)) {
			model.leftArm.visible = false;
		}
		if (BodyPart.gone(mask, BodyPart.RIGHT_ARM)) {
			model.rightArm.visible = false;
		}
		if (BodyPart.gone(mask, BodyPart.LEFT_LEG)) {
			model.leftLeg.visible = false;
		}
		if (BodyPart.gone(mask, BodyPart.RIGHT_LEG)) {
			model.rightLeg.visible = false;
		}
		if (BodyPart.gone(mask, BodyPart.BODY)) {
			model.body.visible = false;
		}
	}
}
