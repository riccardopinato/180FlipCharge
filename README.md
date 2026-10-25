# Rotate 180°

Lightweight Android utility that adds reliable reverse portrait / 180° rotation controls.

## Current baseline

- Display name: `Rotate 180°`
- Legacy package identity: `com.flipcharge.app`
- Version: `0.7.0` (`versionCode 8`)
- Native Kotlin + Jetpack Compose
- Android `minSdk 26`, `targetSdk 36`
- Manual fixed 180° mode
- Permanent auto rotation with 0° / 90° / 180° / 270°
- Charging automation
- Quick Settings Tile
- App Exceptions using Usage Access
- Boot restore
- Foreground-service based background reliability
- Release R8 + resource shrinking
- Release APK budget: **8 MiB**
- Release AAB budget: **10 MiB**
- Commercial model: **paid upfront €4.99; no ads**
- Minimal black `180°` pixel-art adaptive launcher icon

## Signing

Release builds use a persistent INTERNAL signing identity supplied only through GitHub Actions Secrets. No private key or password belongs in the repository. See `SIGNING.md`.

## Build

GitHub Actions is the canonical build path. `Development checks` validates test/lint/release/size. `Build signed release` is manual and requires the four Android signing secrets.

## Factory baseline

App Factory Core baseline used for this consolidation: `4cc0b94467b94ab6fabe2c927fcc58afff49e39a`.
