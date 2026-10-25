package com.flipcharge.app

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object AppLocaleManager {

    const val SYSTEM_LANGUAGE = ""
    private const val PREFS_NAME = "flipcharge_locale"
    private const val KEY_LANGUAGE = "language"

    val supportedLanguageCodes = setOf("en", "it", "es", "fr", "pt", "de")

    fun currentLanguage(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val locales = context.getSystemService(LocaleManager::class.java).applicationLocales
            locales.get(0)?.language?.takeIf { it in supportedLanguageCodes } ?: SYSTEM_LANGUAGE
        } else {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, SYSTEM_LANGUAGE)
                ?.takeIf { it in supportedLanguageCodes }
                ?: SYSTEM_LANGUAGE
        }
    }

    fun setLanguage(context: Context, languageCode: String) {
        val normalized = languageCode.takeIf { it in supportedLanguageCodes } ?: SYSTEM_LANGUAGE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                if (normalized.isBlank()) {
                    LocaleList.getEmptyLocaleList()
                } else {
                    LocaleList.forLanguageTags(normalized)
                }
        } else {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, normalized)
                .apply()
        }
    }

    fun wrap(base: Context): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return base

        val languageCode = currentLanguage(base)
        if (languageCode.isBlank()) return base

        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(base.resources.configuration)
        configuration.setLocales(LocaleList(locale))
        return base.createConfigurationContext(configuration)
    }
}
