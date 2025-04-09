package com.powerdevelop.dailyhoroscope

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HoroscopeActivity : AppCompatActivity() {

    private lateinit var horoscopeText: TextView
    private val client = OkHttpClient()
    private lateinit var shareButton: Button
    private var currentHoroscope: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horoscope)
        shareButton = findViewById(R.id.shareButton)

        horoscopeText = findViewById(R.id.horoscopeText)
        val sign = intent.getStringExtra("sign") ?: return

        val url = "https://horoscope-app-api.vercel.app/api/v1/get-horoscope/daily?sign=$sign"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    horoscopeText.text = getString(R.string.error_message)
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "")
                val data = json.getJSONObject("data")
                val prediction = data.getString("horoscope_data")
                val date = data.getString("date")

                runOnUiThread {
                    horoscopeText.text = "Horoscope for $sign ($date):\n\n$prediction"
                }
                currentHoroscope = """
                    🔮 Here's my daily horoscope from the Daily Horoscope App:

                    ♈ Sign: ${sign.replaceFirstChar { it.uppercase() }}
                    📅 Date: $date
                    ✨ $prediction

                    Download the app and check yours too! 🚀
                    
                    📲 Get it here: https://play.google.com/store/apps/details?id=com.powerdevelop.dailyhoroscope
                    """.trimIndent()

            }
        })
        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, currentHoroscope)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }
}
