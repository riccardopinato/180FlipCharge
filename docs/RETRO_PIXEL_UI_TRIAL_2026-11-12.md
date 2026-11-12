# 180FlipCharge — Retro Pixel visual trial (12/11/2026)

## Scope

Small, reversible UI trial on the **existing Kotlin / Jetpack Compose** app, not a Flutter migration. Based on Design-App references:
- `UIR-0001`: retro desktop / Y2K visual language.
- `UIR-0012`: pixel-window controls.
- `UIR-0035`: reference for visible switch/quick-control states (influence only; exact shape not copied).

The original user-supplied Pinterest images remain reference sources; **no third-party JPG, logo or asset is vendored** into this app.

## Implemented surfaces

1. Home orientation hero: less-rounded card and a seven-block original Compose pixel motif; existing value, title, operational health text, colors, state mapping stay unchanged.
2. Main 180° button: a visible 180° tile, slightly squarer outline, right-hand pixel motif and 64dp minimum height. Original localized action label, permission callback and toggle behavior preserved.
3. Two auto-mode cards: slight corner radius adjustment to align the visual language.

Only `MainScreen.kt` and `DesignComponents.kt` contain code changes; no dependencies, native service, permissions, strings, data stores, AndroidManifest, package identity or product billing are modified.

## QA / rollback

- Required: GitHub Development checks on exact PR head: `gradle :app:test`, `gradle :app:lintRelease`, `gradle :app:checkReleaseSize`.
- Visual/manual pending: light/dark, 360dp small phone, large font/TalkBack, overlay permission absent/present, state ACTIVE/WAITING/IDLE/ERROR, rotation 0°/180°, tablet if applicable.
- This is not a signed APK or physical-device certification.
- Rollback: revert the PR's UI commit, without data migrations or uninstalling the app.
- Factory baseline: `riccardopinato/App-Factory-Core@c5adc147046beb7024629466d10eeb799c3c2619` (Master v27, Golden index v29).
- Donor linkage: Design-App catalog `UI-UX/PRIORITA_VISUALI_PER_APP_2026-11-12.md`; donor state `REFERENCE_ONLY`. Do **not** label this a Certified Golden or Copy Ready.
