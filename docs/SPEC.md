# Design notes

These notes explain how the mod is put together, where each mechanic comes from, and how to check it in game.

## 1. Provenance

* **Sources.** Five ContentSMP video transcripts supplied with the commission (referred to below by the first
  eight characters of their file names) and the ContentSMP wiki page for Charter
  (`content-smp.fandom.com/wiki/Charter`, which the commissioner said is not fully up to date with the lore).
  No video could be watched; only the transcripts were available.
* **Clean room.** The original Charter is a private project. None of its code, assets or data was seen. Every name,
  number and texture here was made for this project.
* **Confidence labels** used below: **[seen]** stated in a transcript, **[wiki]** from the wiki only,
  **[guess]** filled in by the author because the sources are silent.

## 2. What the transcripts establish

| Fact | Where |
| --- | --- |
| Charter makes golden/yellow beams and chains, and is "the only mod on the server that can permanently ban people" | 9943c364 |
| A banned player is fully removed from the server | 9943c364 |
| Mouthpiece (a spirit) runs Charter and sells powerful items through deals; one of them is the Lesser Divinity | e9870b00, d2c25a8d, f7ce434e |
| A previous holder of the contract was "trapped in golden chains and permanently disappeared in a ravaging explosion" | f7ce434e |
| The Lesser Divinity bans with a beam; its last use "scattered into a million pieces, leaving a giant crater"; the crater holds suspicious blocks hiding something | d2c25a8d |
| The Lesser Divinity can be rebuilt as a "half broken" version | d2c25a8d |
| The patched version throws its victim **7,777,840 blocks** away, the victim cannot die (half a heart), has to cover the distance, and "now this can actually kill me" only for the **last ~25,000 blocks** | f7ce434e |
| Severing a contract frees the bearer and costs them the left arm (this version generalises it, see section 4: every break costs the next body part) | f7ce434e |
| Dusk's Epitaph (transcribed "dusk epot") is a weapon that permanently bans people; a very old, rusty gift | df7b0692, d2c25a8d |

## 3. Architecture

```
net.charter
  CharterMod                 entry point, registration order
  config/CharterConfig       every number, JSON file config/charter.json
  state/CharterState         saved data: contracts, lost body parts, exiled, judged, pending exile
  state/ContractRecord       one contract: creator, signer, ids
  body/*                     BodyPart (the order parts are lost in), BodyMirror (who lost what, readable from both
                             sides), BodyShape (hitbox / eye height / render offset), Bodies (taking and giving back
                             parts, speed, jumping, hands that cannot be used)
  enchantment/CursedEnchantment, registry/ModEnchantments
  registry/Mod*              items, entity types, enchantment, creative tab, damage types
  item/*                     ContractItem, LesserDivinityItem, DuskEpitaphItem, SigilStaffItem, DivineDominanceItem,
                             UnboundArmorItem (+ UnboundArmorMaterial), AbsolutionTokenItem (a Trinkets item)
  entity/DivineDominanceEntity  the pillar of Divine Dominance: no hitbox, and the logic of its cage
  fx/CharterFxEntity, Fx     one invisible entity + server helpers that spawn the visuals
  logic/*                    Judgement, Immunity, Terrain, Banisher, ExileManager, Contracts, Curses, Confirm,
                             Detonation, Targeting, CharterRuntime
  command/CharterCommand     /charter ...
  net/CharterNetworking      the one packet: which body parts every player has lost
net.charter.client
  CharterClient              renderer registration, packet receiver
  CharterFxRenderer          draws sigil, beam, chains and the blade of light
  DivineDominanceRenderer    draws the pillar, its sigils, its chains and the golden walls
  armor/*                    the 3D models of the Regalia of the Unbound
  BladeAnim, TwoHanded       the arms that follow the blade of light, and the two-handed Epitaph model switch
  mixin/*                    hide lost parts (player model, armour layer, held items, first person hand), sit a
                             limbless player on the ground, and the two-handed Epitaph pose
net.charter.mixin
  PlayerEntityMixin          the hitbox and eye height of a player who is only a torso or a head (common)
```

