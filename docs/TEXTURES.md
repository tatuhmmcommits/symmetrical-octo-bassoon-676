# Changing the textures of the Charter mod

This guide shows how to replace any texture of the mod with your own, starting with **Dusk's Epitaph**. You do not
need to rebuild the mod or touch any code: a resource pack is enough, and the file `charter-texture-pack-template.zip`
that came with this project is a ready-made one.

A note on the art that ships with the mod: every texture in it was drawn from scratch for this recreation. The art of
the original ContentSMP mod belongs to its makers, so none of it is bundled. The mod is built so that your own files
drop straight in: the chain and the sigil are read in exactly the shapes of the two files you sent (a 32 x 3 chain
strip and a 32 x 32 sigil), so you can use them as they are.

---

## 1. The quick way: a resource pack (no rebuild)

1. Unzip `charter-texture-pack-template.zip` somewhere, or leave it zipped. It already contains copies of every
   texture of the mod at exactly the right paths.
2. Put your own PNG over the one you want to change (see the tables below for the exact file name and size).
   Keep the folder structure and the file name exactly as they are. Names are lower case and case matters.
3. Copy the pack (the folder or the zip) into your game's `resourcepacks` folder:
   * Windows: `%appdata%\.minecraft\resourcepacks`
   * macOS: `~/Library/Application Support/minecraft/resourcepacks`
   * Linux: `~/.minecraft/resourcepacks`
4. In the game: **Options > Resource Packs**, move the pack to the right-hand list and keep it **above** the other
   entries. Done. While you are editing, **F3 + T** reloads all textures without restarting.

Things worth knowing:

