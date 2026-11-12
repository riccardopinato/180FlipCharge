# Neo Tactile Dark V2 — fidelity pass (0.8.1)

Canonical web design: Vercel `rotate180-retro-pixel-preview-ju9e3pyqj.vercel.app` (V2, approved in Chat).
Design-App references: `UIR-0025`, `UIR-0035`, `UIR-0030`, `UIR-0017`.

## Native UI parity improvements
- Headings use locally bundled **Space Grotesk**, body and control labels **Manrope**.
- Fonts are distributed unmodified under SIL OFL 1.1. See `docs/fonts/`.
- Home hierarchy replicated: DISPLAY / CONTROL, header, status card, rotation dial,
  control section, 118×65dp tactile manual switch, 2 full-width mode rows,
  compact utility rows, failure state cards.
- Native gradation: sweep-gradient metallic ring, radial center inset, dashed inner dial,
  progress arc, brushed thumb stripes, multi-stop inset tracks, colored shadows,
  thin keylines. Native Canvas/Compose only; NO WebView or downloaded images.
- State truth: runtime status engine provides IDLE / ACTIVE / WAITING / PERMISSION /
  ERROR / STARTING / SUSPENDED; no simulated states in Android production.
- Five languages + default English: newly added UI labels localized in each existing
  `values-*` locale, avoiding new English-only user-facing descriptions.

## In-place install contract
`applicationId=com.flipcharge.app` unchanged, `versionName=0.8.1`,
`versionCode=11`, same persistent internal signer as prior releases
(expected SHA-256 is tracked in `SIGNING.md`). No migration or data reset.
Package can update v0.8.0 only if installed signer matches.

## Required gates
1. `Development checks`: `gradle :app:test`, `:app:lintRelease`,
   `:app:checkReleaseSize`.
2. `Build signed release` on merged main; embedded certificate fingerprint
   and artifact SHA-256 must be checked before distribution.
3. Physical visual QA on 360dp/large phone; 100%, 130%, 200% font scale;
   TalkBack switch state; dark contrast; permission and runtime error banners.

Visual caveat: Android native shaders and font metrics will not be mathematically
identical to CSS; judge parity from real device screenshots before claiming pixel-perfect.
