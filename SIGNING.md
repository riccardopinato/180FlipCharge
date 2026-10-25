# Android signing identity

Application ID: `com.flipcharge.app`

Signing lane: `INTERNAL`

Expected certificate SHA-256:

`1D:4B:E8:F0:86:62:B1:51:77:9C:54:95:28:8D:8D:8D:16:82:31:FC:FD:27:04:4E:33:B3:74:6E:C1:C1:79:48`

Required GitHub Actions secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

The private `.jks`, its base64 representation and passwords must never be committed.

A signed release is accepted only when CI verifies that the APK signer fingerprint matches the expected fingerprint above.
