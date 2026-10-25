package com.flipcharge.app

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
}
