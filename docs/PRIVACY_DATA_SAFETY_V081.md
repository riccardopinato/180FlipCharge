# Privacy / Data Safety source inventory — 180FlipCharge v0.8.1

**Status:** investigation only; NOT a published Privacy Policy; no Console claims certified.
**Scope:** Git commit `6836c61571c90a0726a3b21ca00e31b492100088`.

| Data / capability | Source evidence | Expected local purpose | Status / verification |
|---|---|---|---|
| Foreground app identity / Usage Access | `ForegroundAppTracker.kt`, `UsageAccessHelper.kt` | Exclude selected apps from rotation | Static source identified; verify permission UX, data retention and no transmission |
| Launchable-app list (installed app inventory) | `InstalledAppsRepository.kt`; `<queries>` in Manifest | Display apps available for exception selection | Sensitive app inventory; disclose access appropriately |
| Selected excluded packages | `AppPreferences.kt` SharedPreferences | Persist exclusion choices | Check device backup transfer and deletion |
| Manual/Auto/Charging flags and suppression | `AppPreferences.kt` SharedPreferences | Persist user-selected mode | Check backup and restore |
| Preferred locale | `AppLocaleManager.kt` / Android LocaleManager | UI locale | Confirm backup behavior |
| Runtime health status | `RuntimeHealthStore.kt` SharedPreferences | Report service status | Check backup/retention behavior |
| Overlay access | `AndroidManifest.xml` SYSTEM_ALERT_WINDOW | 1x1 overlay used for rotation requests | Special Android permission, explicit user action |
| Foreground service / visible notification | `ReversePortraitService.kt` | Continuous rotation control | Verify task visible and stoppable |
| Network transport | Manifest lacks INTERNET; Gradle lists core/Compose/Material3 only | No obvious direct transmission | Inspect final merged Manifest and captured traffic; not a blanket guarantee |
| Cloud backup | `AndroidManifest.xml`: `allowBackup=true` | Android-managed backup may copy preferences | Verify real backup behavior and disclose accurately |
| Advertising / tracking | No ad dependency in `app/build.gradle.kts` | Paid upfront model with no ads | Check merged dependency tree and SDK inventory |

## To complete BEFORE any public Privacy Policy statement

1. Review all first- and third-party dependency/SDK data flows from the **release AAB**.
2. Inspect merged manifest for INTERNET and permissions added by dependencies.
3. Confirm whether Android backup extracts SharedPreferences and how a user deletes local records.
4. Determine whether local app inventory/Usage Access requires additional in-app prominent disclosure; verify the current usage-access screen meets clarity criteria.
5. Record retention and deletion: does uninstalling clear local data? What is restored by system backup?
6. Write a public HTML privacy policy naming app and developer; use an actual owned support contact. Include permission purposes and platform-managed backup where relevant.
7. Publish that policy and add **in-app access** (likely Settings, not currently present) and Play Console URL.
8. Complete the Data Safety form honestly; do not claim "no data collected/shared" until runtime SDK/data-flow review supports it.
9. Independently check whether installed app inventory is a special category in the Play Console questionnaire and disclose appropriately.

Official:
- https://support.google.com/googleplay/android-developer/answer/10144311
- https://support.google.com/googleplay/android-developer/answer/10787469
- https://support.google.com/googleplay/android-developer/answer/9859455

## Placeholder skeleton for final policy (not ready to publish)

- Identity: App name + legal developer identity + valid support/privacy contact.
- Permissions: Overlay, Usage Access, notifications, boot restore, foreground service.
- Local data: selected app exclusions, rotation settings, locale, runtime status.
- Collection, sharing and network behavior: must reflect the **verified** release.
- System backup and restoration; retention and user deletion.
- Effective date, update history, contact and changes.

Do not invent contact email, tax identifier, cloud storage, or audience.
