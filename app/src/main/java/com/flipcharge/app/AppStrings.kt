package com.flipcharge.app

import android.content.Context
import androidx.annotation.StringRes

object AppStrings {
    /**
     * Retrieves a string resource from the app's locale manager.
     * Uses the language selected in the app.
     */
    fun get(context: Context, @StringRes id: Int): String =
        AppLocaleManager.wrap(context).getString(id)
}
