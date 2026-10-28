package com.powerdevelop.dailyhoroscope

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.powerdevelop.dailyhoroscope.databinding.ActivityHoroscopeBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HoroscopeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SIGN_ID = "extra_sign_id"
        const val EXTRA_SIGN_NAME_RES = "extra_sign_name_res"
        const val EXTRA_BACKGROUND_RES = "extra_background_res"

        private const val BASE_URL = "https://freehoroscopeapi.com/api/v1/get-horoscope/daily"
        private const val PREFS_NAME = "horoscope_cache"
        private const val KEY_PREF_TEXT = "cached_text_"
        private const val KEY_PREF_DATE = "cached_date_"
    }

    private lateinit var binding: ActivityHoroscopeBinding
    private val client = OkHttpClient()

    private var signId: String = ""
    private var signDisplayName: String = ""
    private var currentShareText: String = ""
    private var currentCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHoroscopeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Ads
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adViewHoroscope.loadAd(adRequest)

        // Read Intent Extras (with backward compatibility)
        signId = intent.getStringExtra(EXTRA_SIGN_ID)
            ?: intent.getStringExtra("sign")
            ?: return

        val signNameRes = intent.getIntExtra(EXTRA_SIGN_NAME_RES, 0)
        signDisplayName = if (signNameRes != 0) {
            getString(signNameRes)
        } else {
            signId.replaceFirstChar { it.uppercase() }
        }

        val backgroundRes = intent.getIntExtra(EXTRA_BACKGROUND_RES, getFallbackBackground(signId))
        binding.horoscopeRoot.setBackgroundResource(backgroundRes)
        binding.toolbarTitle.text = signDisplayName
        binding.signHeaderIcon.setImageResource(getSignIcon(signId))

        // Navigation
        binding.btnBack.setOnClickListener { finish() }
        binding.btnRefresh.setOnClickListener { fetchHoroscope() }
        binding.btnRetry.setOnClickListener { fetchHoroscope() }

        // Share button
        binding.shareButton.setOnClickListener {
            if (currentShareText.isNotEmpty()) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, currentShareText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_horoscope)))
            }
        }

        // Show cached version first if available, then refresh
        loadCachedHoroscope()
        fetchHoroscope()
    }

    private fun loadCachedHoroscope() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedText = prefs.getString(KEY_PREF_TEXT + signId, null)
        val cachedDate = prefs.getString(KEY_PREF_DATE + signId, null)

        if (!cachedText.isNullOrEmpty()) {
            displayHoroscope(cachedText, cachedDate ?: "")
        }
    }

    private fun saveCachedHoroscope(text: String, date: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_PREF_TEXT + signId, text)
            .putString(KEY_PREF_DATE + signId, date)
            .apply()
    }

    private fun fetchHoroscope() {
        showLoading()
        currentCall?.cancel()

        val url = "$BASE_URL?sign=$signId"
        val request = Request.Builder().url(url).build()

        currentCall = client.newCall(request)
        currentCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (call.isCanceled()) return
                runOnUiThread {
                    showError(getString(R.string.error_message))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        showError(getString(R.string.error_message))
                    }
                    return
                }

                try {
                    val bodyString = response.body?.string() ?: ""
                    val json = JSONObject(bodyString)
                    val data = json.getJSONObject("data")

                    // Safely extract prediction: new API uses "horoscope", legacy used "horoscope_data"
                    val prediction = when {
                        data.has("horoscope") -> data.getString("horoscope")
                        data.has("horoscope_data") -> data.getString("horoscope_data")
                        else -> throw IllegalArgumentException("Missing horoscope field in response")
                    }
                    val date = data.optString("date", "")

                    saveCachedHoroscope(prediction, date)

                    runOnUiThread {
                        displayHoroscope(prediction, date)
                    }
                } catch (ex: Exception) {
                    runOnUiThread {
                        showError(getString(R.string.error_message))
                    }
                }
            }
        })
    }

    private fun displayHoroscope(prediction: String, date: String) {
        binding.loadingLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        binding.predictionCard.visibility = View.VISIBLE
        binding.shareButton.visibility = View.VISIBLE

        binding.horoscopeText.text = prediction
        if (date.isNotEmpty()) {
            binding.horoscopeDateText.visibility = View.VISIBLE
            binding.horoscopeDateText.text = date
        } else {
            binding.horoscopeDateText.visibility = View.GONE
        }

        currentShareText = """
            🔮 ${getString(R.string.app_name)}:
            
            ♈ $signDisplayName
            📅 $date
            
            ✨ $prediction
            
            📲 https://play.google.com/store/apps/details?id=com.powerdevelop.dailyhoroscope
        """.trimIndent()
    }

    private fun showLoading() {
        // If we already have content showing, keep card visible but show subtle loading or just show layout if empty
        if (binding.horoscopeText.text.isNullOrEmpty()) {
            binding.predictionCard.visibility = View.GONE
            binding.shareButton.visibility = View.GONE
        }
        binding.loadingLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.loadingLayout.visibility = View.GONE
        // If we have cached content already displayed, don't hide the card, just inform if needed
        if (binding.horoscopeText.text.isNullOrEmpty()) {
            binding.predictionCard.visibility = View.GONE
            binding.shareButton.visibility = View.GONE
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorText.text = message
        }
    }

    private fun getSignIcon(sign: String): Int {
        return when (sign.lowercase()) {
            "aries" -> R.drawable.aries
            "taurus" -> R.drawable.taurus
            "gemini" -> R.drawable.gemini
            "cancer" -> R.drawable.cancer
            "leo" -> R.drawable.leo
            "virgo" -> R.drawable.virgo
            "libra" -> R.drawable.libra
            "scorpio" -> R.drawable.scorpio
            "sagittarius" -> R.drawable.sagittarius
            "capricorn" -> R.drawable.capricorn
            "aquarius" -> R.drawable.aquarius
            "pisces" -> R.drawable.pisces
            else -> R.drawable.icono
        }
    }

    private fun getFallbackBackground(sign: String): Int {
        return when (sign.lowercase()) {
            "aries" -> R.drawable.background_aries
            "taurus" -> R.drawable.background_taurus
            "gemini" -> R.drawable.background_gemini
            "cancer" -> R.drawable.background_cancer
            "leo" -> R.drawable.background_leo
            "virgo" -> R.drawable.background_virgo
            "libra" -> R.drawable.background_libra
            "scorpio" -> R.drawable.background_scorpio
            "sagittarius" -> R.drawable.background_sagittarius
            "capricorn" -> R.drawable.background_capricorn
            "aquarius" -> R.drawable.background_aquarius
            "pisces" -> R.drawable.background_pisces
            else -> R.drawable.background_main
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentCall?.cancel()
    }
}
