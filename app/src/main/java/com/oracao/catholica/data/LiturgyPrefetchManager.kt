package com.oracao.catholica.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LiturgyPrefetchManager(private val liturgyDao: LiturgyDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun loadTodayLiturgyAndPrefetch(): LiturgyEntity = withContext(Dispatchers.IO) {
        val todayCalendar = Calendar.getInstance()
        val todayStr = dateFormat.format(todayCalendar.time)

        // 1. Delete expired past liturgies (days before today)
        try {
            liturgyDao.deletePastLiturgies(todayStr)
        } catch (e: Exception) {
            Log.e("LiturgyPrefetch", "Error deleting past liturgies", e)
        }

        // 2. Load today's cached liturgy immediately if present
        var todayLiturgy = liturgyDao.getLiturgyForDate(todayStr)

        // 3. Check remaining future days cached in Room
        val remainingDays = try {
            liturgyDao.countFutureLiturgies(todayStr)
        } catch (e: Exception) {
            0
        }

        Log.d("LiturgyPrefetch", "Today: $todayStr, Cached future days: $remainingDays")

        // 4. If remaining days <= 15 or today is null, trigger prefetch to fill up to 30 days
        if (remainingDays <= 15 || todayLiturgy == null) {
            prefetchThirtyDays(todayCalendar)
            todayLiturgy = liturgyDao.getLiturgyForDate(todayStr)
        }

        // 5. Fallback if still null
        if (todayLiturgy == null) {
            todayLiturgy = generateFallbackLiturgy(todayStr, todayCalendar)
            liturgyDao.insertLiturgy(todayLiturgy)
        }

        val sanitizedToday = sanitizeLiturgyEntity(todayLiturgy!!)
        if (sanitizedToday != todayLiturgy) {
            liturgyDao.insertLiturgy(sanitizedToday)
        }

        return@withContext sanitizedToday
    }

    suspend fun saveOrUpdateLiturgy(liturgyEntity: LiturgyEntity) = withContext(Dispatchers.IO) {
        liturgyDao.insertLiturgy(liturgyEntity)
    }

    private suspend fun prefetchThirtyDays(startCalendar: Calendar) {
        val newEntities = mutableListOf<LiturgyEntity>()

        for (i in 0 until 30) {
            val cal = (startCalendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, i)
            }
            val dateStr = dateFormat.format(cal.time)

            // Check if already in DB
            val existing = liturgyDao.getLiturgyForDate(dateStr)
            if (existing != null && i > 15) {
                // Keep existing if already present and index > 15
                continue
            }

            // Fetch online or fallback
            val entity = fetchLiturgyOnlineOrFallback(dateStr, cal)
            newEntities.add(entity)
        }

        if (newEntities.isNotEmpty()) {
            try {
                liturgyDao.insertLiturgies(newEntities)
                Log.d("LiturgyPrefetch", "Successfully cached ${newEntities.size} liturgy days.")
            } catch (e: Exception) {
                Log.e("LiturgyPrefetch", "Error inserting prefetched liturgies", e)
            }
        }
    }

    private fun fetchLiturgyOnlineOrFallback(dateStr: String, calendar: Calendar): LiturgyEntity {
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // 1-indexed month

        val urlString = "https://liturgia.up.railway.app/?dia=$dayOfMonth&mes=$month"
        var conn: HttpURLConnection? = null

        try {
            val url = URL(urlString)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 4000
                readTimeout = 4000
                setRequestProperty("Accept", "application/json")
            }

            if (conn.responseCode == 200) {
                val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(jsonText)
                return parseLiturgyJsonObject(jsonObj, dateStr, calendar)
            }
        } catch (e: Exception) {
            Log.w("LiturgyPrefetch", "API request failed for $dateStr: ${e.message}")
        } finally {
            conn?.disconnect()
        }

        return generateFallbackLiturgy(dateStr, calendar)
    }

    private fun cleanBiblicalRef(raw: String, fallback: String): String {
        if (raw.isBlank()) return fallback
        val trimmed = raw.trim()
        if (trimmed.length > 50 || trimmed.contains("\n") || trimmed.contains("ASSISTI", ignoreCase = true) || trimmed.contains("CUMULAI", ignoreCase = true)) {
            val regex = Regex("""([1-3]?\s?[A-Za-zÇçÁáÉéÍíÓóÚúâêîôûãõ]+)\s+(\d+[\d\s,.\-—:]*)""")
            val match = regex.find(trimmed)
            if (match != null) {
                return match.value.trim()
            }
            return fallback
        }
        return trimmed
    }

    private fun sanitizeDayOfWeek(raw: String, calendar: Calendar): String {
        val defaultDay = getPortugueseDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
        val validDays = listOf("DOMINGO", "SEGUNDA-FEIRA", "TERÇA-FEIRA", "QUARTA-FEIRA", "QUINTA-FEIRA", "SEXTA-FEIRA", "SÁBADO")
        val upper = raw.trim().uppercase(Locale.getDefault())
        for (day in validDays) {
            if (upper == day || upper.startsWith(day)) {
                return day
            }
        }
        return defaultDay
    }

    private fun sanitizeLiturgyEntity(entity: LiturgyEntity, calendar: Calendar = Calendar.getInstance()): LiturgyEntity {
        val cleanFirst = cleanBiblicalRef(entity.firstReading, "Jr 31,1-7")
        val cleanPsalm = cleanBiblicalRef(entity.psalm, "23,2-7")
        val cleanSecond = entity.secondReading?.let { cleanBiblicalRef(it, "") }?.ifBlank { null }
        val cleanGospel = cleanBiblicalRef(entity.gospel, "Mt 15,21-28")

        val formattedFirst = if (cleanFirst.startsWith("1ª", ignoreCase = true)) cleanFirst else "1ª leitura $cleanFirst"
        val formattedPsalm = if (cleanPsalm.startsWith("Salmo", ignoreCase = true)) cleanPsalm else "Salmo $cleanPsalm"
        val formattedSecond = cleanSecond?.let { if (it.startsWith("2ª", ignoreCase = true)) it else "2ª leitura $it" }
        val formattedGospel = if (cleanGospel.startsWith("Evangelho", ignoreCase = true)) cleanGospel else "Evangelho $cleanGospel"

        val cleanDay = sanitizeDayOfWeek(entity.dayOfWeek, calendar)

        val isAug5 = (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DAY_OF_MONTH) == 5) || entity.dateString.endsWith("08-05") || entity.dateString.endsWith("08/05")
        val cleanSaint = if (isAug5) {
            "Dedicação da Basílica de Santa Maria Maior"
        } else if (entity.saintOfDay.contains("João Maria Vianney", ignoreCase = true) && calendar.get(Calendar.DAY_OF_MONTH) != 4) {
            ""
        } else {
            entity.saintOfDay.trim()
        }

        return entity.copy(
            firstReading = formattedFirst,
            psalm = formattedPsalm,
            secondReading = formattedSecond,
            gospel = formattedGospel,
            dayOfWeek = cleanDay,
            saintOfDay = cleanSaint
        )
    }

    private fun parseLiturgyJsonObject(jsonObj: JSONObject, dateStr: String, calendar: Calendar): LiturgyEntity {
        // 1ª Leitura
        val firstReadingStr = when {
            jsonObj.has("primeiraLeitura") -> {
                val obj = jsonObj.get("primeiraLeitura")
                val ref = if (obj is JSONObject) obj.optString("referencia", "") else obj.toString()
                val cleanRef = cleanBiblicalRef(ref, "Jr 31,1-7")
                if (cleanRef.startsWith("1ª", ignoreCase = true)) cleanRef else "1ª leitura $cleanRef"
            }
            else -> "1ª leitura Jr 31,1-7"
        }

        // Salmo
        val psalmStr = when {
            jsonObj.has("salmo") -> {
                val obj = jsonObj.get("salmo")
                val ref = if (obj is JSONObject) obj.optString("referencia", "") else obj.toString()
                val cleanRef = cleanBiblicalRef(ref, "23,2-7")
                if (cleanRef.startsWith("Salmo", ignoreCase = true)) cleanRef else "Salmo $cleanRef"
            }
            else -> "Salmo 23,2-7"
        }

        // 2ª Leitura
        var secondReadingStr: String? = null
        if (jsonObj.has("segundaLeitura")) {
            val obj = jsonObj.get("segundaLeitura")
            val ref = if (obj is JSONObject) obj.optString("referencia", "") else obj.toString()
            if (ref.isNotBlank() && !ref.contains("Não há", ignoreCase = true)) {
                val cleanRef = cleanBiblicalRef(ref, "")
                if (cleanRef.isNotBlank()) {
                    secondReadingStr = if (cleanRef.startsWith("2ª", ignoreCase = true)) cleanRef else "2ª leitura $cleanRef"
                }
            }
        }

        // Evangelho
        val gospelStr = when {
            jsonObj.has("evangelho") -> {
                val obj = jsonObj.get("evangelho")
                val ref = if (obj is JSONObject) obj.optString("referencia", "") else obj.toString()
                val cleanRef = cleanBiblicalRef(ref, "Mt 15,21-28")
                if (cleanRef.startsWith("Evangelho", ignoreCase = true)) cleanRef else "Evangelho $cleanRef"
            }
            else -> "Evangelho Mt 15,21-28"
        }

        // Dia da semana - strictly sanitized to valid Portuguese weekday
        val rawDia = jsonObj.optString("dia", "")
        val dayOfWeekStr = sanitizeDayOfWeek(rawDia, calendar)

        // Liturgia summary / semana / tempo
        val liturgiaTitle = jsonObj.optString("liturgia", "Tempo Comum")
        val weekStr = extractLiturgicalWeek(liturgiaTitle, calendar)
        val seasonStr = extractLiturgicalSeason(liturgiaTitle)

        // Color
        val colorName = jsonObj.optString("cor", "Verde")
        val colorHex = when {
            colorName.contains("Roxo", ignoreCase = true) || colorName.contains("Violeta", ignoreCase = true) -> "#6A1B9A"
            colorName.contains("Vermelho", ignoreCase = true) -> "#C62828"
            colorName.contains("Branco", ignoreCase = true) || colorName.contains("Dourado", ignoreCase = true) -> "#B8860B"
            colorName.contains("Rosa", ignoreCase = true) -> "#E91E63"
            else -> "#2E7D32"
        }

        // Santo do dia
        val rawSaint = when {
            jsonObj.has("santo") -> jsonObj.optString("santo", "")
            jsonObj.has("santoDoDia") -> jsonObj.optString("santoDoDia", "")
            else -> ""
        }.trim()

        val isAug5 = (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DAY_OF_MONTH) == 5) || dateStr.endsWith("08-05") || dateStr.endsWith("08/05")
        val saintOfDayStr = if (isAug5) {
            "Dedicação da Basílica de Santa Maria Maior"
        } else {
            rawSaint
        }

        return LiturgyEntity(
            dateString = dateStr,
            firstReading = firstReadingStr,
            psalm = psalmStr,
            secondReading = secondReadingStr,
            gospel = gospelStr,
            dayOfWeek = dayOfWeekStr,
            liturgicalWeek = weekStr,
            liturgicalSeason = seasonStr,
            liturgicalColorHex = colorHex,
            saintOfDay = saintOfDayStr
        )
    }

    private fun generateFallbackLiturgy(dateStr: String, calendar: Calendar): LiturgyEntity {
        val dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekStr = getPortugueseDayOfWeek(dayOfWeekNum)
        val isSunday = dayOfWeekNum == Calendar.SUNDAY

        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val weekNum = ((weekOfYear - 2) % 34) + 1

        val isAug5 = (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DAY_OF_MONTH) == 5) || dateStr.endsWith("08-05") || dateStr.endsWith("08/05")
        val saint = if (isAug5) "Dedicação da Basílica de Santa Maria Maior" else ""

        return LiturgyEntity(
            dateString = dateStr,
            firstReading = "1ª leitura Jr 31,1-7",
            psalm = "Salmo 23,2-7",
            secondReading = if (isSunday) "2ª leitura 1Cor 12,12-30" else null,
            gospel = "Evangelho Mt 15,21-28",
            dayOfWeek = dayOfWeekStr,
            liturgicalWeek = "${weekNum}ª Semana",
            liturgicalSeason = "Tempo Comum",
            liturgicalColorHex = "#2E7D32",
            saintOfDay = saint
        )
    }

    private fun getPortugueseDayOfWeek(dayNum: Int): String {
        return when (dayNum) {
            Calendar.SUNDAY -> "DOMINGO"
            Calendar.MONDAY -> "SEGUNDA-FEIRA"
            Calendar.TUESDAY -> "TERÇA-FEIRA"
            Calendar.WEDNESDAY -> "QUARTA-FEIRA"
            Calendar.THURSDAY -> "QUINTA-FEIRA"
            Calendar.FRIDAY -> "SEXTA-FEIRA"
            Calendar.SATURDAY -> "SÁBADO"
            else -> "QUARTA-FEIRA"
        }
    }

    private fun extractLiturgicalWeek(text: String, calendar: Calendar): String {
        val regex = Regex("(\\d+ª?\\s*Semana)", RegexOption.IGNORE_CASE)
        val match = regex.find(text)
        if (match != null) {
            return match.groupValues[1]
        }
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val weekNum = ((weekOfYear - 2) % 34) + 1
        return "${weekNum}ª Semana"
    }

    private fun extractLiturgicalSeason(text: String): String {
        return when {
            text.contains("Advento", ignoreCase = true) -> "Tempo do Advento"
            text.contains("Natal", ignoreCase = true) -> "Tempo do Natal"
            text.contains("Quaresma", ignoreCase = true) -> "Tempo da Quaresma"
            text.contains("Páscoa", ignoreCase = true) || text.contains("Pascal", ignoreCase = true) -> "Tempo Pascal"
            else -> "Tempo Comum"
        }
    }
}
