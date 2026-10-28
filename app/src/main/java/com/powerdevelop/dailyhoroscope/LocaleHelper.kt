package com.powerdevelop.dailyhoroscope

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "app_settings"
    private const val KEY_LANG = "selected_language"

    const val LANG_DEFAULT = "default"
    const val LANG_ES = "es"
    const val LANG_EN = "en"

    fun getSelectedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG, LANG_DEFAULT) ?: LANG_DEFAULT
    }

    fun setLanguage(context: Context, langCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, langCode).apply()

        applyLanguage(langCode)
    }

    fun applySavedLanguage(context: Context) {
        val lang = getSelectedLanguage(context)
        applyLanguage(lang)
    }

    private fun applyLanguage(langCode: String) {
        val appLocale = if (langCode == LANG_DEFAULT) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(langCode)
        }
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun isSpanish(context: Context): Boolean {
        val selected = getSelectedLanguage(context)
        return when (selected) {
            LANG_ES -> true
            LANG_EN -> false
            else -> {
                val current = Locale.getDefault().language
                current.equals("es", ignoreCase = true)
            }
        }
    }
}

