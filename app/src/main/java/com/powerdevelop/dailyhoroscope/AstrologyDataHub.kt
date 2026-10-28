package com.powerdevelop.dailyhoroscope

import java.security.MessageDigest
import kotlin.math.abs

data class CosmicLevels(
    val lovePercent: Int,
    val moneyPercent: Int,
    val healthPercent: Int
)

data class SignProfile(
    val element: String,
    val rulingPlanet: String,
    val strengths: String,
    val symbol: String
)

data class TarotCard(
    val name: String,
    val meaning: String,
    val iconEmoji: String
)

object AstrologyDataHub {

    // 1. Cosmic Levels Generator (Consistent per sign & day)
    fun getCosmicLevels(sign: String, date: String): CosmicLevels {
        val seed = "cosmic-$sign-$date"
        val hash = MessageDigest.getInstance("MD5").digest(seed.toByteArray())

        val love = 60 + (abs(hash[0].toInt()) % 39)     // 60 - 98%
        val money = 55 + (abs(hash[1].toInt()) % 44)    // 55 - 98%
        val health = 65 + (abs(hash[2].toInt()) % 34)   // 65 - 98%

        return CosmicLevels(love, money, health)
    }

    // 2. Sign Profile (Permanent encyclopedic traits)
    fun getSignProfile(sign: String, isSpanish: Boolean): SignProfile {
        return if (isSpanish) {
            when (sign.lowercase()) {
                "aries" -> SignProfile("Fuego 🔥", "Marte ♂️", "Valiente, determinado, optimista, apasionado", "El Carnero ♈")
                "taurus" -> SignProfile("Tierra 🌍", "Venus ♀️", "Confiable, paciente, leal, perseverante", "El Toro ♉")
                "gemini" -> SignProfile("Aire 💨", "Mercurio ☿", "Adaptable, elocuente, curioso, sociable", "Los Gemelos ♊")
                "cancer" -> SignProfile("Agua 🌊", "La Luna 🌙", "Intuitivo, protector, empático, afectuoso", "El Cangrejo ♋")
                "leo" -> SignProfile("Fuego 🔥", "El Sol ☀️", "Creativo, generoso, noble, carismático", "El León ♌")
                "virgo" -> SignProfile("Tierra 🌍", "Mercurio ☿", "Analítico, metódico, práctico, bondadoso", "La Virgen ♍")
                "libra" -> SignProfile("Aire 💨", "Venus ♀️", "Diplomático, justo, armónico, elegante", "La Balanza ♎")
                "scorpio" -> SignProfile("Agua 🌊", "Plutón y Marte ♏", "Apasionado, perspicaz, leal, magnético", "El Escorpión ♏")
                "sagittarius" -> SignProfile("Fuego 🔥", "Júpiter ♃", "Generoso, idealista, audaz, alegre", "El Centauro / Arquero ♐")
                "capricorn" -> SignProfile("Tierra 🌍", "Saturno ♄", "Responsable, disciplinado, sabio, tenaz", "La Cabra Marina ♑")
                "aquarius" -> SignProfile("Aire 💨", "Urano ♅", "Progresista, original, humanitario, libre", "El Portador de Agua ♒")
                "pisces" -> SignProfile("Agua 🌊", "Neptuno ♆", "Compasivo, artístico, intuitivo, sabio", "Los Peces ♓")
                else -> SignProfile("Estelar ✨", "El Cosmos 🌌", "Sabio, intuitivo, libre", "El Universo")
            }
        } else {
            when (sign.lowercase()) {
                "aries" -> SignProfile("Fire 🔥", "Mars ♂️", "Courageous, determined, confident, passionate", "The Ram ♈")
                "taurus" -> SignProfile("Earth 🌍", "Venus ♀️", "Reliable, patient, devoted, responsible", "The Bull ♉")
                "gemini" -> SignProfile("Air 💨", "Mercury ☿", "Adaptable, eloquent, curious, outgoing", "The Twins ♊")
                "cancer" -> SignProfile("Water 🌊", "The Moon 🌙", "Tenacious, highly imaginative, loyal, empathetic", "The Crab ♋")
                "leo" -> SignProfile("Fire 🔥", "The Sun ☀️", "Creative, passionate, generous, warm-hearted", "The Lion ♌")
                "virgo" -> SignProfile("Earth 🌍", "Mercury ☿", "Loyal, analytical, kind, hardworking, practical", "The Maiden ♍")
                "libra" -> SignProfile("Air 💨", "Venus ♀️", "Cooperative, diplomatic, gracious, fair-minded", "The Scales ♎")
                "scorpio" -> SignProfile("Water 🌊", "Pluto & Mars ♏", "Resourceful, brave, passionate, a true friend", "The Scorpion ♏")
                "sagittarius" -> SignProfile("Fire 🔥", "Jupiter ♃", "Generous, idealistic, great sense of humor", "The Archer ♐")
                "capricorn" -> SignProfile("Earth 🌍", "Saturn ♄", "Responsible, disciplined, self-control, leadership", "The Sea-Goat ♑")
                "aquarius" -> SignProfile("Air 💨", "Uranus ♅", "Progressive, original, independent, humanitarian", "The Water-Bearer ♒")
                "pisces" -> SignProfile("Water 🌊", "Neptune ♆", "Compassionate, artistic, intuitive, gentle, wise", "The Fishes ♓")
                else -> SignProfile("Cosmic ✨", "The Cosmos 🌌", "Wise, intuitive, open", "The Universe")
            }
        }
    }

