package com.flipcharge.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.flipcharge.app.ui.theme.FlipChargeTheme

class SettingsActivity : LocalizedActivity() {

    private val selectedLanguage = mutableStateOf(AppLocaleManager.SYSTEM_LANGUAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        selectedLanguage.value = AppLocaleManager.currentLanguage(this)

        setContent {
            FlipChargeTheme {
                SettingsScreen(
                    selectedLanguage = selectedLanguage.value,
                    onBack = { finish() },
                    onOpenPrivacyPolicy = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                        runCatching { startActivity(intent) }
                    },
                    onLanguageSelected = { languageCode ->
                        if (languageCode == selectedLanguage.value) return@SettingsScreen

                        AppLocaleManager.setLanguage(this, languageCode)
                        selectedLanguage.value = languageCode
                        RotationController.reconcileService(this)

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            recreate()
                        }
                    },
                )
            }
        }
    }
    companion object {
        private const val PRIVACY_POLICY_URL = "https://rotate180-privacy.vercel.app/"
    }
}
