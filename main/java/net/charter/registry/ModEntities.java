package net.charter.registry;

import net.charter.CharterMod;
import net.charter.entity.DivineDominanceEntity;
import net.charter.fx.CharterFxEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModEntities {
	/** One invisible entity that the client draws as a sigil, golden chains, a beam or the blade of Dusk's Epitaph. */
	public static final EntityType<CharterFxEntity> CHARTER_FX = Registry.register(
			Registries.ENTITY_TYPE,
			CharterMod.id("fx"),
			EntityType.Builder.<CharterFxEntity>create(CharterFxEntity::new, SpawnGroup.MISC)
					.setDimensions(0.25f, 0.25f)
					.maxTrackingRange(24)
					.trackingTickInterval(2)
					.makeFireImmune()
					.disableSaving()
					.disableSummon()
					.build("fx"));

	/**
	 * The pillar of Divine Dominance: 1.5 blocks wide, 3 blocks tall, and without a hitbox (nothing can target,
	 * hit, push or collide with it). It stays where it was raised, so it is tracked rarely but from far away.
	 */
	public static final EntityType<DivineDominanceEntity> DIVINE_DOMINANCE = Registry.register(
			Registries.ENTITY_TYPE,
			CharterMod.id("divine_dominance"),
			EntityType.Builder.<DivineDominanceEntity>create(DivineDominanceEntity::new, SpawnGroup.MISC)
					.setDimensions(DivineDominanceEntity.WIDTH, DivineDominanceEntity.HEIGHT)
					.maxTrackingRange(32)
					.trackingTickInterval(20)
					.makeFireImmune()
					.build("divine_dominance"));

	private ModEntities() {
	}

	public static void register() {
		// loading the class registers the entity types
	}
}
