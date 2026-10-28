package com.powerdevelop.dailyhoroscope

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.powerdevelop.dailyhoroscope.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val signsByMonth = listOf(
        // 1. January: Capricorn (Dec 22 - Jan 19)
        Sign("capricorn", R.string.sign_capricorn, R.string.date_capricorn, R.drawable.capricorn, R.drawable.background_capricorn),
        // 2. February: Aquarius (Jan 20 - Feb 18)
        Sign("aquarius", R.string.sign_aquarius, R.string.date_aquarius, R.drawable.aquarius, R.drawable.background_aquarius),
        // 3. March: Pisces (Feb 19 - Mar 20)
        Sign("pisces", R.string.sign_pisces, R.string.date_pisces, R.drawable.pisces, R.drawable.background_pisces),
        // 4. April: Aries (Mar 21 - Apr 19)
        Sign("aries", R.string.sign_aries, R.string.date_aries, R.drawable.aries, R.drawable.background_aries),
        // 5. May: Taurus (Apr 20 - May 20)
        Sign("taurus", R.string.sign_taurus, R.string.date_taurus, R.drawable.taurus, R.drawable.background_taurus),
        // 6. June: Gemini (May 21 - Jun 20)
        Sign("gemini", R.string.sign_gemini, R.string.date_gemini, R.drawable.gemini, R.drawable.background_gemini),
        // 7. July: Cancer (Jun 21 - Jul 22)
        Sign("cancer", R.string.sign_cancer, R.string.date_cancer, R.drawable.cancer, R.drawable.background_cancer),
        // 8. August: Leo (Jul 23 - Aug 22)
        Sign("leo", R.string.sign_leo, R.string.date_leo, R.drawable.leo, R.drawable.background_leo),
        // 9. September: Virgo (Aug 23 - Sep 22)
        Sign("virgo", R.string.sign_virgo, R.string.date_virgo, R.drawable.virgo, R.drawable.background_virgo),
        // 10. October: Libra (Sep 23 - Oct 22)
        Sign("libra", R.string.sign_libra, R.string.date_libra, R.drawable.libra, R.drawable.background_libra),
        // 11. November: Scorpio (Oct 23 - Nov 21)
        Sign("scorpio", R.string.sign_scorpio, R.string.date_scorpio, R.drawable.scorpio, R.drawable.background_scorpio),
        // 12. December: Sagittarius (Nov 22 - Dec 21)
        Sign("sagittarius", R.string.sign_sagittarius, R.string.date_sagittarius, R.drawable.sagittarius, R.drawable.background_sagittarius)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved language preferences before inflating layout
        LocaleHelper.applySavedLanguage(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        // Settings button click listener
        binding.btnSettings.setOnClickListener {
            showLanguageDialog()
        }

        binding.recyclerViewSigns.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewSigns.adapter = SignAdapter(this, signsByMonth)
    }

    override fun onResume() {
        super.onResume()
        updateFavoriteBanner()
    }

    private fun updateFavoriteBanner() {
        val favSignId = FavoriteManager.getFavoriteSign(this)
        val favSign = signsByMonth.firstOrNull { it.id.equals(favSignId, ignoreCase = true) }

        if (favSign != null) {
            binding.favoriteBannerCard.visibility = View.VISIBLE
            binding.favSignImage.setImageResource(favSign.imageResId)
            binding.favSignName.setText(favSign.nameResId)
            binding.favSignDate.setText(favSign.dateRangeResId)

            binding.favoriteBannerCard.setOnClickListener {
                val intent = Intent(this, HoroscopeActivity::class.java).apply {
                    putExtra(HoroscopeActivity.EXTRA_SIGN_ID, favSign.id)
                    putExtra(HoroscopeActivity.EXTRA_SIGN_NAME_RES, favSign.nameResId)
                    putExtra(HoroscopeActivity.EXTRA_BACKGROUND_RES, favSign.backgroundResId)
                }
                startActivity(intent)
            }
        } else {
            binding.favoriteBannerCard.visibility = View.GONE
        }
    }

    private fun showLanguageDialog() {
        val options = arrayOf(
            getString(R.string.lang_system_default),
            getString(R.string.lang_spanish),
            getString(R.string.lang_english)
        )

        val current = LocaleHelper.getSelectedLanguage(this)
        val selectedIndex = when (current) {
            LocaleHelper.LANG_ES -> 1
            LocaleHelper.LANG_EN -> 2
            else -> 0
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(options, selectedIndex) { dialog, which ->
                val newLang = when (which) {
                    1 -> LocaleHelper.LANG_ES
                    2 -> LocaleHelper.LANG_EN
                    else -> LocaleHelper.LANG_DEFAULT
                }
                LocaleHelper.setLanguage(this, newLang)
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
