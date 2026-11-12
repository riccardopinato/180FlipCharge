# Play Console — FGS specialUse declaration prep (DRAFT, not submitted)

**App:** Rotate 180° — `com.flipcharge.app`
**Version audited:** 0.8.1, `6836c615...`
**Manifest type:** `specialUse` / `FOREGROUND_SERVICE_SPECIAL_USE`
**Manifest subtype property:** "Provides user-enabled continuous screen orientation control including 180-degree reverse portrait, charging automation and app exceptions."
**Current approval state:** UNKNOWN / NOT SUBMITTED (no authenticated Play Console evidence).
**Mandatory human decision:** verify if the use case meets Play FGS criteria, including no reasonable deferrable/background alternative. Review can reject `specialUse` even with a syntactically correct Manifest.

## Candidate description to revise and submit (in English)

"Rotate 180° lets the user explicitly enable continuous 0°/180° portrait orientation control, either manually or conditionally while a charger is connected. When enabled, the app maintains a foreground service to keep the user-selected orientation behavior while the UI is closed. An ongoing service notification identifies the feature and exposes a STOP action that ends all enabled modes. The app can also temporarily exclude user-selected foreground apps."

This is **a description of implemented behavior, not a claim of Play approval or proven interoperability**.

## Impact when started late / interrupted (candidate)

- If startup is deferred: manual 180°, automatic flip, or charging-triggered orientation might not take effect until the user reopens the app or Android permits the service to run.
- If interrupted: selected orientation behavior can be suspended; the user may need to reopen the app or revisit OEM battery/overlay permissions.
- Avoid promising start-on-boot always works. Validate OEM and Android 15/16 restrictions first.

## REQUIRED REAL-DEMO VIDEO storyboard

1. Show device model, version/build info and the real APK.
2. Open the app; enable overlay permission from native Android settings with a visible interaction.
3. Tap manual 180° ON; show physical reversal and user-visible notification.
4. Pull down notifications, show STOP action; stop and verify the reverse portrait is released.
5. Enable auto 180°; physically rotate 0°→180°→0°; show **no forced landscape at 90°/270°**.
6. Enable charging automation; plug a real charger; show state and rotation; unplug to show return.
7. Demonstrate background operation for 2–5 minutes and persistent user-visible indication.
8. If app exceptions are used, show Usage Access explanation, grant in Settings and explain monitoring; avoid exposing private app inventory in published video.
9. Close with user control and stop path.
10. Export a URL viewable by reviewers without login/geoblocking. Paste the URL into Play Console.

### Pre-submission verification

- [ ] Real video recorded, not web-preview mock.
- [ ] Type selected `specialUse` for the declared service.
- [ ] Review `android.permission.SYSTEM_ALERT_WINDOW`, `PACKAGE_USAGE_STATS`, `POST_NOTIFICATIONS` explanation and actual OS flows.
- [ ] FGS continues only when necessary and exposes user-perceptible indication and effective STOP.
- [ ] Justification for no suitable deferrable/system alternative written and backed by technical rationale.
- [ ] Play Console declaration content matches Manifest and runtime test results.
- [ ] Screenshot of submitted declaration and review outcome stored as audit evidence.

Official:
- https://support.google.com/googleplay/android-developer/answer/13392821
- https://support.google.com/googleplay/android-developer/answer/18265346
- https://developer.android.com/develop/background-work/services/fgs/service-types
