# Charter (Fan Recreation) for Minecraft 1.20.1 (Fabric)

An **unofficial fan recreation** of the *Charter* mod from the ContentSMP: golden contracts that bind a soul, the
golden sigils, beams and chains of a judgement, the weapons that can **permanently ban** a player, and the
exile that comes after the patch.

> **Read this first.** This is a clean-room recreation written from the ContentSMP videos (the five transcripts you
> supplied) and the public ContentSMP wiki page for Charter. The original mod is private, none of its code was seen,
> and this project is not affiliated with its authors or with the ContentSMP. Everything the videos show is
> implemented; where they leave a gap, the gap was filled with a reasonable guess and is marked **(inferred)**
> below. The project could not be compiled in the environment it was written in, so see
> [If the build fails](#if-the-build-fails).

## Getting the jar

### Easiest: let GitHub build it (no Java needed)

1. Unzip the download. Create a new, empty repository on GitHub and upload **everything inside the unzipped
   folder** (`src`, `gradle`, `gradlew`, `build.gradle`, ... and the hidden `.github` folder) so that `gradlew` and
   `build.gradle` sit at the top of the repository. (If you upload the folder itself by mistake the workflow still
   finds the project one level down, so that no longer breaks the build.)
2. Open the repository's **Actions** tab. The `build` workflow starts by itself on every push (or press
   *Run workflow*).
3. When it turns green, open the run and download the artifact **`charter-jar`**. Inside is
   `charter-fabric-mc1.20.1-<version>.jar`.
4. A second job, `smoke-test`, starts a dedicated server with the mod and attaches its log as the artifact
   **`server-log`**. It never fails the workflow; if the game crashes on start-up, that log is what to send back.
5. To publish the jar as a release instead, push a tag such as `v1.0.0`: the jar is attached to the release.

### Or build it yourself

You need **JDK 25** (the current Fabric Loom needs it to *run*; the mod itself is compiled for Java 17):

```
./gradlew build            # Windows: gradlew.bat build
```

The jar appears in `build/libs/`. Use the one without `-dev` or `-sources` in its name.

## Installing

* Minecraft **1.20.1**, Fabric Loader **0.14.21 or newer**, **Fabric API** (built against 0.92.12+1.20.1), Java 17+.
* **Trinkets** (3.7.x) provides the necklace slot of the Token of Absolution. It is bundled inside the Charter jar
  ("jar in jar"), so there is nothing extra to download; having Trinkets installed on its own is fine too.
* Put the jar in `mods/`. **Install it on the server *and* on every client**: the effects, the items, the pillar and
  the missing-arm model need the mod on both sides.
