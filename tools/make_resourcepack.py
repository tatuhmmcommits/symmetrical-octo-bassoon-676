#!/usr/bin/env python3
"""Builds charter-texture-pack-template.zip: a resource pack that already holds every texture of the mod (and the
two models that decide how Dusk's Epitaph is held) at exactly the right paths. Overwrite the files you want to
change with your own PNGs, put the pack in your resourcepacks folder and enable it. See docs/TEXTURES.md.

Usage:  python3 tools/make_resourcepack.py [output.zip]      (standard library only)
"""
import os
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, 'src', 'main', 'resources', 'assets', 'charter')
GUIDE = os.path.join(ROOT, 'docs', 'TEXTURES.md')

PACK_MCMETA = """{
	"pack": {
		"pack_format": 15,
		"description": "Charter (fan recreation): put your own textures here"
	}
}
"""

READ_ME = """CHARTER TEXTURE PACK TEMPLATE
=============================

This is a resource pack for Minecraft 1.20.1. Every texture of the Charter mod is in it, at exactly the path the mod
looks for. Replace any file with your own PNG (same name, same folder) and the game uses yours.

1. Replace the picture(s) you want, for example
       assets/charter/textures/item/dusk_epitaph.png        (Dusk's Epitaph, 32 x 32)
       assets/charter/textures/entity/charter_sigil.png     (the sigil on the ground and on the pillar, 32 x 32)
       assets/charter/textures/entity/charter_chain.png     (the chains, 32 x 3)
2. Put this folder (or the zip) into your  .minecraft/resourcepacks  folder.
3. In the game: Options > Resource Packs, enable the pack and keep it above the others. Press F3 + T to reload
   after you change a file.

Keep the file names and folders exactly as they are (lower case). You can delete every file you did not change, but
keep pack.mcmeta.

GUIDE.md has the full list of textures with their sizes, how Dusk's Epitaph is held (and how to adjust it if your
art needs it), and a troubleshooting table.
"""


def main():
    out = sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, 'charter-texture-pack-template.zip')
    count = 0
    with zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as z:
        z.writestr('pack.mcmeta', PACK_MCMETA)
        z.writestr('READ-ME-FIRST.txt', READ_ME)
        if os.path.exists(GUIDE):
            z.write(GUIDE, 'GUIDE.md')
        # a pack icon: the sigil (the game scales any square image)
        sigil = os.path.join(ASSETS, 'textures', 'entity', 'charter_sigil.png')
        if os.path.exists(sigil):
            z.write(sigil, 'pack.png')
        for folder, _, files in os.walk(os.path.join(ASSETS, 'textures')):
            for name in sorted(files):
                if name.endswith('.png'):
                    full = os.path.join(folder, name)
                    z.write(full, 'assets/charter/' + os.path.relpath(full, ASSETS).replace(os.sep, '/'))
                    count += 1
        for name in ('dusk_epitaph.json', 'dusk_epitaph_twohanded.json'):
            full = os.path.join(ASSETS, 'models', 'item', name)
            if os.path.exists(full):
                z.write(full, 'assets/charter/models/item/' + name)
    print('wrote %s (%d textures)' % (out, count))


if __name__ == '__main__':
    main()
