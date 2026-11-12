# Pre-publication test runbook — Rotate 180° v0.8.1

**Date:** 2026-11-12 · **Audit baseline:** `6836c61571c90a0726a3b21ca00e31b492100088`.
**Status:** AUDIT-ONLY / NO-GO. This document is a reproducible plan; no physical-device test, Play review, or Console configuration is claimed.
**Master/Golden baseline:** App-Factory-Core `c5adc147046beb7024629466d10eeb799c3c2619` (pinned from prior consecutive session).

## Evidence capture contract

For each item, attach a file or link in your QA folder and add an evidence receipt:
```text
test_id:
source_commit: 6836c61571c90a0726a3b21ca00e31b492100088
apk_sha256: d74cd89b4b4c746002afdf4f036b3c7a0ae4c17c62f602be32824292d010dcb2
device_model:
android_version / OEM_version:
tester:
executed_at_UTC:
steps_executed:
expected:
actual:
result: PASS | FAIL | BLOCKED
evidence_uri_or_file:
issue_if_failed:
```
**Important:** the APK SHA above is the validated v0.8.1 signed APK. If code is corrected (for example the 90/270 copy bug), generate a new versionCode, SHA and new receipts. Do **not** reuse old PASS evidence without marking coverage.

## Original 34 test cases — procedure + precise pass oracle

### DEV-01 — Test Samsung S23/S24 [P0; PHYSICAL; PENDING]
**Procedure:** Install APK 0.8.1 on S23/S24; manual 180, auto 0↔180, charging, screen lock, home/app switching for 10 min.
**PASS only if:** No unexpected 90/270; controls show correct runtime status; no crash/ANR.
**Required evidence:** video + device model/Android build + logcat.

### DEV-02 — Test Xiaomi Redmi Note 10 Pro [P0; PHYSICAL; PENDING]
**Procedure:** Repeat DEV-01 on Redmi Note 10 Pro with MIUI/HyperOS settings recorded.
**PASS only if:** Rotation persists in supported states; no restricted-background fatal issue.
**Required evidence:** video + OEM battery settings screenshot + logcat.

### DEV-03 — Restore after reboot [P0; PHYSICAL; PENDING]
**Procedure:** Enable auto 180 and charging separately; reboot device physically; unlock; inspect service and tile.
**PASS only if:** Restored if permitted by Android/OEM; otherwise graceful blocked-state indication, no silent false ACTIVE.
**Required evidence:** before/after video + dumpsys activity services.

### DEV-04 — Force-stop recovery [P0; PHYSICAL; PENDING]
**Procedure:** Enable service; Settings > Apps > Force stop; observe; manually open app again.
**PASS only if:** No automatic restart promise after user force-stop; app can recover on explicit relaunch; status reflects reality.
**Required evidence:** screen recording + force-stop time/logcat.

### DEV-05 — Overlay revoke and regrant [P0; PHYSICAL; PENDING]
**Procedure:** Start 180; revoke SYSTEM_ALERT_WINDOW in Android settings; return, retry; regrant.
**PASS only if:** Failure safely shown, no crash, no false operational indication; after regrant user can reactivate.
**Required evidence:** recording + screenshots both states.

### DEV-06 — Usage Access revoke and regrant [P0; PHYSICAL; PENDING]
**Procedure:** Configure exclusion; revoke PACKAGE_USAGE_STATS app-op; switch apps for 5+ min; regrant.
**PASS only if:** Explicitly communicates disabled exclusions; no false protection claims; recovers after regrant.
**Required evidence:** video + appops status + timestamp.

### DEV-07 — Quick Settings tile [P0; PHYSICAL; PENDING]
**Procedure:** Add tile; activate/deactivate with screen unlocked, permission present/missing; compare main status.
**PASS only if:** Tile and app agree; missing permission opens app; no double toggling.
**Required evidence:** recording + screenshots + state log.

### DEV-08 — Charging connect/disconnect [P0; PHYSICAL; PENDING]
**Procedure:** Enable charging only; physically plug/unplug USB; repeat with manual and auto both enabled.
**PASS only if:** Only expected modes; priority manual > charging > auto, no stale charging effect.
**Required evidence:** video plug/unplug + runtime state.

