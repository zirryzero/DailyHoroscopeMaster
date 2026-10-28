package com.powerdevelop.dailyhoroscope

import android.content.Context

object FavoriteManager {

    private const val PREFS_NAME = "favorite_prefs"
    private const val KEY_FAVORITE_SIGN = "favorite_sign"

    fun getFavoriteSign(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FAVORITE_SIGN, null)
    }

    fun isFavorite(context: Context, signId: String): Boolean {
        return getFavoriteSign(context)?.equals(signId, ignoreCase = true) == true
    }

    fun toggleFavorite(context: Context, signId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getFavoriteSign(context)
        return if (current?.equals(signId, ignoreCase = true) == true) {
            prefs.edit().remove(KEY_FAVORITE_SIGN).apply()
            false
        } else {
            prefs.edit().putString(KEY_FAVORITE_SIGN, signId).apply()
            true
        }
    }
}

