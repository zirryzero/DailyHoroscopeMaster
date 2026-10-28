package com.powerdevelop.dailyhoroscope

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HoroscopeAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val favSignId = FavoriteManager.getFavoriteSign(context) ?: "aries"
            val isSpanish = LocaleHelper.isSpanish(context)

            val displayName = favSignId.replaceFirstChar { it.uppercase() }
            val dateFormat = SimpleDateFormat("EEEE, d MMMM", if (isSpanish) Locale("es") else Locale.ENGLISH)
            val currentDate = dateFormat.format(Date()).replaceFirstChar { it.uppercase() }

            val extras = HoroscopeExtrasGenerator.generate(favSignId, "widget")

            // Read cached horoscope if available
            val prefs = context.getSharedPreferences("horoscope_cache", Context.MODE_PRIVATE)
            val cacheKey = favSignId + (if (isSpanish) "_es" else "_en")
            val cachedText = prefs.getString("cached_text_" + cacheKey, null)
                ?: if (isSpanish) "Toca para leer tu predicción completa de hoy." else "Tap to read your full horoscope."

            val views = RemoteViews(context.packageName, R.layout.widget_horoscope)
            views.setTextViewText(R.id.widgetTitle, "$displayName ✨")
            views.setTextViewText(R.id.widgetDate, currentDate)
            views.setTextViewText(R.id.widgetText, cachedText)
            views.setTextViewText(R.id.widgetLucky, "🍀 ${extras.luckyNumbers.joinToString(", ")}")

            // Click opens MainActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