### DEV-09 — Portrait-only Auto 0↔180 [P0; PHYSICAL; PENDING]
**Procedure:** Enable Auto and hold 0°,90°,180°,270°, then tilt near 45/135/225/315 for several seconds.
**PASS only if:** Only portrait 0/180 applied; horizontal states never force landscape; 380ms stability behaves acceptably.
**Required evidence:** video incl. external rotation indicator.

### DEV-10 — App Exceptions over five minutes [P0; PHYSICAL; PENDING]
**Procedure:** Select app using Usage Access; keep excluded app foreground for 6 min; return to another app.
**PASS only if:** Overlay released during excluded foreground; restored after switching; no indefinite stickiness.
**Required evidence:** 6 min timeline + snapshots + logcat.

### DEV-11 — Screen off/on [P0; PHYSICAL; PENDING]
**Procedure:** Auto on; lock/unlock 10 times incl. after screen-off >5min; check runtime and overlay.
**PASS only if:** No black screen, stuck orientation, or stale active; resume when allowed.
**Required evidence:** video + state at each step.

### DEV-12 — Battery Saver / OEM restrictions [P0; PHYSICAL; PENDING]
**Procedure:** Repeat background and charging tests under normal, battery saver, Xiaomi OEM restrictions.
**PASS only if:** Document supported/blocked cases; if service blocked, clear truthful guidance.
**Required evidence:** matrix + screenshots of permissions + logs.

### DEV-13 — Six languages EN/IT/ES/FR/PT/DE [P1; PHYSICAL; PENDING]
**Procedure:** Switch all six in Settings and system; inspect home, sub-pages, tile, notification, plural app counts.
**PASS only if:** No untranslated UI, cut-off action, incorrect rotation claim; no missing strings.
**Required evidence:** six screenshot bundles + locale matrix.

### DEV-14 — Dark theme and font scaling [P1; PHYSICAL; PENDING]
**Procedure:** Test smallest phone width 360dp and 420dp; 100%,130%,200% font scaling, TalkBack, contrast.
**PASS only if:** No clipped controls; switches announce state; tappable target at least 48dp; vertical scroll usable.
**Required evidence:** screenshots + accessibility scan results.

### PLAY-01 — FGS specialUse declaration [P0; PLAY CONSOLE; PENDING]
**Procedure:** Compare Manifest foregroundServiceType, permission, service notification & STOP to Google policy; fill declaration.
**PASS only if:** Play Console accepts exact reason/impacts; decision recorded, no assumed approval.
**Required evidence:** Console declaration screenshot + submitted text.

### PLAY-02 — Foreground-service demonstration video [P0; PLAY CONSOLE; PENDING]
**Procedure:** Record fresh Android device use: enable overlay, tap 180°, see persistent notification and STOP, Auto and charge.
**PASS only if:** Shows user action, user-visible continuous task, why cannot defer, and ability to stop; URL accessible to reviewers.
**Required evidence:** real demo URL + checklist + date.

### PLAY-03 — Usage Access disclosure and Data safety [P0; PLAY CONSOLE; PENDING]
**Procedure:** Inventory app list/usage events and SDKs, check network/backup paths, map data types, verify prominent explanation.
**PASS only if:** Truthful Play Console questionnaire, sensitive permission explained within flow, no unsupported no-data claim.
**Required evidence:** source-to-data map + Console form screenshots.

### PLAY-04 — Privacy Policy finalized [P0; PLAY CONSOLE; PENDING]
**Procedure:** Write and publish policy as public HTML; link from Settings/about and Play Console; verify disclosures and retention.
**PASS only if:** URL resolves without login, explicitly names app/developer and Usage Access/local/backup data handling.
**Required evidence:** URL + desktop/mobile screenshots + published APK proof.

### PLAY-05 — Paid listing €4.99 [P0; PLAY CONSOLE; PENDING]
**Procedure:** Create listing as paid from start; verify payments profile, saleable countries and base price.
**PASS only if:** Play Console shows Paid, €4.99 in required locale and merchant account ready.
**Required evidence:** Console pricing/payment screenshot.

### PLAY-06 — Initial promotion policy/choice [P2; PLAY CONSOLE; PENDING]
**Procedure:** Review eligible paid-app sale in Console; choose 2.99 only after commercial decision.
**PASS only if:** Document discount, dates, eligibility; no misleading in-app price strings.
**Required evidence:** Console sale draft or documented defer decision.

