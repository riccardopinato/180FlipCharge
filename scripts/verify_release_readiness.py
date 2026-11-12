#!/usr/bin/env python3
"""Static release claim guard. No external packages, credentials or web calls."""
import re
import sys
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
failures = []
def require(check, message):
    if not check:
        failures.append(message)

gradle = (ROOT / "app/build.gradle.kts").read_text()
readme = (ROOT / "README.md").read_text()
settings = (ROOT / "app/src/main/java/com/flipcharge/app/SettingsActivity.kt").read_text()
settings_ui = (ROOT / "app/src/main/java/com/flipcharge/app/SettingsScreen.kt").read_text()
privacy_activity = (ROOT / "app/src/main/java/com/flipcharge/app/PrivacyPolicyActivity.kt").read_text()
manifest = (ROOT / "app/src/main/AndroidManifest.xml").read_text()
signed = (ROOT / ".github/workflows/build-signed-release.yml").read_text()
engine = (ROOT / "app/src/main/java/com/flipcharge/app/RotationOrientationEngine.kt").read_text()
policy = (ROOT / "docs/privacy/index.html").read_text()

require('applicationId = "com.flipcharge.app"' in gradle, "Package changed")
require("versionCode = 13" in gradle and 'versionName = "0.8.3"' in gradle, "Version mismatch")
require("0.8.3" in readme and "versionCode 13" in readme, "README stale version")
require("Rotate-180-v0.8.3-internal.apk" in signed and "Rotate-180-v0.8.3-internal.aab" in signed, "Release artifact labels stale")
require("SCREEN_ORIENTATION_REVERSE_PORTRAIT" in engine and "SCREEN_ORIENTATION_PORTRAIT" in engine, "Orientation engine changed")
require("SCREEN_ORIENTATION_LANDSCAPE" not in engine, "Engine unexpectedly requests landscape")
require("android.permission.INTERNET" not in manifest, "Unexpected Internet permission (check privacy inventory)")
require("foregroundServiceType=\"specialUse\"" in manifest, "Unexpected FGS type")
require("startActivity(Intent(this, PrivacyPolicyActivity::class.java))" in settings, "Settings does not open internal privacy Activity")
require('android:name=".PrivacyPolicyActivity"' in manifest, "Privacy Activity missing in manifest")
require("https://rotate180-privacy.vercel.app/" in privacy_activity, "Online policy link missing")
require("rememberSaveable" in privacy_activity and "LazyColumn" in privacy_activity, "Offline policy screen missing")
require("items(copy.sections.size)" in privacy_activity and 'PolicySection("7. Updates"' in privacy_activity, "Offline policy sections incomplete")
require("Intent.ACTION_VIEW" in privacy_activity, "Online policy action missing")
require("privacy_policy_title" in settings_ui and "onOpenPrivacyPolicy" in settings_ui, "Privacy link UI missing")
require("Informativa sulla privacy" in policy and "Privacy Policy" in policy, "Privacy HTML missing")
require("riccardopinato" in policy and "GitHub" in policy, "Privacy identity/contact missing")
require((ROOT / "docs/fonts/manrope-OFL.txt").exists(), "Manrope OFL missing")
require((ROOT / "docs/fonts/space-grotesk-OFL.txt").exists(), "Space Grotesk OFL missing")

required_keys = ["permanent_180_description", "permanent_180_short", "privacy_policy_title", "privacy_policy_description", "usage_access_description"]
for locale in ("values", "values-it", "values-es", "values-fr", "values-pt", "values-de"):
    path = ROOT / "app/src/main/res" / locale / "strings.xml"
    entries = {s.get("name"): ("".join(s.itertext())) for s in ET.parse(path).getroot().findall("string")}
    for k in required_keys:
        require(bool(entries.get(k, "").strip()), f"{locale}: missing {k}")
    for k in ("permanent_180_description", "permanent_180_short"):
        require("90°" not in entries.get(k, "") and "270°" not in entries.get(k, ""), f"{locale}: false landscape claim in {k}")
        require("180°" in entries.get(k, "") and "0°" in entries.get(k, ""), f"{locale}: portrait angles not documented")

for locale in ("en-US", "it-IT", "es-ES", "fr-FR", "pt-PT", "de-DE"):
    path = ROOT / "store/listings" / (locale + ".md")
    require(path.exists(), f"Missing Play listing {locale}")
    if not path.exists():
        continue
    content = path.read_text(encoding="utf-8")
    short = re.search(r"^Short \(\d+/80\): (.+)$", content, re.M)
    title = re.search(r"^Title \(\d+/30\): (.+)$", content, re.M)
    require(short is not None and len(short.group(1)) <= 80, f"{locale}: short desc invalid")
    require(title is not None and len(title.group(1)) <= 30, f"{locale}: title invalid")

if failures:
    for item in failures:
        print("::error::" + item)
    sys.exit(1)
print("Release-readiness static checks PASS: 6 locale XML, 6 store listing drafts, 0/180-only, privacy link and signed artifact identity.")
