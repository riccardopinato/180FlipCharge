# Rotate 180° — Monetization Strategy

## Decision

**Paid upfront. No ads. No in-app paywall. No subscription.**

Base Google Play price:

**€4.99**

Why:
- the product delivers value outside the app after setup;
- expected session duration is short;
- banners/interstitials have weak revenue potential here and damage a clean utility UI;
- paid-upfront monetizes at acquisition rather than waiting for engagement;
- removing AdMob avoids SDK weight, consent UI and advertising-related visual clutter.

## Promotions

Use Google Play paid-app Sales after publication.

Recommended first test:
- base price: €4.99
- promotional price: €2.99
- duration: 7–14 days
- never permanently switch the app to free

Google Play currently permits paid-app sales from 1 to 14 days and requires at
least a 10% discount. There must be 30 days between sales.

Do not hardcode promotional claims inside the APK. Store pricing is managed in
Play Console.

## Measurement

Primary commercial metrics:
- Store listing visitor -> purchase conversion
- refund rate
- acquisition channel
- country/price conversion
- rating/review velocity
- crash/ANR-free users

No ad metrics are relevant because the product intentionally ships without ads.

## Future boundary

If Rotate 180° later adds an in-app product, subscription or cross-app
entitlement, adopt:
- App Factory Entitlement Contract
- Play Billing Golden
- Monetization Backend Golden

Paid-upfront alone does not justify a purchase backend.
