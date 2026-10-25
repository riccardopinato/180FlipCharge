plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val signingKeystorePath = System.getenv("ANDROID_KEYSTORE_PATH")
val signingStorePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
val signingKeyAlias = System.getenv("ANDROID_KEY_ALIAS")
val signingKeyPassword = System.getenv("ANDROID_KEY_PASSWORD")
val hasReleaseSigning = listOf(
    signingKeystorePath,
    signingStorePassword,
    signingKeyAlias,
    signingKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.flipcharge.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.flipcharge.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "0.6.1"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(signingKeystorePath!!)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    androidResources {
        localeFilters += listOf("en", "it", "es", "fr", "pt", "de")
    }

    buildFeatures {
        compose = true
        buildConfig = false
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.11.0")

    implementation(platform("androidx.compose:compose-bom:2026.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

tasks.register("checkReleaseSize") {
    group = "verification"
    description = "Fails when 180FlipCharge release artifacts exceed the size budget."
    dependsOn("assembleRelease", "bundleRelease")

    doLast {
        val apkDir = layout.buildDirectory.dir("outputs/apk/release").get().asFile
        val apk = apkDir.listFiles()
            ?.filter { it.isFile && it.extension == "apk" }
            ?.maxByOrNull { it.length() }
            ?: error("Release APK not found in ${apkDir.absolutePath}")

        val apkLimit = 8L * 1024L * 1024L
        println("180FlipCharge release APK: %.2f MiB".format(apk.length().toDouble() / 1024.0 / 1024.0))
        check(apk.length() <= apkLimit) {
            "Release APK exceeds 8 MiB budget: ${apk.length()} bytes"
        }

        val aabDir = layout.buildDirectory.dir("outputs/bundle/release").get().asFile
        val aab = aabDir.listFiles()
            ?.filter { it.isFile && it.extension == "aab" }
            ?.maxByOrNull { it.length() }
            ?: error("Release AAB not found in ${aabDir.absolutePath}")

        val aabLimit = 10L * 1024L * 1024L
        println("180FlipCharge release AAB: %.2f MiB".format(aab.length().toDouble() / 1024.0 / 1024.0))
        check(aab.length() <= aabLimit) {
            "Release AAB exceeds 10 MiB budget: ${aab.length()} bytes"
        }
    }
}