### PLAY-07 — Production AAB exact artifact [P0; STATIC; PENDING]
**Procedure:** Use source SHA 6836c615 and signed AAB from run 43255813788; verify upload signer and Play app signing certificate.
**PASS only if:** Bundle accepted in Play upload track; package, versionCode and certificate correct.
**Required evidence:** AAB SHA + Play upload receipt + Play App Signing fingerprint.

### ASO-01 — Final store title [P1; PLAY CONSOLE; PENDING]
**Procedure:** Validate Rotate 180° title and store policy/length, uniqueness and product promise.
**PASS only if:** Approved final title consistent with actual functionality.
**Required evidence:** title approved + Console screenshot.

### ASO-02 — Short description [P1; PLAY CONSOLE; PENDING]
**Procedure:** Draft localized concise value proposition; check no unsupported claim.
**PASS only if:** Within Console character limit; benefits verifiable on physical device.
**Required evidence:** Console preview + approved copy.

### ASO-03 — Long description [P1; PLAY CONSOLE; PENDING]
**Procedure:** Explain modes, overlay/usage permissions and 0/180-only auto; review six locales.
**PASS only if:** No unsupported 90/270 or guaranteed all-device claim; reviewer can reproduce.
**Required evidence:** versioned copy file + Console preview.

### ASO-04 — Keyword and competitor review [P2; PLAY CONSOLE; PENDING]
**Procedure:** Research live query terms, pricing and 5 comparable apps; no trademark misuse.
**PASS only if:** Evidence sheet with timestamp and justified final positioning.
**Required evidence:** research log + final title selection.

### ASO-05 — Localized listing markets [P1; PLAY CONSOLE; PENDING]
**Procedure:** Prioritize IT/EN then ES/FR/PT/DE, review by human for claims and grammar.
**PASS only if:** Approved localized descriptions + policies and visuals match language.
**Required evidence:** locale reviewer initials + Console previews.

### SHOT-01 — First screenshot: cable problem [P1; PHYSICAL; PENDING]
**Procedure:** Capture real app on phone in cable-at-bottom use case; show immediate benefit.
**PASS only if:** Store image truthful, legible, no composited fictional controls.
**Required evidence:** original screenshot + exported Play screenshot.

### SHOT-02 — Before/after: cable bottom/top [P1; PHYSICAL; PENDING]
**Procedure:** Physically photograph or record orientation change; screenshot accurately shows actual app.
**PASS only if:** Actual demonstrated behavior, not web concept, no misleading claims.
**Required evidence:** before-after original files + consent/licenses.

### SHOT-03 — Permanent Auto 180 [P1; PHYSICAL; PENDING]
**Procedure:** Record 0↔180 on real sensor with Auto enabled; use verified state screenshot.
**PASS only if:** Claim portrait-only 0/180; no 90/270.
**Required evidence:** video + source screenshot.

### SHOT-04 — Charging automation [P1; PHYSICAL; PENDING]
**Procedure:** Record plug/unplug with state and direction changes.
**PASS only if:** Accurate charge/standby captions.
**Required evidence:** source screenshot + video.

### SHOT-05 — App Exceptions [P1; PHYSICAL; PENDING]
**Procedure:** Capture list and real excluded app behavior; obscure sensitive installed apps as needed.
**PASS only if:** Permissions explained; no private installed app list unintentionally exposed.
**Required evidence:** redacted screenshot + original secured file.

### SHOT-06 — Quick Settings Tile [P1; PHYSICAL; PENDING]
**Procedure:** Capture tile ON/OFF state on real device.
**PASS only if:** Visible tile corresponds to actual app state.
**Required evidence:** device screenshot + release SKU.

### SHOT-07 — Lightweight / no ads statement [P1; PLAY CONSOLE; PENDING]
**Procedure:** Inspect final merged manifest/dependency tree and AAB; take real UI screenshot.
**PASS only if:** No AdMob/analytics/ad UI, package size accurately stated.
**Required evidence:** SBOM/dependencies + screenshot.

### SHOT-08 — Localized screenshot captions [P1; PLAY CONSOLE; PENDING]
**Procedure:** Localize approved captions and regenerate sets without changing underlying app appearance.
**PASS only if:** No text overflow, native language matches locale, no false claims.
**Required evidence:** six reviewed screenshot sets.


## 14 additional launch-risk cases

### EXTRA-01 — Update in place [P0; PHYSICAL; PENDING]
**Procedure:** Install signed v0.8.0 then signed v0.8.1 without uninstall; compare prefs, permissions, excluded apps.
**PASS only if:** Installer offers Update; app data preserved; no signature/version conflict.
**Required evidence:** video + adb package version before/after.

