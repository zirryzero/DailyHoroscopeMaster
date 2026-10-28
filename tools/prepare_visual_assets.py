"""Package generated artwork for Android and create review contact sheets.

Usage: python tools/prepare_visual_assets.py docs/design/generated-assets.json
Requires Pillow. This only resizes/converts generated art; it does not draw it.
"""
import json
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageOps

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'app/src/main/res'
DOCS = ROOT / 'docs/design'
SIGNS = ['aries', 'taurus', 'gemini', 'cancer', 'leo', 'virgo', 'libra',
         'scorpio', 'sagittarius', 'capricorn', 'aquarius', 'pisces']


def contact_sheet(paths, destination, cell=(240, 280), columns=4):
    width, height = cell
    sheet = Image.new('RGB', (width * columns, height * ((len(paths) + columns - 1) // columns)), '#091426')
    draw = ImageDraw.Draw(sheet)
    for index, (name, path) in enumerate(paths):
        im = Image.open(path).convert('RGBA')
        im.thumbnail((width - 32, height - 48), Image.Resampling.LANCZOS)
        x = index % columns * width + (width - im.width) // 2
        y = index // columns * height + 12
        sheet.paste(im, (x, y), im)
        draw.text((index % columns * width + 16, (index // columns + 1) * height - 24), name, fill='#F2E8CE')
    sheet.save(destination)


def main():
    manifest = json.loads(Path(sys.argv[1]).read_text(encoding='utf-8-sig'))
    paths = {key: ROOT / value for key, value in manifest['assets'].items()}
    for sign in SIGNS:
        im = Image.open(paths[sign]).convert('RGBA')
        assert im.getchannel('A').getextrema()[0] == 0, f'{sign}: missing transparency'
        im = im.crop(im.getchannel('A').getbbox())
        im = ImageOps.contain(im, (336, 336), Image.Resampling.LANCZOS)
        canvas = Image.new('RGBA', (384, 384))
        canvas.paste(im, ((384-im.width)//2, (384-im.height)//2))
        canvas.save(RES / 'drawable' / f'{sign}.png', optimize=True)

    for key, path in paths.items():
        if key.startswith('background_'):
            im = Image.open(path).convert('RGB')
            im = ImageOps.fit(im, (720, 1600), Image.Resampling.LANCZOS, centering=(0.5, 1.0))
            im.save(RES / 'drawable' / f'{key}.png', optimize=True)

    brand = Image.open(paths['brand']).convert('RGB')
    brand = ImageOps.fit(brand, (512, 512), Image.Resampling.LANCZOS)
    brand.save(ROOT / 'app/src/main/ic_launcher-playstore.png', optimize=True)
    brand.save(RES / 'drawable/icono.png', optimize=True)
    for density, size, foreground_size in [('mdpi', 48, 108), ('hdpi', 72, 162), ('xhdpi', 96, 216), ('xxhdpi', 144, 324), ('xxxhdpi', 192, 432)]:
        folder = RES / f'mipmap-{density}'
        for name in ['ic_launcher', 'ic_launcher_round']:
            brand.resize((size, size), Image.Resampling.LANCZOS).save(folder / f'{name}.webp', lossless=True)
        brand.resize((foreground_size, foreground_size), Image.Resampling.LANCZOS).save(folder / 'ic_launcher_foreground.webp', lossless=True)

    contact_sheet([(sign, RES / 'drawable' / f'{sign}.png') for sign in SIGNS], DOCS / 'zodiac-preview.jpg')
    contact_sheet([(key, RES / 'drawable' / f'{key}.png') for key in paths if key.startswith('background_')], DOCS / 'backgrounds-preview.jpg', (240, 460))
    print('Packaged 12 zodiac icons, 13 backgrounds, and launcher assets.')


if __name__ == '__main__':
    main()
