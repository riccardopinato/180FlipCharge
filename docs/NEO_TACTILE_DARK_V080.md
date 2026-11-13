# Neo Tactile Dark Android — v0.8.0

Source: approved interactive web V2, Design-App UIR-0025, UIR-0035, UIR-0030, UIR-0017.

Native Jetpack Compose implementation: tactile brushed switch thumb, recessed teal track,
circular status dial, dark layered surfaces, vertical automations, compact utility rows.
Runtime logic and callbacks remain Android-native; no WebView, raster images,
external fonts or additional Gradle dependencies.

Update contract:
- applicationId com.flipcharge.app (unchanged)
- versionCode 10 / versionName 0.8.0
- internal release signer SHA-256 must match SIGNING.md
- existing prefs, overlay permissions, foreground service and quick tile unchanged
- user can update in place only if installed signer matches and installed versionCode < 10.

Verification:
- Required GitHub Development checks (unit test, lintRelease, release size).
- Build signed release on merged main with commit marker [signed-release].
- Do not distribute an APK unless signer check and source SHA are verified.
- Physical visual QA still required (small phone, large text, states).