Design choices worth knowing:

* **Almost no gameplay mixins.** Everything that changes rules uses Fabric API events (attack, use, block break,
  damage, death, join, respawn, tick). The one common mixin only changes a player's hitbox and eye height when a
  body is down to a torso or a head; the others are cosmetic and client-side. All mixin configs are
  `required: false`, so a mixin that fails to apply costs a feature, never the game.
* **One effect entity (`charter:fx`).** The server sets a few tracked values (kind, start time, lifetime, size...)
  and the client derives the whole animation from the world time, so nothing is streamed every tick. Kinds: sigil,
  beam, chains, blade (the cage now belongs to the pillar entity). A tracked `style` chooses the artwork: the Charter
  sigil and chain (Lesser Divinity, Epitaph, Dominance) or the Sigil Staff's own gold rune. It is never saved and is
  drawn with the fullbright translucent layer.
* **A second entity for the pillar (`charter:divine_dominance`).** It has no hitbox (it cannot be hit, pushed, collided
  with or damaged and ignores pistons and explosions), is 1.5 x 3.0 in size, is saved with the world, and runs its own
  cage logic. It is an entity rather than a block so that it behaves like an armor stand and can be walked through.
* **Saved data** lives in the overworld's `data/charter_state.dat`, so it follows the world, not the config.
* **Bans** are real: the player is added to the vanilla ban list *and* to Charter's own list, which is enforced on
  join. `/charter pardon` removes both.

## 4. Mechanics

### Contracts (`ContractItem`, `Contracts`, `CharterState`)
A contract is a record in the saved data (creator, signer); the items are only copies of it.
* Blank contract used on a **crouching** player: the user is the creator, the target the signer; both get a signed
  copy. **[from the description]** (the crouch as consent is a **[guess]**)
* Blank contract used in the air while crouching, twice: the user signs with Charter itself (no creator).
  **[guess]**, so that the contract still has a use when nobody else is around.
* The signer cannot harm the creator: damage, the Epitaph, sigils and the Dominance wall all check it.
  **[from the description]**
* The creator takes the signer's arms: signed contract in the main hand, a sword in the off hand, strike the signer
  (left arm first, then right; 30 seconds between blows). **[from the description]**
* Breaking (signer crouches and uses the copy, twice) costs the next part: left arm, right arm, left leg, right
  leg, body. Contracts can be signed and broken again and again. **[from the description]**; the order and the
  confirmation are **[guess]**
* The creator can release the signer at no price. **[guess]**
* Lost parts (`Bodies`): a lost arm locks that hand (use, attack, break, place) and empties the off-hand slot; one
  lost leg: -35% speed; no legs: -80% speed, no jumping (a hidden jump boost with a negative amplifier), a 0.6x1.3
  hitbox and the model drawn lower; head only: 0.5x0.5 hitbox, no ground speed (it hops). **[from the description]**
  for "a head that can only jump around", numbers are **[guess]**

### The Cursed enchantment (`CursedEnchantment`, `Curses`)
* Only Charter weapons can carry it (`CharterWeapon` marker), and only a contract can put it on: the creator holds
  the signed contract in the main hand and the weapon in the off hand and uses it (not crouching).
* The weapon is tagged with the contract and the signer and delivered to the signer's inventory when they are near
  (otherwise it is dropped for them: `ItemEntity` owner = signer, never despawns).
* Anyone else holding it loses it every half second (inventory and cursor stack are scanned). The curse is lifted
  when the contract ends (online inventories and loaded item entities at once, anything else the next time somebody
  holds it).

