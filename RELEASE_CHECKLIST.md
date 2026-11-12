# Rotate 180° — Release readiness matrix (v0.8.1)

**Audited:** 2026-11-12 · Source `riccardopinato/180FlipCharge@6836c61571c90a0726a3b21ca00e31b492100088` · Package `com.flipcharge.app` · `versionCode=11`.

**Status: NO-GO for production.** This is a test/evidence plan, **not** a claim that tests or Play review have passed.
The original 34 unchecked release items are preserved and mapped 1:1 to the identifiers below. An additional 14 cross-cutting launch gates are added without changing app features.

### Evidence already VERIFIED from GitHub

- Signed build: https://github.com/riccardopinato/180FlipCharge/actions/runs/43255813788 — SUCCESS, exact SHA above.
- `:app:test`, `:app:lintRelease`, `:app:checkReleaseSize` — SUCCESS in signed workflow; APK, AAB and evidence artifacts uploaded.
- APK signing certificate SHA-256: `1D:4B:E8:F0:86:62:B1:51:77:9C:54:95:28:8D:8D:8D:16:82:31:FC:FD:27:04:4E:33:B3:74:6E:C1:C1:79:48` — verified by CI.
- App `targetSdk 36`, `minSdk 26`, release `versionCode 11`, `versionName 0.8.1` — source static verified.
- All other rows remain **PENDING** until their own evidence is attached; source presence ≠ physical acceptance.

### Status discipline

`PENDING` = no accepted evidence; `BLOCKED` = cannot execute; `FAIL` = disproven by test/static audit; `PASS` = evidence attached and reviewed.
`P0` blocks production; `P1` blocks store readiness/release quality; `P2` commercial optimization that may be consciously deferred.
`DEVICE` requires physical proof, `CONSOLE` Play Console/reviewer proof, `STATIC` source/artifact comparison.
Before changing PASS, append: tester, UTC date, device/Android build if applicable, source commit, APK/AAB SHA-256, method, expected/actual, screenshots/video/log path. Redact personal installed-app lists.

## Original 34 open checklist items, mapped one-to-one

| ID | Area | Item | Priority | Gate | Status |
|---|---|---|---|---|---|
| DEV-01 | Device | Test Samsung S23/S24 | **P0** | DEVICE | PENDING |
| DEV-02 | Device | Test Xiaomi Redmi Note 10 Pro | **P0** | DEVICE | PENDING |
| DEV-03 | Device | Restore after reboot | **P0** | DEVICE | PENDING |
| DEV-04 | Device | Force-stop recovery | **P0** | DEVICE | PENDING |
| DEV-05 | Permissions | Overlay revoke and regrant | **P0** | DEVICE | PENDING |
| DEV-06 | Permissions | Usage Access revoke and regrant | **P0** | DEVICE | PENDING |
| DEV-07 | Device | Quick Settings tile | **P0** | DEVICE | PENDING |
| DEV-08 | Device | Charging connect/disconnect | **P0** | DEVICE | PENDING |
| DEV-09 | Device | Portrait-only Auto 0↔180 | **P0** | DEVICE | PENDING |
| DEV-10 | Device | App Exceptions over five minutes | **P0** | DEVICE | PENDING |
| DEV-11 | Device | Screen off/on | **P0** | DEVICE | PENDING |
| DEV-12 | Device | Battery Saver / OEM restrictions | **P0** | DEVICE | PENDING |
| DEV-13 | Localization | Six languages EN/IT/ES/FR/PT/DE | **P1** | DEVICE | PENDING |
| DEV-14 | Accessibility | Dark theme and font scaling | **P1** | DEVICE | PENDING |
| PLAY-01 | Play policy | FGS specialUse declaration | **P0** | CONSOLE | PENDING |
| PLAY-02 | Play policy | Foreground-service demonstration video | **P0** | CONSOLE | PENDING |
| PLAY-03 | Play policy | Usage Access disclosure and Data safety | **P0** | CONSOLE | PENDING |
| PLAY-04 | Play policy | Privacy Policy finalized | **P0** | CONSOLE | PENDING |
| PLAY-05 | Commerce | Paid listing €4.99 | **P0** | CONSOLE | PENDING |
| PLAY-06 | Commerce | Initial promotion policy/choice | **P2** | CONSOLE | PENDING |
| PLAY-07 | Release | Production AAB exact artifact | **P0** | STATIC | PENDING |
| ASO-01 | Store listing | Final store title | **P1** | CONSOLE | PENDING |
| ASO-02 | Store listing | Short description | **P1** | CONSOLE | PENDING |
| ASO-03 | Store listing | Long description | **P1** | CONSOLE | PENDING |
| ASO-04 | Store listing | Keyword and competitor review | **P2** | CONSOLE | PENDING |
| ASO-05 | Store listing | Localized listing markets | **P1** | CONSOLE | PENDING |
| SHOT-01 | Creative | First screenshot: cable problem | **P1** | DEVICE | PENDING |
| SHOT-02 | Creative | Before/after: cable bottom/top | **P1** | DEVICE | PENDING |
| SHOT-03 | Creative | Permanent Auto 180 | **P1** | DEVICE | PENDING |
| SHOT-04 | Creative | Charging automation | **P1** | DEVICE | PENDING |
| SHOT-05 | Creative | App Exceptions | **P1** | DEVICE | PENDING |
| SHOT-06 | Creative | Quick Settings Tile | **P1** | DEVICE | PENDING |
| SHOT-07 | Creative | Lightweight / no ads statement | **P1** | CONSOLE | PENDING |
| SHOT-08 | Creative | Localized screenshot captions | **P1** | CONSOLE | PENDING |

