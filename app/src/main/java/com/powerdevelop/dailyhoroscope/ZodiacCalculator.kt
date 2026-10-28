package com.powerdevelop.dailyhoroscope

object ZodiacCalculator {

    /**
     * Determines sign ID ("capricorn", "aquarius", etc.) based on 1-based month (1..12) and day (1..31).
     */
    fun getSignByDate(month: Int, day: Int): String {
        return when (month) {
            1 -> if (day <= 19) "capricorn" else "aquarius"
            2 -> if (day <= 18) "aquarius" else "pisces"
            3 -> if (day <= 20) "pisces" else "aries"
            4 -> if (day <= 19) "aries" else "taurus"
            5 -> if (day <= 20) "taurus" else "gemini"
            6 -> if (day <= 20) "gemini" else "cancer"
            7 -> if (day <= 22) "cancer" else "leo"
            8 -> if (day <= 22) "leo" else "virgo"
            9 -> if (day <= 22) "virgo" else "libra"
            10 -> if (day <= 22) "libra" else "scorpio"
            11 -> if (day <= 21) "scorpio" else "sagittarius"
            12 -> if (day <= 21) "sagittarius" else "capricorn"
            else -> "aries"
        }
    }
}

