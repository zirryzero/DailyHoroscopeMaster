# Celestial artwork refresh

## English

The artwork uses a midnight navy and indigo background, ivory zodiac illustrations, and warm gold accents. Zodiac images contain no labels: Android renders the localized sign names separately.

- `generated/`: original selected artwork produced with the built-in image generation tool.
- `generated-assets.json`: asset mapping and generation prompts.
- `zodiac-preview.jpg`: contact sheet of the twelve packaged zodiac icons.
- `backgrounds-preview.jpg`: contact sheet of the thirteen packaged backgrounds.
- `screenshots/`: actual captures of the refreshed application on an Android emulator.
- `previous-screenshots/`: historical screenshots moved out of Android resources so they are not bundled in the APK.

Android resources retain their existing names. Icons are packaged as 384 × 384 transparent PNGs; backgrounds as 720 × 1600 PNGs. Launcher artwork is exported for each existing density and the Play Store image. Adaptive launcher icons include an inset to keep the central mark clear of the mask boundary.

To reproduce the packaging after changing the selected source artwork:

```powershell
python tools/prepare_visual_assets.py docs/design/generated-assets.json
```

The script requires Pillow and only resizes/converts generated assets and creates review contact sheets. Source artwork remains in `generated/`.

## Español

La colección utiliza fondos azul medianoche e índigo, ilustraciones zodiacales marfil y acentos dorados. Las imágenes de los signos no incluyen nombres: Android muestra los textos traducidos por separado.

- `generated/`: originales seleccionados, creados con la herramienta integrada de generación de imágenes.
- `generated-assets.json`: relación de archivos y prompts de generación.
- `zodiac-preview.jpg`: vista de conjunto de los doce iconos preparados para Android.
- `backgrounds-preview.jpg`: vista de conjunto de los trece fondos preparados para Android.
- `screenshots/`: capturas reales de la aplicación renovada en un emulador Android.
- `previous-screenshots/`: capturas históricas trasladadas fuera de los recursos Android para no incluirlas en el APK.

Los recursos Android conservan sus nombres. Los iconos se preparan como PNG transparentes de 384 × 384; los fondos, como PNG de 720 × 1600. El icono de inicio se exporta para las densidades existentes y para Google Play. Los iconos adaptativos incluyen un margen para proteger el símbolo del recorte de Android.

Para repetir la preparación después de cambiar los originales seleccionados:

```powershell
python tools/prepare_visual_assets.py docs/design/generated-assets.json
```

El script requiere Pillow y solo redimensiona o convierte los recursos generados y crea las vistas de revisión. Los originales se conservan en `generated/`.
