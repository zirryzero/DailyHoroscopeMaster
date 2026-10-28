package com.powerdevelop.dailyhoroscope

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import java.security.MessageDigest
import kotlin.math.abs

data class DailyDetails(
    val luckyNumbers: List<Int>,
    @StringRes val colorNameRes: Int,
    @ColorInt val colorHex: Int
)

object HoroscopeExtrasGenerator {

    private val COLORS = listOf(
        Pair(R.string.color_gold, 0xFFFFD700.toInt()),
        Pair(R.string.color_emerald, 0xFF2E8B57.toInt()),
        Pair(R.string.color_ruby, 0xFFE0115F.toInt()),
        Pair(R.string.color_sapphire, 0xFF0F52BA.toInt()),
        Pair(R.string.color_amethyst, 0xFF9966CC.toInt()),
        Pair(R.string.color_turquoise, 0xFF40E0D0.toInt()),
        Pair(R.string.color_coral, 0xFFFF7F50.toInt()),
        Pair(R.string.color_silver, 0xFFC0C0C0.toInt()),
        Pair(R.string.color_violet, 0xFF8A2BE2.toInt()),
        Pair(R.string.color_amber, 0xFFFFBF00.toInt())
    )

    fun generate(sign: String, date: String): DailyDetails {
        val seed = "$sign-$date"
        val hash = md5Hash(seed)

        // Generate 3 unique lucky numbers between 1 and 99
        val numbers = mutableSetOf<Int>()
        var offset = 0
        while (numbers.size < 3 && offset < hash.size) {
            val num = (abs(hash[offset].toInt()) % 99) + 1
            numbers.add(num)
            offset++
        }
        while (numbers.size < 3) {
            numbers.add((numbers.size + 1) * 7)
        }

        // Color selection
        val colorIndex = abs(hash[0].toInt()) % COLORS.size
        val selectedColor = COLORS[colorIndex]

        return DailyDetails(
            luckyNumbers = numbers.sorted(),
            colorNameRes = selectedColor.first,
            colorHex = selectedColor.second
        )
    }

    private fun md5Hash(input: String): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray())
    }
}

