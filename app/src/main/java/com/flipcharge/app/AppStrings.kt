package com.flipcharge.app

import android.content.Context
import androidx.annotation.StringRes

object AppStrings {
    fun get(context: Context, @StringRes id: Int): String =
        AppLocaleManager.wrap(context).getString(id)
}