### Lesser Divinity (`LesserDivinityItem`, `Judgement`, `Terrain`, `Detonation`)
* An **off-hand** item (a use from the main hand is refused). Aim up to 96 blocks at a creature (its feet) or the
  block hit. A sigil of radius 5 is drawn and **everything inside is chained up** (`Judgement.chainUp`: players and
  mobs, up to `sigilMaxChained`, rescanned every 5 ticks; the wielder, the wielder's pets and the creator of the
  wielder's contract are exempt). After 80 ticks a beam strikes and everyone inside the sigil receives the verdict.
  **[seen]** sigil/beam, off hand, "destroys the environment"; **[guess]** numbers.
* **Half a heart.** With `lesserDivinityHalfHeartOnly` (default on) the sigil is started with `lowOnly`, and the one
  function that decides who is "inside" (`Judgement.inside`) also requires `Judgement.isWeak`: health at or below
  `judgementHealthThreshold` (1.0 = half a heart). So only creatures at half a heart are chained and judged; the
  rescan every 5 ticks seizes whoever gets that low while standing in it, and the late-comer check at the moment of
  the strike uses the same test. Healthy creatures are left alone entirely. The destruction of the land
  (`afterStrike`) runs regardless. **[from the description]** ("it only does the ban when they are at half a heart")
* Its sigil is drawn with the Charter sigil (`STYLE_CHARTER`), like the Epitaph and the Dominance; the Sigil Staff
  keeps its own rune (`STYLE_STAFF`).
* Every strike runs `Terrain.obliterate`: the cylinder of the sigil from `terrainDepth` (4) blocks below the point of
  impact to `terrainHeight` (24) above it is erased from the top down, skipping unbreakable blocks and Divine
  Dominance, at most `terrainMaxBlocks` blocks. The Fractured version leaves the land alone by default.
* Three charges, shown on the item bar. The third destroys the item and calls `Detonation.detonate`: a blast that
  never breaks blocks, a hand-carved crater (radius 10, depth 5) and suspicious sand/gravel scattered over it that
  uses the loot table `charter:archaeology/lesser_divinity_crater` (mostly fragments). **[seen]**
* Eight fragments around a signed contract make the Fractured Lesser Divinity. **[guess]** recipe.

### Fractured Lesser Divinity
* Same sigil, verdict **exile**, unlimited uses with a 3 second cooldown. **[seen]**
* `ExileManager.exile`: remembers where the player stood, picks a random direction and teleports them
  `exileDistance` blocks (7,777,840) from the world spawn onto the surface, loading that chunk first.
* While exiled: lethal damage is cancelled and health set to `exileMinHealth` (half a heart) **unless** the player
  is within `exileLethalRadius` (25,000) of their origin; blocks cannot be broken, placed or used; the exile ends
  within `exileReturnRadius` (1,000) of the origin, or when the player really dies. **[seen]**, tail radii **[guess]**.

### Dusk's Epitaph (`DuskEpitaphItem`, `CharterRuntime.onAttackEntity`, `BladePath`, `BladeAnim`)
* A sword with damage 6 + 5 and attack speed 1.0/s that never breaks.
* A blow on a **player or a mob** (`epitaphAffectsMobs`) with attack charge of at least 85% cancels the damage and
  calls `Judgement.slamJudge`: the target is held in chains (a mob has its AI switched off, a player is held by
  teleports), cannot be hurt, and the wielder is held in place and turned toward it. For `epitaphChargeTicks` (28)
  the blade turns into a beam of light and rises over the wielder's head, then for `epitaphSlamTicks` (7) it is
  slammed down on the target; on impact the beam falls and the verdict is carried out: a player is banned, any
  other creature is slain (`damage` with the judgement damage type, then `setHealth(0)` + `onDeath` if something
  refused it). **[seen]** ban, the blade of light (the animation Lux uses on Invictus), **[guess]** timings.
* **Half a heart.** With `epitaphHalfHeartOnly` (default on) `onAttackEntity` leaves a blow alone (ordinary sword
  damage) unless `Judgement.isWeak(target)`, i.e. the target is at or below `judgementHealthThreshold`. Because a
  greatsword this heavy would simply kill most things long before they reach half a heart, `epitaphLeavesHalfHeart`
  (default on) hooks Fabric's `ALLOW_DEATH` (`CharterRuntime.epitaphSpares`): when a melee blow of the Epitaph
  holding player would kill a creature that Charter could judge (not a spectator, armor stand, creature already in
  chains or wearer of the Regalia; mobs only with `epitaphAffectsMobs`; contract rules with `weaponsRequireContract`),
  the health is set to the threshold and the death is cancelled. The next fully charged blow then finds a weak target
  and starts the slam. Fabric runs this check before totems, so a totem is not spent. **[from the description]**
  ("hp has to be half a heart, then it does the chains"); the clamp is a **[guess]** that makes the rule reachable.
* The motion is one function, `BladePath.phi(age, ...)`: the angle of the blade from straight up. The beam
  (`CharterFxRenderer.renderBlade`) is drawn along it and the player model's arms (`BipedEntityModelMixin` ->
  `BladeAnim.pose`) point along it (arm pitch = phi - PI), and the Epitaph itself is hidden in the hands
  (first and third person) while the beam replaces it. The client learns about a swing from the effect entity of
  kind `BLADE` (it calls `CharterFxEntity.bladeHook` every tick).

