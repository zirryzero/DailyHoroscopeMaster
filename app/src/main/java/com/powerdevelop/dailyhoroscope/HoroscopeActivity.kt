package com.powerdevelop.dailyhoroscope

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        private const val KEY_FONT_SIZE = "key_font_size"
    }

    private lateinit var binding: ActivityHoroscopeBinding
    private val client = OkHttpClient()

    private var signId: String = ""
    private var signDisplayName: String = ""
    private var currentPrediction: String = ""
    private var currentDate: String = ""
    private var currentShareText: String = ""
    private var currentCall: Call? = null

    // Font size options: Normal (16sp), Large (19sp), Extra Large (22sp)
    private val fontSizes = arrayOf(16f, 19f, 22f)
    private var currentFontSizeIndex = 0

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

        // Favorite State
        updateFavoriteIcon()
        binding.btnFavorite.setOnClickListener {
            FavoriteManager.toggleFavorite(this, signId)
            updateFavoriteIcon()
        }

        // Font Size Setup
        loadSavedFontSize()
        binding.btnFontSize.setOnClickListener {
            showFontSizeDialog()
        }

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

        // Copy button
        binding.copyButton.setOnClickListener {
            if (currentPrediction.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Horoscope", currentShareText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        }

        // Show cached version first if available, then refresh
        loadCachedHoroscope()
        fetchHoroscope()
    }

    private fun updateFavoriteIcon() {
        val isFav = FavoriteManager.isFavorite(this, signId)
        binding.btnFavorite.setImageResource(
            if (isFav) R.drawable.ic_star_filled else R.drawable.ic_star_border
        )
    }

    private fun loadSavedFontSize() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentFontSizeIndex = prefs.getInt(KEY_FONT_SIZE, 0).coerceIn(0, fontSizes.size - 1)
        binding.horoscopeText.textSize = fontSizes[currentFontSizeIndex]
    }

    private fun showFontSizeDialog() {
        val options = arrayOf(
            "Normal (16sp)",
            "Grande / Large (19sp)",
            "Muy Grande / Extra Large (22sp)"
        )
        AlertDialog.Builder(this)
            .setTitle(R.string.text_size)
            .setSingleChoiceItems(options, currentFontSizeIndex) { dialog, which ->
                currentFontSizeIndex = which
                binding.horoscopeText.textSize = fontSizes[which]
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(KEY_FONT_SIZE, which)
                    .apply()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun getCacheKey(): String {
        val langSuffix = if (HoroscopeTranslator.shouldTranslateToSpanish(this)) "_es" else "_en"
        return signId + langSuffix
    }

    private fun loadCachedHoroscope() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cacheKey = getCacheKey()
        val cachedText = prefs.getString(KEY_PREF_TEXT + cacheKey, null)
        val cachedDate = prefs.getString(KEY_PREF_DATE + cacheKey, null)

        if (!cachedText.isNullOrEmpty()) {
            displayHoroscope(cachedText, cachedDate ?: "")
        }
    }

    private fun saveCachedHoroscope(text: String, date: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cacheKey = getCacheKey()
        prefs.edit()
            .putString(KEY_PREF_TEXT + cacheKey, text)
            .putString(KEY_PREF_DATE + cacheKey, date)
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

                    val rawPrediction = when {
                        data.has("horoscope") -> data.getString("horoscope")
                        data.has("horoscope_data") -> data.getString("horoscope_data")
                        else -> throw IllegalArgumentException("Missing horoscope field in response")
                    }
                    val date = data.optString("date", "")

                    // Translate to Spanish if configured/system language is Spanish
                    val finalPrediction = if (HoroscopeTranslator.shouldTranslateToSpanish(this@HoroscopeActivity)) {
                        HoroscopeTranslator.translateEnToEs(client, rawPrediction)
                    } else {
                        rawPrediction
                    }

                    saveCachedHoroscope(finalPrediction, date)

                    runOnUiThread {
                        displayHoroscope(finalPrediction, date)
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
        currentPrediction = prediction
        currentDate = date

        binding.loadingLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        binding.predictionCard.visibility = View.VISIBLE
        binding.actionButtonsLayout.visibility = View.VISIBLE

        binding.horoscopeText.text = prediction
        if (date.isNotEmpty()) {
            binding.horoscopeDateText.visibility = View.VISIBLE
            binding.horoscopeDateText.text = date
        } else {
            binding.horoscopeDateText.visibility = View.GONE
        }

        // Setup Lucky Extras
        val extras = HoroscopeExtrasGenerator.generate(signId, if (date.isNotEmpty()) date else "daily")
        binding.luckyCard.visibility = View.VISIBLE
        binding.luckyNumbersText.text = extras.luckyNumbers.joinToString(" • ")
        binding.luckyColorText.setText(extras.colorNameRes)

        val circleDrawable = binding.luckyColorCircle.background as? GradientDrawable
            ?: GradientDrawable().apply { shape = GradientDrawable.OVAL }
        circleDrawable.setColor(extras.colorHex)
        binding.luckyColorCircle.background = circleDrawable

        currentShareText = """
            🔮 ${getString(R.string.app_name)}:
            
            ♈ $signDisplayName
            📅 $date
            🍀 ${getString(R.string.lucky_numbers)}: ${extras.luckyNumbers.joinToString(", ")}
            🎨 ${getString(R.string.lucky_color)}: ${getString(extras.colorNameRes)}
            
            ✨ $prediction
            
            📲 https://play.google.com/store/apps/details?id=com.powerdevelop.dailyhoroscope
        """.trimIndent()
    }

    private fun showLoading() {
        if (binding.horoscopeText.text.isNullOrEmpty()) {
            binding.predictionCard.visibility = View.GONE
            binding.actionButtonsLayout.visibility = View.GONE
            binding.luckyCard.visibility = View.GONE
        }
        binding.loadingLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.loadingLayout.visibility = View.GONE
        if (binding.horoscopeText.text.isNullOrEmpty()) {
            binding.predictionCard.visibility = View.GONE
            binding.actionButtonsLayout.visibility = View.GONE
            binding.luckyCard.visibility = View.GONE
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
