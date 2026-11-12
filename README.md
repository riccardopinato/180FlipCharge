# Rotate 180°

Lightweight Android utility that adds reliable reverse portrait / 180° rotation controls.

## Current baseline

- Display name: `Rotate 180°`
- Legacy package identity: `com.flipcharge.app`
- Version: `0.8.2` (`versionCode 12`)
- Native Kotlin + Jetpack Compose
- Android `minSdk 26`, `targetSdk 36`
- Manual fixed 180° mode
- Permanent portrait flip with 0° / 180° only — never 90° / 270°
- Charging automation
- Quick Settings Tile
- App Exceptions using Usage Access
- Boot restore
- Foreground-service based background reliability
- Release R8 + resource shrinking
- Release APK budget: **8 MiB**
- Release AAB budget: **10 MiB**
- Commercial model: **paid upfront €4.99; no ads**
- Minimal dark `180°` launcher icon (store asset redesign pending review)

## Signing

Release builds use a persistent INTERNAL signing identity supplied only through GitHub Actions Secrets. No private key or password belongs in the repository. See `SIGNING.md`.

## Build

GitHub Actions is the canonical build path. `Development checks` validates test/lint/release/size. `Build signed release` is available on main after an explicitly marked signed-release commit (or manual dispatch) and requires the four Android signing secrets.

## Google Play release readiness

- Production remains **NO-GO** until device QA and Play Console review.
- [Privacy Policy](https://rotate180-privacy.vercel.app/) (IT/EN, publisher identity and contact pending final verification).
- [48-item audit checklist](RELEASE_CHECKLIST.md) and [test runbook](docs/PREPUBLICATION_AUDIT_V081_2026-11-12.md).
- The Auto 180° orientation engine supports **upright 0° and reverse portrait 180° only**; never promises landscape modes.
- [Store listing drafts](store/listings/) and [ASO asset plan](store/ASSETS_SPEC.md) — not Play-approved.

## Factory baseline

App Factory Core baseline used for this fix: `1b4d39a558bff76d08ea7ccc1ce44d6cdb398e1e`.


## Template Notion

[Catalogo di ricerca](docs/TEMPLATE_NOTION/README.md): nessun template assegnato in questa fase.