### Sigil Staff (`SigilStaffItem`) *(invented)*
* A small tireless Lesser Divinity: sigil radius 3.5, range 96, cooldown 10 s, never touches the land. Crouch and
  use it to cycle the mode: Bind (hold the captives `bindHoldTicks` and let go), Kick, Ban, Exile.
* It keeps the original gold rune sigil (`STYLE_STAFF`, tinted gold) and chains everybody inside its sigil; the
  half-heart rule is available through `sigilStaffHalfHeartOnly` but off by default.

### Divine Dominance (`DivineDominanceItem`, `DivineDominanceEntity`, `DivineDominanceRenderer`)
* The item is a flat sprite (red diamond tablet), not a block. Used on a block it spawns the **pillar entity** where
  a block would have been placed (`ItemPlacementContext`, centred on that block, turned to the nearest quarter turn of
  the player's yaw) and is used up unless the player is in creative mode. With `weaponsRequireContract` only contract
  signers may use it.
* The pillar is an **entity with no hitbox**, in the manner of an armor stand: 1.5 blocks wide, 3.0 tall
  (`EntityType` dimensions); `canHit`, `isAttackable`, `isCollidable` and `isPushable` are false, `damage` does
  nothing, it is invulnerable to everything (explosions and pistons ignore it), fire cannot touch it and it is saved
  with the world. It leaves the world when its cage ends, with `/charter dominance remove`, or with `/kill`.
  **[from the description]** (entity, no hitbox, 3 tall and 1.5 wide, carries the Charter sigil)
* The client draws it (`DivineDominanceRenderer`): the Charter sigil turning on the ground, a stone pillar (plinth,
  shaft and capital cut out of `dominance_pillar.png`) that rises out of the ground over 40 ticks, the sigil glowing
  on all four sides of the shaft, four tight chain wraps and two chain helices orbiting it, and the golden walls.
  **[seen]** pillar wrapped in chains.
* Arming (when the entity is spawned, or by `/charter dominance place`) fixes the geometry from the config: a square
  of half width 24 that stays still for 100 ticks then shrinks to half width 1 over 2400 ticks, reaching 48 blocks up
  and 24 down. **[wiki]**
* Players inside cannot leave, players outside cannot enter (they are put back on their side); the owner, the bound
  (signers of any contract), the creator of the owner's contract and spectators ignore it. At the end everyone still
  inside is chained and judged (`dominanceVerdict`, through `Judgement.bindAndJudge`), and the pillar shatters when
  nobody is left in chains (or after 900 ticks). Squeezed players take crush damage in the last few blocks.
  **[wiki]** concept, **[guess]** details.

### Regalia of the Unbound and Token of Absolution (`Immunity`, `UnboundArmorItem`, `AbsolutionTokenItem`) *(invented)*
* `Immunity.protects(entity)` is true when `armorGrantsImmunity` is on and at least `armorPiecesRequired` (4) of the
  armor the creature wears are `UnboundArmorItem`s. Every way in checks it: `Judgement.exempt` (the sigil does not seize
  them), `Judgement.begin` (a swing of the Epitaph does not seize them), the Epitaph's half-heart clamp (against a
  wearer the Epitaph is only a sword, so it can kill), and `Judgement.turnedAway`, the last test before a verdict
  falls, which catches whoever put the armor on while already in chains. An operator's `/charter judge` is forced and skips all of it, and so does
  `/charter exile` (it never asks).
