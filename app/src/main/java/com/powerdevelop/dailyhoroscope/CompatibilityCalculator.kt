package com.powerdevelop.dailyhoroscope

import java.security.MessageDigest
import kotlin.math.abs

data class CompatibilityResult(
    val loveScore: Int,
    val friendshipScore: Int,
    val workScore: Int,
    val summary: String
)

object CompatibilityCalculator {

    fun calculate(signA: String, signB: String, isSpanish: Boolean): CompatibilityResult {
        // Sort names so order doesn't change compatibility
        val pair = listOf(signA.lowercase(), signB.lowercase()).sorted().joinToString("-")
        val md = MessageDigest.getInstance("MD5").digest(pair.toByteArray())

        val love = 60 + (abs(md[0].toInt()) % 39) // 60% .. 98%
        val friendship = 65 + (abs(md[1].toInt()) % 34) // 65% .. 98%
        val work = 55 + (abs(md[2].toInt()) % 44) // 55% .. 98%

        val avg = (love + friendship + work) / 3

        val summary = if (isSpanish) {
            when {
                avg >= 85 -> "¡Afinidad cósmica excepcional! Se complementan y potencian mutuamente de forma natural."
                avg >= 75 -> "Gran conexión y entendimiento mutuo. Con buena comunicación superarán cualquier diferencia."
                else -> "Atracción magnética basada en contrastes. Sus diferencias son su mayor oportunidad de crecimiento."
            }
        } else {
            when {
                avg >= 85 -> "Exceptional cosmic harmony! You naturally complement and inspire each other."
                avg >= 75 -> "Strong mutual bond and understanding. Great communication overcomes any obstacle."
                else -> "Magnetic attraction through contrast. Your differences are your greatest strength."
            }
        }

        return CompatibilityResult(love, friendship, work, summary)
    }
}

