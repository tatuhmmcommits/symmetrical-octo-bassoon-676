package net.charter.registry;

import net.charter.CharterMod;
import net.charter.item.AbsolutionTokenItem;
import net.charter.item.ContractItem;
import net.charter.item.DivineDominanceItem;
import net.charter.item.DuskEpitaphItem;
import net.charter.item.LesserDivinityItem;
import net.charter.item.SigilStaffItem;
import net.charter.item.UnboundArmorItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
	/** A blank contract. Using it binds the signer to Charter. */
	public static final Item CHARTER_CONTRACT = register("charter_contract",
			new ContractItem(new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON), false));

	/** A contract that has been signed; it remembers who signed it. */
	public static final Item SIGNED_CONTRACT = register("signed_contract",
			new ContractItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), true));

	/** Three uses of a permanent ban, then it destroys itself in a crater. */
	public static final Item LESSER_DIVINITY = register("lesser_divinity",
			new LesserDivinityItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).fireproof(), false));

	/** The rebuilt, patched Lesser Divinity: it exiles instead of banning. */
	public static final Item FRACTURED_LESSER_DIVINITY = register("fractured_lesser_divinity",
			new LesserDivinityItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE).fireproof(), true));

	/** A shard of the Lesser Divinity, brushed out of the crater it left. */
	public static final Item LESSER_DIVINITY_FRAGMENT = register("lesser_divinity_fragment",
			new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON).fireproof()));

	/** The huge rusted greatsword that bans with one blow. */
	public static final Item DUSK_EPITAPH = register("dusk_epitaph",
			new DuskEpitaphItem(new FabricItemSettings().rarity(Rarity.EPIC).fireproof()));

	/** A tireless staff that draws small sigils: bind, kick, ban or exile whoever stands in them. */
	public static final Item SIGIL_STAFF = register("sigil_staff",
			new SigilStaffItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

	/** The tablet that raises the pillar of Divine Dominance and its shrinking golden walls. */
	public static final Item DIVINE_DOMINANCE = register("divine_dominance",
			new DivineDominanceItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

	/** Regalia of the Unbound: corroded netherite in golden chains. Worn as a set it puts its wearer beyond Charter. */
	public static final Item UNBOUND_HELMET = register("unbound_helmet",
			new UnboundArmorItem(ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.EPIC).fireproof()));
	public static final Item UNBOUND_CHESTPLATE = register("unbound_chestplate",
			new UnboundArmorItem(ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.EPIC).fireproof()));
	public static final Item UNBOUND_LEGGINGS = register("unbound_leggings",
			new UnboundArmorItem(ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.EPIC).fireproof()));
	public static final Item UNBOUND_BOOTS = register("unbound_boots",
			new UnboundArmorItem(ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.EPIC).fireproof()));

	/** A pendant (necklace slot) that turns away one verdict of Charter and crumbles. */
	public static final Item TOKEN_OF_ABSOLUTION = register("token_of_absolution",
			new AbsolutionTokenItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE).fireproof()));

	private ModItems() {
	}

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, CharterMod.id(name), item);
	}

	public static void register() {
		// loading the class registers the items
	}
}
