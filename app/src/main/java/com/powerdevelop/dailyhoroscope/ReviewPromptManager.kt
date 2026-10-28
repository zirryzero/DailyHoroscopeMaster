package com.powerdevelop.dailyhoroscope

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog

object ReviewPromptManager {

    private const val PREFS_NAME = "review_prefs"
    private const val KEY_VIEW_COUNT = "horoscope_view_count"
    private const val KEY_ALREADY_RATED = "already_rated"

    fun recordHoroscopeView(activity: Activity) {
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ALREADY_RATED, false)) return

        val count = prefs.getInt(KEY_VIEW_COUNT, 0) + 1
        prefs.edit().putInt(KEY_VIEW_COUNT, count).apply()

        // Show prompt on 3rd and 8th view
        if (count == 3 || count == 8) {
            showReviewDialog(activity)
        }
    }

    private fun showReviewDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.rate_app_title)
            .setMessage(R.string.rate_app_desc)
            .setPositiveButton(R.string.rate_now) { dialog, _ ->
                val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(KEY_ALREADY_RATED, true).apply()
                dialog.dismiss()

                try {
                    val uri = Uri.parse("market://details?id=${activity.packageName}")
                    activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } catch (e: Exception) {
                    val webUri = Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}")
                    activity.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                }
            }
            .setNegativeButton(R.string.later, null)
            .show()
    }
}

