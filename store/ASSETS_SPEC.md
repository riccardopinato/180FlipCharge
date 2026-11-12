# Google Play artwork — Rotate 180° v0.8.2

**Status:** ASSET PLAN / NOT YET APPROVED. The production screenshots must be captured from the actual signed Android build on a physical device. The approved Vercel web design is NOT an authentic Android screenshot.

## Required exports
- High-resolution store icon: PNG 512×512 (separate from the small adaptive launcher icon).
- Feature graphic: PNG/JPEG 1024×500, sRGB, no alpha, communicates the real 0°↔180° cable problem, not a fake feature.
- At least two real phone screenshots: supported PNG/JPEG dimensions according to current Play Console validation.
- Any screenshot with personally identifying installed apps must be redacted.
- The language of the caption must match the listed Play Store locale.
- Keep original unaltered capture, edited-caption copy, device/OS metadata, tested release SHA, tester and timestamp.

## Priority screen captures
1. Charging cable blocks comfortable portrait grip — real cable and phone, photo only if actual use case passes QA.
2. Manual 180° active and OFF — native Neo Tactile Dark home, not browser.
3. Auto 180° normal/reverse portrait. **Never show 90°/270° rotation as supported.**
4. Charging automation connected/disconnected.
5. Quick Settings tile on/off.
6. Exceptions list with permission explanation; hide private installed-app details.
7. Settings with Privacy Policy link.
8. No ads / lightweight copy only after release dependency, merged Manifest and crash checks.

## Caption proposals (IT / EN)
| Visual | IT | EN |
|---|---|---|
| Hero | Quando il cavo dà fastidio, ruota di 180° | Flip 180° when the cable gets in the way |
| Manual | Comando manuale immediato | Manual control when you need it |
| Auto | Rotazione automatica verticale 0°↔180° | Automatic portrait 0°↔180° |
| Charging | Rotazione intelligente mentre ricarichi | Flip while charging |
| Exceptions | Le app escluse restano normali | Choose apps to exclude |
| Tile | Controllo dalle Impostazioni rapide | Quick Settings control |
| Privacy | Permessi chiari e gestibili | Clear, manageable permissions |

## Artwork validation
- No web preview shown as installed app.
- No fake devices, mock notifications, fake Google Play ratings/reviews or unverifiable OEM compatibility logos.
- Create visually consistent Neo Tactile Dark high-res icon and feature graphic in separate approved design step.
- Verify trademark/licensing rights and avoid copying Design-App references directly.

Google official asset requirements: https://support.google.com/googleplay/android-developer/answer/9866151
