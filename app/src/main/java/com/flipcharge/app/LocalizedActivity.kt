package com.flipcharge.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity

abstract class LocalizedActivity : ComponentActivity() {

    private var localeAtCreate: String = AppLocaleManager.SYSTEM_LANGUAGE

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocaleManager.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeAtCreate = AppLocaleManager.currentLanguage(this)
    }

    override fun onResume() {
        super.onResume()
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
            localeAtCreate != AppLocaleManager.currentLanguage(this)
        ) {
            recreate()
        }
    }
}
