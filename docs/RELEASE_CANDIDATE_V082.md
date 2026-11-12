# Rotate 180° — pre-publication release candidate v0.8.2

**Prepared from:** audited v0.8.1 source `6836c61571c90a0726a3b21ca00e31b492100088` plus docs-only audit commit `4be769b89427c088b072e44bcb574f3c005b51a2`.
**Objective:** resolve source/documentation claims and provide audit-ready materials; no new orientation features.

## Implemented in proposed v0.8.2
- Fix `permanent_180_description` / `permanent_180_short` in all 6 locales: only 0°↔180° portrait, not 90°/270° landscape.
- Expand Usage Access on-device explanation in all 6 locales.
- Add in-app Settings → Privacy Policy link to publicly served IT/EN web page.
- VersionCode 12 / versionName 0.8.2, original package and persistent internal release signing.
- Replace stale README version and document source truth.
- 6 draft localized Play listings + asset/evidence guidelines.
- Automated source checks: localized claims, signed workflow labels, privacy Settings link, source policy, package/version.
- Source-copy of public privacy HTML in `docs/privacy/index.html`.

## Evidence and what remains
- All source changes must pass exact-head CI: `verify_release_readiness.py`, JVM tests, release lint, size budget.
- Then build signed APK/AAB and verify signing digest, checksums, package and version.
- v0.8.1 previously passed these build checks but is **not** the same binary as candidate v0.8.2.
- Android real-device compatibility, app backup behavior, TalkBack, battery restrictions, signer update, FGS video, Play Console acceptance, payments and final merchant identity are **NOT VERIFIED**.
- No screenshot should be taken from the web UI prototype and labelled as actual Android.
- Privacy Policy is online, but final legal contact still needs approval.

## No-Go gate
Publishing paid production remains blocked until FGS permission justification, device testing and Console declarations are recorded with evidence and no P0/P1 failures remain.