## Additional audit-discovered items

| ID | Area | Item | Priority | Gate | Status |
|---|---|---|---|---|---|
| EXTRA-01 | Compatibility | Update in place | **P0** | DEVICE | PENDING |
| EXTRA-02 | Signing | Play App Signing setup | **P0** | CONSOLE | PENDING |
| EXTRA-03 | Accuracy | Correct 90°/270° misleading text | **P0** | STATIC | FAIL |
| EXTRA-04 | Privacy | Local data backup behavior | **P0** | DEVICE | PENDING |
| EXTRA-05 | Privacy | Network/data flows and SDKs | **P0** | STATIC | PENDING |
| EXTRA-06 | Console | App creation, account eligibility and payments | **P0** | CONSOLE | PENDING |
| EXTRA-07 | Console | Closed testing eligibility | **P0** | CONSOLE | PENDING |
| EXTRA-08 | Console | App content declarations | **P0** | CONSOLE | PENDING |
| EXTRA-09 | Store assets | Listing artwork spec | **P1** | CONSOLE | PENDING |
| EXTRA-10 | Documentation | README version and feature correctness | **P1** | STATIC | FAIL |
| EXTRA-11 | Visual QA | Native UI parity with approved V2 web | **P1** | DEVICE | PENDING |
| EXTRA-12 | Accessibility | Permissions/notification with notifications denied | **P0** | DEVICE | PENDING |
| EXTRA-13 | Privacy | Public privacy URL and in-app link | **P0** | CONSOLE | PENDING |
| EXTRA-14 | Release | No exact-artifact visual certification | **P0** | DEVICE | PENDING |

## Blockers and release decision

**Current code-level finding (FAIL):** `EXTRA-03` — all language resource files contain preexisting text implying `90° / 270°` in Auto; engine `RotationOrientationEngine` deliberately maps only PORTRAIT/REVERSE_PORTRAIT. Correct text, rebuild/sign new versionCode, retest. The existing signed v0.8.1 remains the audit baseline, **not production-approved**.

**Current documentation finding (FAIL):** `EXTRA-10` — `README.md` states v0.7.1/versionCode 9 while Gradle is v0.8.1/versionCode 11.

**Release blockers without evidence:** DEV-01..14, PLAY-01..05, PLAY-07, ASO-01..03, ASO-05, SHOT-01..08, EXTRA-01..09, EXTRA-11..14. `PLAY-06` and `ASO-04` are eligible for deferred status with explicit decision.

Do not upload to **production** unless **all P0/P1 rows** are PASS and the Play Console required declarations are accepted; an internal test track may precede completion of external listing requirements where allowed.

### Execution instructions

Procedures, expected pass criteria, and evidence format for **every ID** are in [the audited test runbook](docs/PREPUBLICATION_AUDIT_V081_2026-11-12.md).
Core policy declaration draft and video shot list: [FGS](docs/PLAY_FGS_DECLARATION_V081.md).
Privacy/data-handling inventory: [Privacy & Data Safety](docs/PRIVACY_DATA_SAFETY_V081.md).

### Google authoritative references checked on 2026-11-12

- FGS declaration/video: https://support.google.com/googleplay/android-developer/answer/13392821
- FGS permission policy: https://support.google.com/googleplay/android-developer/answer/18265346
- User Data and Privacy Policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Data safety form: https://support.google.com/googleplay/android-developer/answer/10787469
- Target API levels: https://support.google.com/googleplay/android-developer/answer/11926878
- Closed test eligibility: https://support.google.com/googleplay/android-developer/answer/14151465
- Play App Signing: https://developer.android.com/studio/publish/app-signing
