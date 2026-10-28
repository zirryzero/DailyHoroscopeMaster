package com.powerdevelop.dailyhoroscope

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

object HoroscopeTranslator {

    private const val TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=es&dt=t&q="
    private const val USER_AGENT = "AndroidTranslate/5.3.0.RC02.130475354-53000263 5.1 phone TRANSLATE_OPM5_TEST_1"

    fun shouldTranslateToSpanish(context: Context): Boolean {
        return LocaleHelper.isSpanish(context)
    }

    fun translateEnToEs(client: OkHttpClient, text: String): String {
        return try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val request = Request.Builder()
                .url(TRANSLATE_URL + encoded)
                .header("User-Agent", USER_AGENT)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return text
                val body = response.body?.string() ?: return text
                val jsonArray = JSONArray(body)
                val sentences = jsonArray.getJSONArray(0)
                val sb = StringBuilder()
                for (i in 0 until sentences.length()) {
                    val sentence = sentences.getJSONArray(i)
                    val transPart = sentence.optString(0, "")
                    sb.append(transPart)
                }
                val result = sb.toString().trim()
                if (result.isNotEmpty()) result else text
            }
        } catch (e: Exception) {
            text
        }
    }
}