* `Immunity.absolve(entity)` looks for a Token of Absolution in a Trinkets slot (`chest/necklace`, set up by the
  files under `data/trinkets/`), removes it (unless `absolutionTokenConsumed` is off) and turns the verdict away; the
  chains still close first. Only that one verdict is spared.
* The armor is a real 3D model (plates, horns, pauldrons, chains and a cape built from boxes): `UnboundArmorModel`
  defines one box model per piece, `UnboundArmorLayers` registers them with Fabric's `ArmorRenderer` API, and
  `UnboundArmorRenderer` draws them with `unbound.png` plus a fullbright pass of `unbound_glow.png` for the glowing
  pixels. The plain vanilla-style layers (`unbound_layer_1/2.png`) exist so that anything that draws armor the vanilla
  way still shows something. Lost limbs are respected (`LostParts`): a missing arm or leg hides that part of the armor.
* All art is original (corroded netherite, open golden shackles).

### Judgement sequence
Nothing is instant. A verdict is always: telegraph (sigil or chains, sounds, particles) -> beam and thunder ->
verdict. While chained, the target is protected from all damage, cannot attack, use items or interact, and is
held in place. Verdicts: `BAN`, `EXILE`, `KICK`, `KILL`. A ban that is switched off in the config, or aimed at an
exempt player (the host of a local world), becomes the configured fallback (`EXILE` or `KICK`).

## 5. Assets and data

* All art was drawn from scratch for this project (pixel art for items, the pillar and the armor, procedural
  patterns for the beam and the cage) and checked as images. The generator scripts are not part of the project; the
  PNGs are final. No texture of the original mod is used. Every texture is documented, with its size and how it is
  drawn, in `docs/TEXTURES.md`, and `tools/make_resourcepack.py` builds a ready-made resource-pack template from them.
* Effect textures (`textures/entity/`):
  * `charter_sigil.png` (32x32): the Charter sigil under the judged, in the shockwave of the beam and on the pillar.
    Drawn flat, north at the top, turning slowly, fullbright and **untinted**.
  * `charter_chain.png` (32x3): the strip that makes every golden chain, repeated every 1.6 blocks along it,
    fullbright and **untinted**, visible from both sides. Both of these are read in the shapes of the two files the
    commissioner supplied, so those can be dropped in as they are.
  * `staff_sigil.png` (256x256): the original rotating rune circle, now only for the Sigil Staff (drawn twice, counter-
    rotating, tinted gold).
  * `beam.png` (64x256, repeats every 24 blocks) and `cage.png` (128x128, repeats every 4 blocks): white with alpha,
    tinted gold by the renderer.
  * `dominance_pillar.png` (64x64): the stone of the pillar of Divine Dominance (layout in `docs/TEXTURES.md`).
* Armor (`models/armor/`): `unbound.png` and `unbound_glow.png` (128x48) for the 3D model, `unbound_layer_1/2.png`
  (64x32) as plain layers.
* Dusk's Epitaph (`textures/item/dusk_epitaph.png`, 32x32) uses `minecraft:item/handheld` plus a second model for the
  two-handed hold (`dusk_epitaph_twohanded.json`, chosen by the `charter:two_handed` predicate); one image serves both.
* Data: two damage types (`charter:judgement`, `charter:crush`) with the tags that make the first unblockable, two
  recipes, the loot table of the crater, and the Trinkets files (`data/trinkets/`: the `chest/necklace` tag with the
  Token of Absolution, and the slots a player has).
* Language: `assets/charter/lang/en_us.json`. Chat messages from the server are plain English literals.

## 6. In-game test checklist

Nothing here could be run while writing the mod, so it is worth a quick pass after the first build. Give yourself
operator rights and create a creative world with Fabric API installed.