* Nothing needs a resource pack. The first launch writes `config/charter.json`.
* Want different art? Every texture can be swapped for your own with a resource pack, no rebuild needed:
  [docs/TEXTURES.md](docs/TEXTURES.md) explains it step by step (starting with Dusk's Epitaph), and
  `charter-texture-pack-template.zip` is a ready-made pack with every texture of the mod in the right place.

## What is in it

| Item / block | What it is |
| --- | --- |
| **Charter Contract** | A leather book with gold on it. Use it on a crouching player to draw up a contract with them, or crouch and use it twice to sign with Charter itself. |
| **Signed Contract** | The written copy of a contract, with a red signet-ring seal. The creator's copy and the signer's copy do different things. |
| **Cursed** *(enchantment)* | Can only go on Charter weapons, and only through a contract: only the contract's signer can hold the weapon. |
| **Lesser Divinity** | An **off-hand** relic with three uses. Its sigil chains up everything inside it that is **down to half a heart**, then a beam permanently bans them, and the land inside the sigil is erased. The third use destroys it and leaves a crater. |
| **Lesser Divinity Fragment** | A shard brushed out of the crater. |
| **Fractured Lesser Divinity** | The patched, rebuilt version: it exiles instead of banning and never runs out. |
| **Dusk's Epitaph** | A huge rusted greatsword, held in both hands. It hits hard but never kills; once the target is **down to half a heart**, a full-strength blow chains a player or a mob, the blade turns into a beam of light, rises over your head and is slammed down on them. Players are banned, mobs slain. |
| **Sigil Staff** *(invented)* | A tireless staff that draws small sigils. Four modes (crouch and use to change): **Bind** holds everything in the sigil for a while and lets it go, **Kick**, **Ban**, **Exile**. It never touches the land. |
| **Divine Dominance** | A flat red diamond tablet. Used on the ground it raises a **pillar of Charter**: an entity with no hitbox (like an armor stand), 1.5 blocks wide and 3 tall, carrying the Charter sigil on every side and wrapped in golden chains. It raises a golden cage that slowly closes in. |
| **Regalia of the Unbound** *(Helm, Cuirass, Greaves, Sabatons)* | A 3D armor set of corroded netherite hung with open golden shackles. Wear all four pieces and Charter cannot touch you: the chains do not seize you, and no ban, kick or exile falls on you. |
| **Token of Absolution** | A necklace (Trinkets slot). It turns away one verdict of Charter and then crumbles. The chains still close; only the sentence passes you by. |

Everything except the Signed Contract (you get that by signing) is in the creative tab **Charter**, and every item works with `/give @s charter:<item>` (the ids are `charter_contract`, `signed_contract`, `lesser_divinity`, `lesser_divinity_fragment`, `fractured_lesser_divinity`, `dusk_epitaph`, `sigil_staff`, `divine_dominance`, `unbound_helmet`, `unbound_chestplate`, `unbound_leggings`, `unbound_boots` and `token_of_absolution`).

### Crafting

* **Charter Contract**: paper + ink sac + gold nugget (shapeless).
* **Fractured Lesser Divinity**: eight Lesser Divinity Fragments in a ring around a **Signed Contract**. *(inferred)*
* The Lesser Divinity, Dusk's Epitaph, Divine Dominance, the Regalia of the Unbound and the Token of Absolution cannot
  be crafted; on the ContentSMP such things were obtained by deals and trades. Hand them out with `/give`, or have
  the crater give you the fragments.

## How it plays

### Contracts, the Cursed enchantment and lost limbs
A contract has two sides: the **creator** draws it up, the **signer** puts their name to it.

* **Drawing one up:** use a blank contract on a player who is crouching (that is their consent). You become the
  creator and they become the signer; each of you receives a signed copy (a book with a red signet-ring seal).
  Crouch and use a blank contract in the air, twice, to sign with Charter itself (nobody is the creator then).
* **The signer can never harm the creator.** Swords, the Epitaph, Lesser Divinity sigils and the Divine Dominance
  wall all leave the creator alone.
* **The creator can take the signer's arms:** hold the signed contract in your main hand and a sword in your off
  hand, and strike the signer. The first blow takes their left arm, the next their right arm.
* **Curse a weapon:** hold the signed contract in your main hand and a Charter weapon (Lesser Divinity, Dusk's
  Epitaph, Divine Dominance) in your off hand, and use the contract without crouching. The weapon gains the
  **Cursed** enchantment and is delivered to the signer; **only the signer can pick it up or hold it**; anyone else
  who ends up with it loses it again. The curse ends with the contract.
* **Breaking a contract** (the signer crouches and uses their copy, twice) **costs a part of their body**: the left
  arm, then the right arm, then the left leg, the right leg, and finally everything but the head. They can sign again
  and break again, over and over, until they are only a head that hops.
* The creator can **release** the signer (crouch and use the creator's copy, twice) at no price.
* **What a missing part does:** it disappears from the model (third person, first person and the armour layer, for
  everyone who has the mod). A missing arm can no longer use, hit, break or place with that hand, and the off hand
  slot is emptied; one missing leg slows you; two missing legs and you sit on the ground with a smaller hitbox and
  cannot jump; with only a head left you are half a block wide and can only hop. `/charter body restore <player>`
  gives everything back; by default dying does not.
* A player who is the signer of any contract is "bound": golden motes circle them and they can walk through the
  walls of Divine Dominance.

### Lesser Divinity
An **off-hand item**: it only answers when it is in your off hand (`lesserDivinityOffhandOnly`). Point it at the
ground or at a creature (up to 96 blocks). The golden **Charter sigil** is drawn on the ground. Like Dusk's Epitaph,
it only takes hold of those who are **down to half a heart** (`lesserDivinityHalfHeartOnly`,
`judgementHealthThreshold`): **everything standing in the sigil that is at half a heart or less is seized by golden
chains** (players and mobs alike; the wielder, the wielder's pets and the creator of the wielder's contract are
spared, and anything that is brought down that far while standing in it is chained too). Anything healthier is left
alone. The sigil burns brighter for four seconds, and then a **beam** falls: every chained player is **permanently
banned** (their inventory drops where they stood, they are added to the server's ban list and disconnected with the
Charter message; Charter keeps its own list too and kicks the judged on join even if the ban list is edited), every
chained mob is slain, and **the environment inside the sigil is destroyed**, whoever stands in it: every block from a
few blocks under the point of impact up to 24 blocks above it, except bedrock and other unbreakable blocks.
The **third use destroys the item**: it blows up into a huge crater whose **suspicious sand and gravel** each hide a
**Lesser Divinity Fragment** (brush them out with a brush).

### Fractured Lesser Divinity
The patched version, also an off-hand item. It no longer bans: the victims are chained up in its sigil and then
**exiled 7,777,840 blocks** from the world spawn (mobs simply vanish). It leaves the land alone
(`fracturedDestroysTerrain`), and it chains everybody in its sigil, not only the weak, unless you switch on
`fracturedHalfHeartOnly`. Exiled players **cannot die** (lethal damage leaves them on half a heart) until they are
within 25,000 blocks of home, **cannot break, place or use blocks**, and the exile ends when they get back within 1,000
blocks of where they were thrown from. It does not run out.

### Dusk's Epitaph
Held in **both hands** (main hand holds it, off hand empty; with something in the off hand it is a one-handed
hold). A slow, heavy greatsword, rusted and worn.

* **Until the target is down to half a heart it is only a sword** (`epitaphHalfHeartOnly`). It hits hard but **never
  kills**: a blow that would finish a player or a mob leaves them on exactly half a heart (`epitaphLeavesHalfHeart`,
  `judgementHealthThreshold`).
* **At half a heart, a fully charged blow** on a **player or a mob** is not damage: golden chains seize the target and
  the Charter sigil burns under it while the blade **turns into a beam of light**, rises over the wielder's head
  (both arms follow it) and is **slammed down on the chained target**. A player is **banned**, any other creature is
  slain. The wielder stays where they are, facing the target, during the swing. A weaker swing is only a heavy blade.
* With `epitaphHalfHeartOnly` off, every full blow starts the beam of light at once, whatever the target's health.
  With `epitaphAffectsMobs` off it only works on players.

### Sigil Staff *(invented for this mod)*
Draws a small sigil (3.5 blocks) where you look, with the same chains-then-beam sequence as the Lesser Divinity, but
it never runs out and never destroys the land. Crouch and use it to change the mode: **Bind** (everything in the
sigil is held in chains for ten seconds and then let go), **Kick**, **Ban** or **Exile**. It keeps its own gold rune
sigil and beam (the Charter sigil is reserved for the real weapons) and it chains everybody in its sigil, not only the
weak; `sigilStaffHalfHeartOnly` changes that.

### Divine Dominance *(inferred from the wiki)*
The item is a flat red diamond tablet. Used on the ground it raises a **pillar of Charter**. The pillar is an
**entity, not a block**, so it has **no hitbox**: nothing can hit it, push it, stand on it or break it, and you walk
right through it. It is **1.5 blocks wide and 3 blocks tall**, carries the **Charter sigil on all four sides**, is
wrapped in golden Charter chains and has the sigil burning on the ground under it. A golden square wall rises around
it (48 blocks wide, 48 up and 24 down). After five seconds the wall starts to close in and takes two minutes to meet
in the middle. Whoever is inside is held in, whoever is outside is kept out; the placer, Charter-bound players (the
signers of any contract), the creator of the placer's contract and spectators pass freely. When the walls meet,
everyone left inside is chained and judged (ban by default), and the pillar shatters once the judgements are done.
Because it has no hitbox, it is looked after with commands: `/charter dominance list` shows every pillar, where it
stands and how long it has left, `/charter dominance place <pos>` raises one, and `/charter dominance remove
all|nearest` shatters them (`/kill @e[type=charter:divine_dominance]` works too).

### Regalia of the Unbound and Token of Absolution *(invented)*
* **Regalia of the Unbound** (Helm, Cuirass, Greaves and Sabatons of the Unbound). A 3D armor set, drawn with its own
  model rather than the flat vanilla layers: corroded netherite with golden shackles that hang open. **Wearing all four
  pieces** (`armorPiecesRequired`) **puts you beyond Charter**: the golden chains cannot seize you, and no ban, kick or
  exile can fall on you, from the Lesser Divinity, Dusk's Epitaph, the Sigil Staff or Divine Dominance. If you put it
  on while you are already in chains, the verdict is turned away when it falls. Only an operator's `/charter judge`
  and `/charter exile` ignore the armor.
* **Token of Absolution.** A pendant for the necklace slot (Trinkets). The first verdict that is about to fall on its
  wearer is turned away and the token crumbles (`absolutionTokenConsumed`). The chains still close around you; only
  the sentence passes you by.

## Commands

All need permission level 2 (operator).

| Command | Effect |
| --- | --- |
| `/charter bind <players>` | Each player signs a contract with Charter itself. |
| `/charter unbind <players>` | Lift every contract the players have signed, at no price. |
| `/charter sever <players>` | Each player breaks their oldest contract and pays with the next body part. |
| `/charter body remove <players> [part]` | Take a body part (the next in line when none is named). Parts: `left_arm`, `right_arm`, `left_leg`, `right_leg`, `body`. |
| `/charter body restore <players> [part]` | Give a part back (everything when none is named). |
| `/charter judge <creatures> [ban\|exile\|kick\|kill\|bind]` | Chain and judge players or mobs (default `ban`; `bind` only holds them for a moment). Ignores contracts and armour. |
| `/charter slam <creature> [verdict]` | The swing of Dusk's Epitaph (the blade of light rises and falls) with you as the wielder. |
| `/charter sigil <pos> <verdict> [radius]` | Draw a sigil that judges whoever stands in it. |
| `/charter exile <players>` | Exile at once. |
| `/charter release <players> [home]` | End an exile (optionally teleporting them home). |
| `/charter pardon <name>` | Remove a ban placed by Charter. |
| `/charter crater <pos>` | Test the crater of the Lesser Divinity. |
| `/charter dominance list` | Every pillar of Divine Dominance in the loaded chunks: where it stands, who placed it and how long it has left. |
| `/charter dominance place <pos>` | Raise a pillar (and its golden walls) at a position, with you as its owner. |
| `/charter dominance remove <all\|nearest>` | Shatter every pillar, or only the one nearest to you. Nobody is judged. |
| `/charter list` | Every contract, who is missing which parts, who is exiled and who is judged. |
| `/charter reload` | Re-read `config/charter.json`. |

## Configuration

`config/charter.json`, re-read with `/charter reload`. Missing keys fall back to the defaults; delete the file to
reset it. Tick values are in game ticks (20 per second).

#### Permanent ban

| Key | Default | What it does |
| --- | --- | --- |
| `permanentBans` | `true` | Master switch. When false every "BAN" verdict is replaced by `fallbackVerdict`. |
| `fallbackVerdict` | `EXILE` | What happens instead of a ban when a ban is switched off or the target is exempt: EXILE or KICK. |
| `exemptOperators` | `false` | Operators cannot be banned when true. Off by default: on the ContentSMP even the server owner was banned. |
| `exemptSingleplayerHost` | `true` | The host of a singleplayer / LAN world is never banned (they would lose their own world). |
| `dropInventoryOnBan` | `true` | Banned players drop their whole inventory where they stood, as if they had died. |
| `useVanillaBanList` | `true` | Add the player to the vanilla ban list (banned-players.json). |
| `enforceOnJoin` | `true` | Charter keeps its own list of judged players and kicks them on join even if the vanilla list was edited. |
| `banSource` | `Charter` | Source name written to the vanilla ban list. |
| `banReason` | `Judged by Charter.` | Reason written to the vanilla ban list. |
| `kickMessage` | `Charter has judged you.\nYour name has been struck from this world.` | Shown on the disconnect screen. "\n" starts a new line. |
| `broadcastJudgements` | `true` | Announce every ban in chat. |
| `broadcastMessage` | `%s has been judged by Charter.` | %s is replaced by the player's name. |

#### Telegraph

| Key | Default | What it does |
| --- | --- | --- |
| `sigilTelegraphTicks` | `80` | Ticks (20 per second) between the golden sigil appearing and the strike. |
| `chainTelegraphTicks` | `40` | Ticks a target stays bound in golden chains before Dusk's Epitaph finishes the judgement. |
| `strikeBeamTicks` | `30` | How long the golden beam of the strike stays visible. |
| `sigilRadius` | `5.0` | Radius in blocks of the sigil the Lesser Divinity draws; everyone inside it is chained and judged. |
| `sigilMaxChained` | `32` | Most creatures one sigil chains up (the rest standing in it are still judged by the beam). |
| `sigilSparesPets` | `true` | A sigil spares the tamed animals of whoever drew it. |
| `bindHoldTicks` | `200` | How long a sigil in Bind mode holds its captives. |

#### Half a heart

| Key | Default | What it does |
| --- | --- | --- |
| `judgementHealthThreshold` | `1.0` | Health, in points (2 points are a heart), at or below which Charter's weapons can take hold of a creature. 1.0 is half a heart. |
| `epitaphHalfHeartOnly` | `true` | Dusk's Epitaph only chains and judges a creature that is down to the threshold; above it, it is only a heavy sword. |
| `epitaphLeavesHalfHeart` | `true` | A blow of the Epitaph that would kill a creature leaves it on half a heart instead, so the next full blow can bring down the chains. Only has an effect with `epitaphHalfHeartOnly`. |
| `lesserDivinityHalfHeartOnly` | `true` | The Lesser Divinity only chains and judges the creatures in its sigil that are down to the threshold. (The land is erased either way.) |
| `fracturedHalfHeartOnly` | `false` | The same rule for the Fractured Lesser Divinity. |
| `sigilStaffHalfHeartOnly` | `false` | The same rule for the Sigil Staff. |

#### Lesser Divinity

| Key | Default | What it does |
| --- | --- | --- |
| `lesserDivinityCharges` | `3` | Uses before the Lesser Divinity destroys itself. |
| `lesserDivinityRange` | `96.0` | How far (in blocks) the wielder can aim it. |
| `lesserDivinityOffhandOnly` | `true` | The Lesser Divinity (and its fractured form) only answers from the off hand. |
| `lesserDivinityDestroysTerrain` | `true` | Every strike erases the land inside the sigil. |
| `fracturedDestroysTerrain` | `false` | The Fractured Lesser Divinity erases the land too. |
| `terrainDepth` | `4` | Blocks below the point of impact that are erased. |
| `terrainHeight` | `24` | Blocks above the point of impact that are erased. |
| `terrainMaxBlocks` | `30000` | The most blocks one strike may erase. |
| `lesserDivinityCooldownTicks` | `60` | Item cooldown after each use. |
| `fracturedCooldownTicks` | `60` | Item cooldown after each use of the Fractured Lesser Divinity. |
| `craterRadius` | `10` | Radius in blocks of the crater left by the last use. |
| `craterDepth` | `5` | Depth in blocks at the centre of that crater. |
| `craterSuspiciousChance` | `0.4` | Chance that a block of the crater becomes suspicious gravel holding a fragment. |
| `detonationPower` | `4.0` | Strength of the blast of the last use. It hurts and throws creatures but never breaks blocks. 0 turns it off. |

#### Dusk's Epitaph

| Key | Default | What it does |
| --- | --- | --- |
| `epitaphMinCharge` | `0.85` | Attack cooldown progress (0 to 1) needed for a blow to ban. 0.85 means "nearly a full swing". |
| `epitaphAffectsMobs` | `true` | The Epitaph judges mobs as well as players (mobs are slain). |
| `epitaphChargeTicks` | `28` | Ticks the blade takes to turn into light and rise while the target is chained. |
| `epitaphSlamTicks` | `7` | Ticks the beam of light takes to come down on the target. |
| `epitaphHoldsWielder` | `true` | The wielder stands still and faces the target during the swing. |
| `epitaphCooldownTicks` | `60` | Cooldown of the Epitaph after the swing has ended. |

#### Sigil Staff

| Key | Default | What it does |
| --- | --- | --- |
| `sigilStaffRange` | `96.0` | How far (in blocks) the wielder can aim it. |
| `sigilStaffRadius` | `3.5` | Radius in blocks of the sigil it draws. |
| `sigilStaffCooldownTicks` | `200` | Item cooldown after each use. |

#### Regalia of the Unbound and Token of Absolution

| Key | Default | What it does |
| --- | --- | --- |
| `armorGrantsImmunity` | `true` | Wearing the Regalia of the Unbound puts the wearer beyond Charter's chains and verdicts. |
| `armorPiecesRequired` | `4` | How many of the four pieces have to be worn for that. |
| `absolutionTokenConsumed` | `true` | A Token of Absolution crumbles when it turns a verdict away (false: it protects for ever). |

#### Exile

| Key | Default | What it does |
| --- | --- | --- |
| `exileDistance` | `7777840.0` | Blocks from the world spawn that an exiled player is thrown. |
| `exileDimension` | `minecraft:overworld` | Dimension the exile takes place in. |
| `exileReturnRadius` | `1000` | An exiled player is freed on coming this close to where they were exiled from. |
| `exileUnkillable` | `true` | Exiled players cannot die. Lethal damage leaves them on exileMinHealth. |
| `exileLethalRadius` | `25000` | The exile stops protecting from death this close to home ("now this can actually kill me"). |
| `exileMinHealth` | `1.0` | Health an exiled player is left on instead of dying (1.0 is half a heart). |
| `exileLocksBlocks` | `true` | Exiled players cannot break, place or use blocks: they may look, but not touch. |
| `exileMessages` | `true` | Tell exiled players how far from home they are. |

#### Divine Dominance

| Key | Default | What it does |
| --- | --- | --- |
| `dominanceStartRadius` | `24.0` | Half width of the golden square when it starts to close in (blocks from the pillar to each wall). |
| `dominanceEndRadius` | `1.0` | Half width at which the cage has fully closed. |
| `dominanceArmTicks` | `100` | Ticks between raising the pillar and the walls starting to move. |
| `dominanceShrinkTicks` | `2400` | Ticks the walls take to close in. |
| `dominanceHeightUp` | `48` | Blocks the walls reach above the pillar. |
| `dominanceHeightDown` | `24` | Blocks the walls reach below the pillar. |
| `dominanceVerdict` | `BAN` | What happens to everyone still inside when the cage closes: BAN or KILL. |
| `dominanceCrushDamage` | `4.0` | Damage per second once the walls are squeezing (only the last few blocks). |

#### Contracts and bodies

| Key | Default | What it does |
| --- | --- | --- |
| `contractNeedsConsent` | `true` | The player a contract is used on has to be crouching. |
| `contractCharterSigning` | `true` | A blank contract may be signed with Charter itself (crouch and use it twice). |
| `creatorProtected` | `true` | The signer of a contract cannot harm its creator. |
| `creatorMayMaim` | `true` | The creator may take the signer's arms (contract in the main hand, a sword in the off hand). |
| `maimCooldownTicks` | `600` | How long the contract needs before it can take another arm. |
| `creatorMayRelease` | `true` | The creator may release the signer at no price. |
| `contractMayBeBroken` | `true` | The signer may break the contract. |
| `breakingCostsBody` | `true` | Breaking a contract costs the next body part. |
| `lostArmsLockHands` | `true` | A hand whose arm is gone cannot be used, and the off-hand slot is emptied. |
| `lostLegsSlow` | `true` | Missing legs slow you down; with none you cannot jump. |
| `bodyRestoredOnDeath` | `false` | When true, dying grows everything back. Off by default: in the lore only Charter gives it back. |
| `cursedEnchantment` | `true` | Contracts may curse Charter weapons. |
| `curseDeliveryRange` | `64.0` | A cursed weapon is put straight into the signer's inventory when they are this close; otherwise it waits on the ground, and only they can pick it up. |
| `boundParticles` | `true` | Contract signers are wreathed in golden chain particles. |
| `weaponsRequireContract` | `false` | When true the Lesser Divinity, Dusk's Epitaph and Divine Dominance only answer to contract signers. |


## What is faithful and what is a guess

**Taken from the videos:** the golden beams and chains; the permanent ban (title screen, never again); contracts
whose breaking costs a body part; a player without an arm not showing it and not using that hand; the Lesser Divinity (a sigil, then a beam, three uses, the last
detonating into a giant crater of suspicious blocks hiding pieces); the patched version that exiles about
7,777,840 blocks, cannot die until about 25,000 blocks from home, and cannot touch blocks; Dusk's Epitaph as a rusted
greatsword that chains and bans.

**Taken from your description:** the creator / signer contract, the signer being unable to harm the creator, the
creator taking an arm with the contract and a sword, breaking a contract over and over until only a head is left,
the Cursed enchantment that only the signer can hold, Dusk's Epitaph and the Lesser Divinity only taking hold of
those who are down to half a heart, Divine Dominance as a hitbox-less pillar 1.5 blocks wide and 3 tall wrapped in
chains and carrying the Charter sigil, a 3D armor set that makes its wearer immune to the chains, bans, kicks and
exile, and the two texture slots in the shapes of the files you sent (a 32 x 3 chain strip and a 32 x 32 sigil).

**Inferred (the sources do not say):** exact timings, radii and damage numbers; the crafting recipes; the textures
and models (all drawn from scratch: no texture of the original mod was used, which is also why your two files are
not bundled: the guide in `docs/TEXTURES.md` shows how to drop them in); how Divine Dominance behaves in detail (it
comes from the wiki, not the videos); the design of the Regalia of the Unbound and the Token of Absolution; the order
in which body parts go; the speed penalties; the particle effects; the wording of every message.

**Not included:** the *Mark of the Benefactor*, the "Charter clothes", the Mouthpiece NPC, and the features of the
public 1.19.2 Charter mod (claims, vessels and the rest). The design is ready for them but they were out of scope.

## If the build fails

The code was checked as far as possible without a compiler (every import and every Minecraft call was checked
against the 1.20.1 Yarn mappings, and all sources pass a Java syntax check), but it could not be compiled here. If
GitHub's build goes red, open the failed run, copy the first few lines that contain `error:` and send them back:
a mistake of this kind is normally a one-line fix. The one outside library is Trinkets (`dev.emi:trinkets:3.7.2`,
looked up on the TerraformersMC and Sleeping Town maven repositories, with Cardinal Components from Ladysnake's). If
the log says it could not resolve that dependency, send that line back as well.

If the mod builds but the game does not start, send `logs/latest.log` (or the `server-log` artifact of the build) and
the crash report from `crash-reports/`.

## Project layout

```
src/main/java/net/charter/     common code (items, the pillar entity, commands, logic, state, config)
src/client/java/net/charter/   client code (renderers for the golden effects, the pillar and the armor; model mixins)
src/main/resources/            mod metadata, assets (models, textures, lang), data (recipes, loot, damage types)
docs/SPEC.md                   the design notes behind each mechanic
docs/TEXTURES.md               how to replace any texture, Dusk's Epitaph step by step
tools/make_resourcepack.py     builds charter-texture-pack-template.zip from the project's textures
```

## Credits

Original Charter mod and the ContentSMP belong to their creators. This fan recreation is MIT licensed (see
`LICENSE`); the original mod's own licence is not affected by it. Built with Fabric.
