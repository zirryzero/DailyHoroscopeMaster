package com.powerdevelop.dailyhoroscope

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.powerdevelop.dailyhoroscope.databinding.ActivityMainBinding
import com.powerdevelop.dailyhoroscope.databinding.DialogCompatibilityBinding
import com.powerdevelop.dailyhoroscope.databinding.DialogTarotBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openTimePickerForReminder()
            } else {
                Toast.makeText(this, R.string.reminder_disabled, Toast.LENGTH_SHORT).show()
            }
        }

    private val signsByMonth = listOf(
        Sign("capricorn", R.string.sign_capricorn, R.string.date_capricorn, R.drawable.capricorn, R.drawable.background_capricorn),
        Sign("aquarius", R.string.sign_aquarius, R.string.date_aquarius, R.drawable.aquarius, R.drawable.background_aquarius),
        Sign("pisces", R.string.sign_pisces, R.string.date_pisces, R.drawable.pisces, R.drawable.background_pisces),
        Sign("aries", R.string.sign_aries, R.string.date_aries, R.drawable.aries, R.drawable.background_aries),
        Sign("taurus", R.string.sign_taurus, R.string.date_taurus, R.drawable.taurus, R.drawable.background_taurus),
        Sign("gemini", R.string.sign_gemini, R.string.date_gemini, R.drawable.gemini, R.drawable.background_gemini),
        Sign("cancer", R.string.sign_cancer, R.string.date_cancer, R.drawable.cancer, R.drawable.background_cancer),
        Sign("leo", R.string.sign_leo, R.string.date_leo, R.drawable.leo, R.drawable.background_leo),
        Sign("virgo", R.string.sign_virgo, R.string.date_virgo, R.drawable.virgo, R.drawable.background_virgo),
        Sign("libra", R.string.sign_libra, R.string.date_libra, R.drawable.libra, R.drawable.background_libra),
        Sign("scorpio", R.string.sign_scorpio, R.string.date_scorpio, R.drawable.scorpio, R.drawable.background_scorpio),
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

        // Notification Reminder button
        binding.btnNotification.setOnClickListener {
            checkAndRequestNotificationPermission()
        }

        // Find My Sign tool
        binding.btnFindSignCard.setOnClickListener {
            showFindSignDatePicker()
        }

        // Compatibility tool
        binding.btnCompatibilityCard.setOnClickListener {
            showCompatibilityDialog()
        }

        // Daily Tarot tool
        binding.btnTarotCard.setOnClickListener {
            showDailyTarotDialog()
        }

        // Fortune Cookie tool
        binding.btnFortuneCookieCard.setOnClickListener {
            showFortuneCookieDialog()
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
                openHoroscope(favSign)
            }
        } else {
            binding.favoriteBannerCard.visibility = View.GONE
        }
    }

    private fun openHoroscope(sign: Sign) {
        val intent = Intent(this, HoroscopeActivity::class.java).apply {
            putExtra(HoroscopeActivity.EXTRA_SIGN_ID, sign.id)
            putExtra(HoroscopeActivity.EXTRA_SIGN_NAME_RES, sign.nameResId)
            putExtra(HoroscopeActivity.EXTRA_BACKGROUND_RES, sign.backgroundResId)
        }
        startActivity(intent)
    }

    private fun showFindSignDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, _, monthOfYear, dayOfMonth ->
                val signId = ZodiacCalculator.getSignByDate(monthOfYear + 1, dayOfMonth)
                val sign = signsByMonth.firstOrNull { it.id.equals(signId, ignoreCase = true) }
                if (sign != null) {
                    openHoroscope(sign)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.setTitle(getString(R.string.select_birth_date))
        datePicker.show()
    }

    private fun showCompatibilityDialog() {
        val dialogBinding = DialogCompatibilityBinding.inflate(LayoutInflater.from(this))
        val signNames = signsByMonth.map { getString(it.nameResId) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, signNames)
        dialogBinding.spinnerSignA.adapter = adapter
        dialogBinding.spinnerSignB.adapter = adapter

        val isSpanish = LocaleHelper.isSpanish(this)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.calculate_compatibility, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            val calcBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            calcBtn.setOnClickListener {
                val indexA = dialogBinding.spinnerSignA.selectedItemPosition
                val indexB = dialogBinding.spinnerSignB.selectedItemPosition
                val signA = signsByMonth[indexA]
                val signB = signsByMonth[indexB]

                val result = CompatibilityCalculator.calculate(signA.id, signB.id, isSpanish)
                dialogBinding.compatibilityResultLayout.visibility = View.VISIBLE
                dialogBinding.resultSummaryText.text = result.summary
                dialogBinding.resultLoveText.text = "❤️ ${getString(R.string.love_compatibility)}: ${result.loveScore}%"
                dialogBinding.resultFriendshipText.text = "🤝 ${getString(R.string.friendship_compatibility)}: ${result.friendshipScore}%"
                dialogBinding.resultWorkText.text = "💼 ${getString(R.string.work_compatibility)}: ${result.workScore}%"
            }
        }
        dialog.show()
    }

    private fun showDailyTarotDialog() {
        val isSpanish = LocaleHelper.isSpanish(this)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val favSignId = FavoriteManager.getFavoriteSign(this) ?: "general"
        val tarot = AstrologyDataHub.getDailyTarot(favSignId, todayStr, isSpanish)

        val dialogBinding = DialogTarotBinding.inflate(LayoutInflater.from(this))
        dialogBinding.tarotEmoji.text = tarot.iconEmoji
        dialogBinding.tarotCardName.text = tarot.name
        dialogBinding.tarotCardMeaning.text = tarot.meaning

        AlertDialog.Builder(this)
            .setTitle(R.string.daily_tarot)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun showFortuneCookieDialog() {
        val isSpanish = LocaleHelper.isSpanish(this)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val message = AstrologyDataHub.getFortuneCookie(todayStr, isSpanish)

        AlertDialog.Builder(this)
            .setTitle(R.string.fortune_cookie_title)
            .setMessage("🥠 \"$message\"")
            .setIcon(R.drawable.ic_fortune_smile)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                openTimePickerForReminder()
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            openTimePickerForReminder()
        }
    }

    private fun openTimePickerForReminder() {
        val (hour, minute) = NotificationHelper.getReminderTime(this)

        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                NotificationHelper.setReminder(this, true, selectedHour, selectedMinute)
                val timeStr = String.format("%02d:%02d", selectedHour, selectedMinute)
                Toast.makeText(this, getString(R.string.reminder_enabled, timeStr), Toast.LENGTH_SHORT).show()
            },
            hour,
            minute,
            true
        ).apply {
            setTitle(getString(R.string.daily_reminder))
            setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel)) { _, _ ->
                NotificationHelper.setReminder(this@MainActivity, false)
                Toast.makeText(this@MainActivity, R.string.reminder_disabled, Toast.LENGTH_SHORT).show()
            }
            show()
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