* Packs only change how things look **on your own computer**. Other players on a server see their own textures.
* You only need the files you actually changed. Delete the rest of the template if you like (keep `pack.mcmeta`).
* The pack format for Minecraft 1.20.1 is `15` (already set in the template's `pack.mcmeta`).

### Using your own sigil and chain files (the two you sent)

1. Copy your 32 x 32 sigil over `assets/charter/textures/entity/charter_sigil.png` in the pack.
2. Copy your 32 x 3 chain strip over `assets/charter/textures/entity/charter_chain.png`.
3. Enable the pack and press **F3 + T**. Every sigil under the judged, the shockwave of the beam, the pillar of Divine
   Dominance and every chain now use your pictures. The Sigil Staff keeps its own sigil (`staff_sigil.png`).

They are drawn exactly as they are (no tint), so the colours you drew are the colours you get.

---

## 2. Dusk's Epitaph, step by step

**The file:** `assets/charter/textures/item/dusk_epitaph.png`

| | |
|---|---|
| Size | **32 x 32** (16 x 16, 64 x 64 or any other power of two also works; the sword stays the same size on screen, only the pixels get finer) |
| Format | PNG with a transparent background (RGBA) |
| Orientation | Drawn like a vanilla sword: **hilt at the bottom left, point at the top right**. The game rotates it into the hand for you. |

That single file is used everywhere: the inventory icon, the hotbar, the sword in your hand in first and third
person, the item on the ground, in item frames, and the two-handed version (which is the same picture with a
different hold). So one image is all it takes.

**Steps**

1. Draw (or open) your 32 x 32 image. Free tools that work well: Piskel (piskelapp.com, in the browser), LibreSprite,
   GIMP, or the texture editor in Blockbench.
2. Save it as `dusk_epitaph.png` (exactly that name).
3. Put it into your resource pack at `assets/charter/textures/item/dusk_epitaph.png`, replacing the template's copy.
4. Load the game, press **F3 + T**, and open the *Charter* creative tab to check the icon.
5. Hold it, then press **F5** to look at it in third person. Then hold it with both hands (the off hand must be
   empty) to see the two-handed hold.

**If the sword sits wrongly in the hand** (your art is bigger or smaller than the original or has another angle),
change how the model is held instead of redrawing it. Put copies of these two files into your pack and edit them:

* `assets/charter/models/item/dusk_epitaph.json` (one-handed hold; first and third person, both hands)
* `assets/charter/models/item/dusk_epitaph_twohanded.json` (two-handed hold, third person)

Inside each file the block `"display"` has an entry per view (`thirdperson_righthand`, `firstperson_righthand`,
`ground`, `fixed`, ...). Each entry has three values:

| value | meaning |
|---|---|
| `rotation` | degrees around x, y and z |
| `translation` | offset in sixteenths of a block (x, y, z) |
| `scale` | size multiplier (x, y, z) |

The easy way to find good numbers is Blockbench (free): open the model file with **File > Open Model**, switch to the
**Display** tab, drag the sword into place for each view, then copy the numbers back into the JSON. The left-hand
entries (`..._lefthand`) are the mirror image of the right-hand ones: keep the same translation and scale and flip the
sign of the y and z rotation.

---

## 3. Every texture of the mod

All paths below start with `assets/charter/textures/`.

### Items (`item/`)

| File | Size | What it is |
|---|---|---|
| `dusk_epitaph.png` | 32 x 32 | Dusk's Epitaph |
| `lesser_divinity.png` | 16 x 16 | Lesser Divinity |
| `fractured_lesser_divinity.png` | 16 x 16 | Fractured Lesser Divinity |
| `lesser_divinity_fragment.png` | 16 x 16 | Fragment of the Lesser Divinity |
| `divine_dominance.png` | 16 x 16 | Divine Dominance (the item; the pillar is below) |
| `sigil_staff.png` | 16 x 16 | Sigil Staff |
| `charter_contract.png` | 16 x 16 | Blank contract |
| `signed_contract.png` | 16 x 16 | Signed contract |
| `token_of_absolution.png` | 16 x 16 | Token of Absolution |
| `unbound_helmet.png`, `unbound_chestplate.png`, `unbound_leggings.png`, `unbound_boots.png` | 16 x 16 | Icons of the Regalia of the Unbound |

Item textures can be any power-of-two square size (16, 32, 64, ...). Their models are in `assets/charter/models/item/`.

### Effects and Divine Dominance (`entity/`)

| File | Size | Where you see it | How it is drawn |
|---|---|---|---|
| `charter_sigil.png` | 32 x 32 (any square) | On the ground under whoever Dusk's Epitaph, the Lesser Divinity or the fractured Lesser Divinity judges, in the shockwave when the beam lands, on the ground under Divine Dominance, and on all four sides of its pillar | Drawn flat, **north at the top**, turning slowly, fullbright and **not tinted**: the colours you paint are the colours you get. Transparent pixels show the ground. |
| `charter_chain.png` | 32 x 3 | Every golden chain: the ones that wind around the condemned and the ones around the pillar | A strip that is repeated along the chain (one repeat per 1.6 blocks), fullbright, not tinted, visible from both sides. The top row is the top edge of the chain. Keep roughly the 32 : 3 shape, or the links look stretched. |
| `staff_sigil.png` | 256 x 256 (any square) | Only the Sigil Staff | Drawn twice (full size and 60 % size, turning against each other) and **tinted gold**, so paint it white or grey. |
| `beam.png` | 64 x 256 | The columns of light, and the blade of Dusk's Epitaph when it turns into light | Repeats every 24 blocks vertically, tinted gold. |
| `cage.png` | 128 x 128 | The walls of Divine Dominance | Repeats every 4 blocks, tinted gold (redder as the walls close in). |
| `dominance_pillar.png` | 64 x 64 | The stone of the Divine Dominance pillar | See the layout below. |

**Layout of `dominance_pillar.png`** (the pillar is 1.5 blocks wide and 3 blocks tall; it uses the same picture on all
four sides, and the sigil and chains are drawn on top from `charter_sigil.png` and `charter_chain.png`):

```
  x:   0                32                64
  y: 0 +----------------+-----------------+
     | capital  32 x 8 |  (unused)        |
   8 +--+----------+---+                  |
     |  | shaft    |   +-----------------+ 16
     |  | 24 x 48  |   | ledge stone      |
     |  | (x 4-27) |   | 32 x 16          |
     |  |          |   +-----------------+ 32
     |  |          |   |                  |
     |  |          |   | top of the       |
  56 +--+----------+---+ capital 32 x 32  |
     | plinth 32 x 8   |                  |
  64 +-----------------+------------------+
```

* The left half is the front of the pillar, from the top: the capital (the top 8 rows, full width), the shaft (rows 8
  to 55, only the middle 24 columns, x 4 to 27) and the plinth (the bottom 8 rows, full width). The sigil is drawn
  over the middle of the shaft, so a darker panel there makes it stand out.
* The ledge stone (x 32 to 63, y 16 to 31) is the flat ring where the plinth and the capital are wider than the shaft.
* The top of the capital (x 32 to 63, y 32 to 63) is what you see from above.
* The pixels must be fully opaque in the areas that are used.

### Worn armor (`models/armor/`)

| File | Size | Notes |
|---|---|---|
| `unbound.png` | 128 x 48 | The texture of the 3D model of the Regalia of the Unbound, when worn |
| `unbound_glow.png` | 128 x 48 | The glowing parts (drawn fullbright over the first one); same layout |
| `unbound_layer_1.png`, `unbound_layer_2.png` | 64 x 32 | Plain vanilla-style armor layers, only used if the 3D model cannot be drawn |

The layout of `unbound.png` follows the model's boxes; the simplest way to restyle it is to open the existing file in an
image editor and paint over it, keeping the same shapes in the same places.

---

## 4. Changing the textures inside the project instead

If you would rather make your textures part of the mod itself (for example to give the finished `.jar` to other
people), copy your PNG over the file of the same name in
`src/main/resources/assets/charter/textures/...`, push the project to GitHub, and let the *Build* action produce a new
jar. The paths and sizes are exactly the ones in the tables above.

---

## 5. When something goes wrong

| What you see | Likely cause |
|---|---|
| A purple-and-black checkerboard | The file name or folder is not exactly right (check upper/lower case and `.png` after a `.png`, a common slip on Windows with hidden extensions). |
| Nothing changed | The pack is not enabled, or it is below another pack that also has the file. Press **F3 + T** after changing files. |
| The pack does not appear in the list | `pack.mcmeta` is missing, or the folder is nested one level too deep (the pack's top folder must directly contain `pack.mcmeta` and `assets`). |
| The sword is held upside down or sideways | The drawing is rotated: draw the hilt at the bottom left and the point at the top right, or adjust the `display` values (section 2). |
| The chain looks stretched or blurry | The strip is not about 32 x 3 (or a multiple, such as 64 x 6). |
| The sigil looks cut off at the edges | The sigil is not square, or it touches the border of the image: leave a few transparent pixels around it. |
| Edges look white or fringed | Save the PNG with straight (not premultiplied) alpha and no background layer. |