1. **Start-up.** The game reaches the title screen; the log says "Charter has been signed". The creative tab
   *Charter* shows thirteen items with their textures; hover each to read the tooltips.
2. **Contract.** With two players: `/give @s charter:charter_contract`; use it on the other player while they
   crouch: both get a signed contract. The signer cannot hit the creator. Hold the signed contract in the main hand
   and a sword in the off hand and strike the signer: their left arm disappears (check F5), and they cannot use their
   off hand. Alone: crouch and use a blank contract twice to sign with Charter, then crouch and use the signed copy
   twice to break it: it costs your left arm, then your right arm, and so on until only your head is left.
   `/charter body restore @s` brings everything back.
   **Cursed:** the creator holds the signed contract (main hand) and a Charter weapon (off hand) and uses it: the
   weapon goes to the signer, and nobody else can keep it.
3. **Sigil without a ban.** `/charter sigil ~ ~ ~ kick`: a golden sigil, a beam, and any other player standing in it
   is disconnected. (Aim it at a second account.)
4. **Lesser Divinity.** `/give @s charter:lesser_divinity`, put it in your **off hand**, aim at the ground (with a few
   mobs standing there): the Charter sigil appears, and the land inside it disappears when the beam falls, but
   healthy mobs are left alone. Hurt a mob down to half a heart (a pig has 10 health, so
   `/damage @e[type=minecraft:pig,limit=1,sort=nearest] 9` leaves it on 1, which is half a heart) and use the item
   again: sigil -> chains -> beam, and the mob is slain. Use it three times: the third destroys it and digs a crater. Brush the suspicious
   blocks with a brush to find fragments. `lesserDivinityHalfHeartOnly: false` in the config makes it judge everyone.
   *Testing alone in singleplayer:* the host cannot be banned, so a ban aimed at you becomes an exile.
5. **Exile.** `/charter exile <name>`: they are teleported millions of blocks away and can look but not touch.
   `/charter release <name> home` brings them back.
6. **Epitaph.** `/give @s charter:dusk_epitaph`; hold it with both hands (off hand empty). On a mob at full health,
   full-strength swings only hurt, and the last blow that would kill it leaves it on half a heart (the mob is still
   alive). The next full-strength swing chains it, the blade turns into light, rises and is brought down on it (players
   are banned, mobs slain). `/charter slam <creature>` shows the animation without a second player.
   `/charter pardon <name>` lets a banned player back in. Press F5 to see the two-handed hold.
7. **Divine Dominance.** To see it quickly set `dominanceArmTicks` and `dominanceShrinkTicks` low in
   `config/charter.json`, `/charter reload`, then use the tablet on the ground (or `/charter dominance place ~ ~ ~`):
   a 1.5 x 3 pillar rises with the Charter sigil on each side, wrapped in chains, and the golden walls close in.
   You can walk through the pillar and cannot hit it. `/charter dominance list` shows the time left and
   `/charter dominance remove nearest` takes it down.
8. **Regalia and Token.** `/give @s charter:unbound_helmet` (and the other three); put all four on and stand in
   `/charter sigil ~ ~ ~ bind`: nothing seizes you. Take one piece off and you are chained for ten seconds.
   `/give @s charter:token_of_absolution`; open the inventory (the Trinkets slots sit on the side of the inventory
   screen), put it in the necklace slot, and stand in `/charter sigil ~ ~ ~ exile`: you are chained, then the token
   crumbles and you stay where you are. (Without it you would be exiled; `/charter release @s home` brings you back.)
9. **Textures.** Press **F3 + T** after changing anything in a resource pack; `docs/TEXTURES.md` has the steps for
   replacing Dusk's Epitaph, the sigil and the chain.

## 7. Extending it

* New verdicts go in `Verdict` and `Judgement.deliver`.
* All numbers live in `CharterConfig`; add a field and it appears in the JSON.
* The public 1.19.2 Charter features (claims, vessels) would be new packages next to `logic`; the saved data class
  already has the place for them.
