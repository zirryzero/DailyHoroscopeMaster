package com.powerdevelop.dailyhoroscope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.powerdevelop.dailyhoroscope.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.recyclerViewSigns.layoutManager = GridLayoutManager(this, 3)

        val signs = listOf(
            Sign("aries", R.string.sign_aries, R.string.date_aries, R.drawable.aries, R.drawable.background_aries),
            Sign("taurus", R.string.sign_taurus, R.string.date_taurus, R.drawable.taurus, R.drawable.background_taurus),
            Sign("gemini", R.string.sign_gemini, R.string.date_gemini, R.drawable.gemini, R.drawable.background_gemini),
            Sign("cancer", R.string.sign_cancer, R.string.date_cancer, R.drawable.cancer, R.drawable.background_cancer),
            Sign("leo", R.string.sign_leo, R.string.date_leo, R.drawable.leo, R.drawable.background_leo),
            Sign("virgo", R.string.sign_virgo, R.string.date_virgo, R.drawable.virgo, R.drawable.background_virgo),
            Sign("libra", R.string.sign_libra, R.string.date_libra, R.drawable.libra, R.drawable.background_libra),
            Sign("scorpio", R.string.sign_scorpio, R.string.date_scorpio, R.drawable.scorpio, R.drawable.background_scorpio),
            Sign("sagittarius", R.string.sign_sagittarius, R.string.date_sagittarius, R.drawable.sagittarius, R.drawable.background_sagittarius),
            Sign("capricorn", R.string.sign_capricorn, R.string.date_capricorn, R.drawable.capricorn, R.drawable.background_capricorn),
            Sign("aquarius", R.string.sign_aquarius, R.string.date_aquarius, R.drawable.aquarius, R.drawable.background_aquarius),
            Sign("pisces", R.string.sign_pisces, R.string.date_pisces, R.drawable.pisces, R.drawable.background_pisces)
        )

        binding.recyclerViewSigns.adapter = SignAdapter(this, signs)
    }
}