    // 3. Daily Tarot Card (22 Major Arcana)
    private val TAROT_ES = listOf(
        TarotCard("El Mago", "Poder creador, iniciativa y nuevos comienzos. Tienes todos los recursos para triunfar.", "🪄"),
        TarotCard("La Sacerdotisa", "Intuición profunda, serenidad y misterio. Escucha la voz silenciosa de tu interior.", "🔮"),
        TarotCard("La Emperatriz", "Abundancia, fertilidad y calidez. Tus proyectos florecen con gracia.", "👑"),
        TarotCard("El Emperador", "Estructura, autoridad y estabilidad. Mantén tus metas claras y disciplina firme.", "🏛️"),
        TarotCard("El Hierofante", "Sabiduría espiritual, guía y tradición. Busca consejo en quien tenga experiencia.", "📜"),
        TarotCard("Los Enamorados", "Decisiones del corazón, unión y armonía. Elige siempre con honestidad y amor.", "❤️"),
        TarotCard("El Carro", "Avance triunfal, determinación y victoria. Mantén el control de tus emociones.", "🏇"),
        TarotCard("La Fuerza", "Paciencia, coraje y dominio suave. La compasión supera cualquier adversidad.", "🦁"),
        TarotCard("El Ermitaño", "Reflexión serena, búsqueda interior y sabiduría. Un momento de calma aclara tu camino.", "🏮"),
        TarotCard("La Rueda de la Fortuna", "Ciclos favorables, giros del destino y buena suerte. El cambio trae oportunidades.", "☸️"),
        TarotCard("La Justicia", "Equilibrio, verdad y causa y efecto. Lo que das con bondad regresa multiplicado.", "⚖️"),
        TarotCard("El Colgado", "Nueva perspectiva, pausa sabia y desapego. Ver las cosas desde otro ángulo te liberará.", "🕊️"),
        TarotCard("La Muerte (Renovación)", "Transformación profunda y cierre de ciclos. Lo viejo da paso a un renacer radiante.", "🦋"),
        TarotCard("La Templanza", "Moderación, paz interior y armonía. La paciencia convierte el plomo en oro.", "🏺"),
        TarotCard("La Estrella", "Esperanza, fe luminosa e inspiración cósmica. Tus deseos reciben el favor estelar.", "⭐"),
        TarotCard("La Luna", "Sueños lúcidos, intuición y revelaciones. Confía en lo que sientes más que en las apariencias.", "🌕"),
        TarotCard("El Sol", "Alegría radiante, éxito, claridad y vitalidad. Hoy brillas con luz propia.", "☀️"),
        TarotCard("El Juicio", "Despertar, renovación y llamados del destino. Ha llegado el momento de dar el salto.", "📯"),
        TarotCard("El Mundo", "Plenitud, logro supremo y culminación feliz. El universo conspira a tu favor.", "🌍"),
        TarotCard("El Loco", "Espontaneidad, valentía y comienzos libres. Da ese paso de fe con entusiasmo.", "🎒")
    )

