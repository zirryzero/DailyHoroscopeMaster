package com.powerdevelop.dailyhoroscope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        val adView = findViewById<AdView>(R.id.adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        recyclerView = findViewById(R.id.recyclerViewSigns)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val signs = listOf(
            Sign("aries", R.drawable.aries),
            Sign("taurus", R.drawable.taurus),
            Sign("gemini", R.drawable.gemini),
            Sign("cancer", R.drawable.cancer),
            Sign("leo", R.drawable.leo),
            Sign("virgo", R.drawable.virgo),
            Sign("libra", R.drawable.libra),
            Sign("scorpio", R.drawable.scorpio),
            Sign("sagittarius", R.drawable.sagittarius),
            Sign("capricorn", R.drawable.capricorn),
            Sign("aquarius", R.drawable.aquarius),
            Sign("pisces", R.drawable.pisces)
        )

        recyclerView.adapter = SignAdapter(this, signs)
    }
}