### EXTRA-02 — Play App Signing setup [P0; PLAY CONSOLE; PENDING]
**Procedure:** Compare internal APK signing SHA256 to chosen Play upload key and Google's distributed app cert.
**PASS only if:** Correct certificate registration; record irreversible package/key decision.
**Required evidence:** Play certificates screen + versionCode upload receipt.

### EXTRA-03 — Correct 90°/270° misleading text [P0; STATIC; FAIL (source audit)]
**Procedure:** Search all six locales and store copy for 90°,270° and contradictory behavior.
**PASS only if:** No user-facing description promises landscape rotation unless feature truly does so.
**Required evidence:** diff + six locale grep + rebuilt release receipt.

### EXTRA-04 — Local data backup behavior [P0; PHYSICAL; PENDING]
**Procedure:** Review allowBackup=true and app data extraction; restore to second test device; inspect sensitive settings.
**PASS only if:** Document whether exclusion inventory and preferences can be restored; disclosures accurate.
**Required evidence:** tested backup/restore + final policy text.

### EXTRA-05 — Network/data flows and SDKs [P0; STATIC; PENDING]
**Procedure:** Inspect final merged Manifest incl. libraries, INTERNET, Gradle deps and traffic during 15-min use.
**PASS only if:** Data safety declarations match observed runtime; no unreviewed SDK/network collection.
**Required evidence:** manifest export + dependency list + traffic log.

### EXTRA-06 — App creation, account eligibility and payments [P0; PLAY CONSOLE; PENDING]
**Procedure:** Check if 180FlipCharge exists, personal account date, payment profile, legal identity.
**PASS only if:** App configured paid, country availability, commercial account eligible.
**Required evidence:** Console screenshots.

### EXTRA-07 — Closed testing eligibility [P0; PLAY CONSOLE; PENDING]
**Procedure:** Check whether 12 opted-in testers for 14 continuous days rule applies to developer account.
**PASS only if:** If applicable test has 12 testers continuously 14 days, production access granted.
**Required evidence:** Console test dashboard + approval.

### EXTRA-08 — App content declarations [P0; PLAY CONSOLE; PENDING]
**Procedure:** Complete target audience, ads, app access, content rating and device eligibility.
**PASS only if:** All Console content panels green and consistent with APK.
**Required evidence:** screenshots of each submission.

### EXTRA-09 — Listing artwork spec [P1; PLAY CONSOLE; PENDING]
**Procedure:** Create/review 512x512 icon and 1024x500 feature graphic; export appropriate screenshots.
**PASS only if:** All Play upload assets pass size/color/format checks.
**Required evidence:** export files + Console validation.

### EXTRA-10 — README version and feature correctness [P1; STATIC; FAIL (source audit)]
**Procedure:** Update stale README from 0.7.1/9 to 0.8.1/11 and correct any auto orientation claims.
**PASS only if:** Docs match checked app, no false 90/270.
**Required evidence:** reviewed docs diff.

### EXTRA-11 — Native UI parity with approved V2 web [P1; PHYSICAL; PENDING]
**Procedure:** Compare physical Android screenshots side-by-side with web V2 reference in 0/180/auto/wait/error states.
**PASS only if:** Meaningful visual parity + legibility, no clipped switch or CSS-only claim.
**Required evidence:** 5 state screenshot pairs + device metadata.

### EXTRA-12 — Permissions/notification with notifications denied [P0; PHYSICAL; PENDING]
**Procedure:** Deny POST_NOTIFICATIONS on API33+, activate FGS, inspect Android task manager/notification UI.
**PASS only if:** User still understands active service and can stop; no falsely promised always-visible notification.
**Required evidence:** video + dumpsys notification + app UI.

### EXTRA-13 — Public privacy URL and in-app link [P0; PLAY CONSOLE; PENDING]
**Procedure:** Open policy link without auth across IT and EN device; confirm in-app Settings link.
**PASS only if:** Content accessible and accurate; no PDF/login/geoblock.
**Required evidence:** URL proof + device video.

### EXTRA-14 — No exact-artifact visual certification [P0; PHYSICAL; PENDING]
**Procedure:** Install exact signed artifact from run (not debug or web preview); verify real release UI and rotation.
**PASS only if:** Pass based on identical signed binary that Play would upload or documented Play delivery differences.
**Required evidence:** artifact SHA + device recording.


