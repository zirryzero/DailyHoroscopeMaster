package com.powerdevelop.dailyhoroscope

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val signs = listOf(
        "aries", "taurus", "gemini", "cancer",
        "leo", "virgo", "libra", "scorpio",
        "sagittarius", "capricorn", "aquarius", "pisces"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<LinearLayout>(R.id.signsLayout)

        signs.forEach { sign ->
            val button = Button(this)
            button.text = sign.replaceFirstChar { it.uppercase() }
            button.setOnClickListener {
                val intent = Intent(this, HoroscopeActivity::class.java)
                intent.putExtra("sign", sign)
                startActivity(intent)
            }
            layout.addView(button)
        }
    }
}
