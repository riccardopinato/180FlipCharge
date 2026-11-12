# Play Console handoff — Rotate 180° (NOT SUBMITTED)

**Candidate:** v0.8.2 / versionCode 12 · package `com.flipcharge.app` · paid app proposal €4.99.
**Current state:** no authenticated evidence that the app exists in the developer's private Play Console.
**No screenshot, setting, review or price is claimed completed.**

## Suggested Console submission sequence
1. Verify developer account identity, payments eligibility, Play Console access, and whether 12 testers / 14 days apply.
2. Create app as **paid** if this is the intended model; verify pricing, territories and payment profile. Changing a published free app to paid is not available in place.
3. Select Play App Signing, confirm *upload certificate* vs *Google app signing certificate*. Source signing is internal; do not assume it matches the distributed certificate.
4. Upload exact signed AAB produced from v0.8.2 once exact-artifact CI has passed (not the older v0.8.1).
5. App content: foreground service `specialUse`, user impact if delayed/interrupted, real demonstration video, usage access explanation, app access, ads declaration, content rating, target audience, Data Safety, Privacy Policy URL.
6. Privacy URL: https://rotate180-privacy.vercel.app/ — currently published and accessible but developer's definitive legal identity/contact must be reviewed before public distribution. The same link appears inside Settings in candidate v0.8.2.
7. Main store listing: `store/listings/{en-US,it-IT,es-ES,fr-FR,pt-PT,de-DE}.md` — text prepared; each still requires human language and claim review.
8. Upload authentic store icon, feature graphic and *real-device* screenshots per `store/ASSETS_SPEC.md`.
9. Internal Testing: upload, install from Play and test cert/updates, device compatibility and FGS permissions.
10. Closed Testing and production access if required for the developer account; do not invent Play acceptance before Console receipt.
11. Only after all P0/P1 tests in release checklist pass, request production.

## Details requiring human/Console evidence
- Exact developer/publisher public name and privacy support contact.
- Definitive payment profile, country availability, pricing and refund information.
- Whether package name has been reserved in Play Console.
- Legal/privacy review and Data Safety declaration based on actual release artifact.
- FGS specialUse approval, submitted video and outcome.
- Any required tester opt-ins/14-day continuous period.
- Signing certificate fingerprints and Play-distributed install test.
- Signed AAB digest, Google Play build receipt and tracks/releases.