## Fast ADB capture guide (reference only; ADB does not replace real device observations)

Install update, preserving user data (requires same signer as currently installed):
```powershell
adb devices -l
adb install -r .\Rotate-180-v0.8.1-Neo-Tactile-Dark-signed.apk
adb shell dumpsys package com.flipcharge.app | Select-String "versionCode|versionName"
adb shell appops get com.flipcharge.app GET_USAGE_STATS
adb shell dumpsys activity services com.flipcharge.app
adb logcat -c
# Perform a test by physically manipulating the device.
adb logcat -d -v time > .\rotate180-device-test.log
```
Never run `adb uninstall` for the in-place update test. Record package signature from installed APK if update is rejected.

Do **real** reboot, real charge/discharge, real battery-saver checks; synthetic broadcasts and mocked charging cannot certify OEM behavior.

## AAB / signer evidence (from signed CI run)

Run: https://github.com/riccardopinato/180FlipCharge/actions/runs/43255813788
- Artifact `Rotate-180-signed-apk` — present.
- Artifact `Rotate-180-signed-aab` — present.
- Artifact `Rotate-180-release-evidence` — present.
- Certificate SHA-256 expected/verified (internal): `1D:4B:E8:F0:86:62:B1:51:77:9C:54:95:28:8D:8D:8D:16:82:31:FC:FD:27:04:4E:33:B3:74:6E:C1:C1:79:48`.

Play may distribute APKs signed with a different **app signing key**; the CI signer may serve as **upload key** after Play App Signing enrollment. Do not assume equality of installed sideloaded APK signer and Play-distributed signer.

## Code risks discovered during source review

- **Misleading claims:** `values*/strings.xml`: `permanent_180_description` and `permanent_180_short` still promise 0/90/180/270 while `RotationOrientationEngine.kt` chooses only portrait/reverse portrait. Static FAIL.
- **Foreground service policy:** `ReversePortraitService.kt` starts `specialUse` FGS; persistent notification with STOP is implemented. Approval and non-deferrable justification remain unproven.
- **Background start:** `BootReceiver.kt` calls `restoreAfterBoot()` → `ContextCompat.startForegroundService`; Android 15/16 foreground-background restrictions can block starts depending on OS state/exemptions. Physical tests essential; do not promise uninterrupted auto-restoration.
- **Usage Access:** `ForegroundAppTracker.kt` queries usage events and app foreground identity every 2 seconds when exceptions selected; `InstalledAppsRepository.kt` queries launchable apps, a privacy-sensitive app inventory. No network transmission detected by static code search is *not* a certified data-safety conclusion.
- **Backup:** `AndroidManifest.xml` sets `allowBackup=true` without explicit `dataExtractionRules`; test and disclose behavior of backed-up preferences/exclusions.
- **Runtime content:** `MainScreen.kt` shows user-facing local strings; actual UI may differ from web CSS due to Compose and OEM font scaling. Screenshot parity untested.
- **Tests:** `src/test` contains five JVM suites for state/classification/tile. No `src/androidTest` instrumentation suite in audited Git tree.
- **Design deviations outside main:** `SettingsScreen.kt` and `AppExceptionsScreen.kt` still use older gradient cards; not a release blocker unless visual QA or misleading state fails.
- **Stale documentation:** `README.md` still describes version 0.7.1, versionCode 9. Documentation FAIL.
- **Store readiness:** no privacy policy, listing screenshots, graphics, or release notes found in repo; absence of private Console state cannot be inferred.
- **Package metadata:** package `com.flipcharge.app`, minSdk 26, targetSdk 36, versionCode 11, versionName 0.8.1, no AdMob or billing dependencies detected in Gradle build.

## Rollout and no-go authority

1. Audit/fix only **P0 correctness + policy**: honest 0/180 text, privacy disclosure/contact, FGS justification, boot background limits, permission failure.
2. Re-sign v0.8.2+ if source changed; verify all exact-artifact checks.
3. Run physical QA on two manufacturers.
4. Upload to Internal Testing, collect crash/ANR reports, refine copy/assets.
5. Complete Play Console declarations, closed test where applicable.
6. Only when P0/P1 evidence is attached and accepted: request production.

The audit **does not assert Google approval**, device quality or finished commercial setup.