    private val TAROT_EN = listOf(
        TarotCard("The Magician", "Creative power, initiative and new beginnings. You hold all the tools for success.", "🪄"),
        TarotCard("The High Priestess", "Deep intuition, serenity and mystery. Trust the quiet inner voice within you.", "🔮"),
        TarotCard("The Empress", "Abundance, fertility and grace. Your efforts and creative ideas are blossoming.", "👑"),
        TarotCard("The Emperor", "Structure, authority and stability. Stay focused with discipline and confidence.", "🏛️"),
        TarotCard("The Hierophant", "Spiritual wisdom, guidance and knowledge. Seek perspective from trusted mentors.", "📜"),
        TarotCard("The Lovers", "Choices from the heart, union and alignment. Choose with authenticity and love.", "❤️"),
        TarotCard("The Chariot", "Triumph, focused drive and willpower. Determination overcomes any obstacle.", "🏇"),
        TarotCard("Strength", "Courage, gentle resilience and patience. Compassion is your greatest power.", "🦁"),
        TarotCard("The Hermit", "Inner reflection, illumination and wisdom. Quiet moments bring absolute clarity.", "🏮"),
        TarotCard("Wheel of Fortune", "Favorable cycles, positive destiny and good luck. Change brings new blessings.", "☸️"),
        TarotCard("Justice", "Balance, truth and clarity. Fairness and honorable choices yield great rewards.", "⚖️"),
        TarotCard("The Hanged Man", "Fresh perspective, surrender and wisdom. Looking differently sets you free.", "🕊️"),
        TarotCard("Transformation", "Profound renewal and positive endings. Old patterns give way to brilliant rebirth.", "🦋"),
        TarotCard("Temperance", "Peace, balance, harmony and moderation. Patience transforms any situation.", "🏺"),
        TarotCard("The Star", "Hope, divine inspiration and faith. The universe showers you with blessings.", "⭐"),
        TarotCard("The Moon", "Dreams, intuition and subconscious truth. Look beyond surfaces and trust feelings.", "🌕"),
        TarotCard("The Sun", "Radiant joy, clarity, vitality and success. You shine brightly wherever you go.", "☀️"),
        TarotCard("Judgement", "Awakening, renewed purpose and life calling. Step into your highest potential.", "📯"),
        TarotCard("The World", "Completion, fulfillment and triumph. You have reached a state of harmony.", "🌍"),
        TarotCard("The Fool", "Spontaneous adventure, courage and freedom. Take that bold leap of faith.", "🎒")
    )

    fun getDailyTarot(sign: String, date: String, isSpanish: Boolean): TarotCard {
        val seed = "tarot-$sign-$date"
        val hash = MessageDigest.getInstance("MD5").digest(seed.toByteArray())
        val list = if (isSpanish) TAROT_ES else TAROT_EN
        val index = abs(hash[0].toInt()) % list.size
        return list[index]
    }

    // 4. Fortune Cookie Messages
    private val COOKIES_ES = listOf(
        "Hoy una sorpresa inesperada llenará tu corazón de alegría.",
        "La paciencia es el puente entre tus sueños y su realización.",
        "Confía en el proceso: lo que parece un obstáculo es una lección de maestría.",
        "Una puerta que creías cerrada comenzará a abrirse a tu favor.",
        "Tu energía positiva hoy es contagiosa e iluminará a quienes te rodean.",
        "El secreto de avanzar es dar el primer paso sin mirar atrás.",
        "Una conversación sincera resolverá aquello que te inquietaba.",
        "El universo siempre conspira a favor de los corazones sinceros.",
        "Hoy tu intuición está más afilada que nunca: hazle caso.",
        "Grandes logros están hechos de pequeñas decisiones constantes."
    )

    private val COOKIES_EN = listOf(
        "An unexpected blessing will fill your day with joy.",
        "Patience is the golden bridge between dreams and realization.",
        "Trust the journey: what seems like an obstacle is guidance in disguise.",
        "A door you thought was closed is quietly preparing to open for you.",
        "Your positive energy is contagious and will brighten many hearts today.",
        "The secret to moving forward is taking the first step without hesitation.",
        "An honest conversation will resolve what was lingering in your mind.",
        "The universe always works in favor of genuine intentions.",
        "Your intuition is sharper than ever today: listen closely to it.",
        "Great triumphs are made of small, disciplined, daily choices."
    )

    fun getFortuneCookie(date: String, isSpanish: Boolean): String {
        val seed = "cookie-$date"
        val hash = MessageDigest.getInstance("MD5").digest(seed.toByteArray())
        val list = if (isSpanish) COOKIES_ES else COOKIES_EN
        val index = abs(hash[0].toInt()) % list.size
        return list[index]
    }
}

