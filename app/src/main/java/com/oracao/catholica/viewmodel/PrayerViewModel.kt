package com.oracao.catholica.viewmodel

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oracao.catholica.data.LiturgyEntity
import com.oracao.catholica.data.LiturgyPrefetchManager
import com.oracao.catholica.data.PrayerDatabase
import com.oracao.catholica.data.PrayerEntity
import com.oracao.catholica.data.PrayerRepository
import com.oracao.catholica.data.PrayerSeedData
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PrayerDatabase.getDatabase(application, viewModelScope)
    private val repository = PrayerRepository(database.prayerDao())
    private val liturgyPrefetchManager = LiturgyPrefetchManager(database.liturgyDao())
    private var tts: TextToSpeech? = null
    val isTtsReady = MutableStateFlow(false)
    val isSpeaking = MutableStateFlow(false)

    private val prefs = application.getSharedPreferences("catholic_prayer_prefs", Context.MODE_PRIVATE)

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("") // Empty string means Home / All
    val selectedPrayer = MutableStateFlow<PrayerEntity?>(null)
    val fontSizeSp = MutableStateFlow(prefs.getInt("saved_font_size_sp", 18))
    val textAlignState = MutableStateFlow(
        when (prefs.getString("saved_text_align_name", "Start")) {
            "Center" -> TextAlign.Center
            "Justify" -> TextAlign.Justify
            else -> TextAlign.Start
        }
    )

    // Admin & Sync States
    val isAdminMode = MutableStateFlow(false)
    val syncUrlState = MutableStateFlow("")
    val syncStatusMessage = MutableStateFlow<String?>(null)
    val isSyncing = MutableStateFlow(false)

    private val defaultCategories = listOf(
        "Terços",
        "Rosário",
        "Orações",
        "Novenas",
        "Catequese",
        "Ação de Graças",
        "Quaresma de São Miguel",
        "Preparação para confissão",
        "Preparação para a Santa Missa"
    )
    val categoriesState = MutableStateFlow<List<String>>(defaultCategories)
    val categories: List<String> get() = categoriesState.value

    // Permanent favorites: Creio, Pai Nosso, Ave Maria, Glória ao Pai + User favorites, sorted alphabetically
    val favoritePrayers: StateFlow<List<PrayerEntity>> = repository.allPrayers.map { list ->
        list.filter { prayer ->
            prayer.isFavorite ||
                    prayer.title.contains("Creio", ignoreCase = true) ||
                    prayer.title.contains("Pai Nosso", ignoreCase = true) ||
                    prayer.title.contains("Ave Maria", ignoreCase = true) ||
                    prayer.title.contains("Glória ao Pai", ignoreCase = true) ||
                    prayer.title.contains("Gloria ao Pai", ignoreCase = true)
        }.sortedBy { it.title.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        val savedCategoriesJson = prefs.getString("custom_categories_list", null)
        if (savedCategoriesJson != null) {
            try {
                val arr = JSONArray(savedCategoriesJson)
                val list = (0 until arr.length()).map { arr.getString(it) }.toMutableList()
                for (cat in defaultCategories) {
                    if (!list.contains(cat)) {
                        list.add(cat)
                    }
                }
                categoriesState.value = list
            } catch (e: Exception) {
                categoriesState.value = defaultCategories
            }
        } else {
            categoriesState.value = defaultCategories
        }

        // Clean installation requirement: clear all prayers for a completely clean install
        val cleanInstallDone = prefs.getBoolean("clean_install_cleared_v1", false)
        viewModelScope.launch(Dispatchers.IO) {
            if (!cleanInstallDone) {
                try {
                    repository.deleteAllPrayers()
                    prefs.edit().putBoolean("clean_install_cleared_v1", true).apply()
                } catch (e: Exception) {
                    // Log or ignore
                }
            } else {
                try {
                    repository.insertSeedPrayers(PrayerSeedData.getInitialPrayers())
                } catch (e: Exception) {
                    // Log or ignore
                }
            }
        }

        syncUrlState.value = prefs.getString("online_sync_url", "https://raw.githubusercontent.com/username/oracao-catholica/main/prayers.json") ?: "https://raw.githubusercontent.com/username/oracao-catholica/main/prayers.json"

        // Initialize Text to Speech for Portuguese audio reading
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("pt", "BR"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady.value = true
                }
            }
        }

        // Auto-sync from online URL on launch if configured
        if (syncUrlState.value.isNotBlank() && syncUrlState.value.startsWith("http")) {
            syncFromOnlineUrl(syncUrlState.value, silent = true)
        }

        // Prefetch & load daily liturgy (30-day offline cache engine)
        refreshLiturgyCache()
    }

    // All prayers list filtered by category and query, sorted alphabetically
    val prayersList: StateFlow<List<PrayerEntity>> = combine(
        repository.allPrayers,
        searchQuery,
        selectedCategory
    ) { prayers, query, category ->
        prayers.filter { prayer ->
            if (query.isNotBlank()) {
                prayer.title.contains(query, ignoreCase = true) ||
                        prayer.content.contains(query, ignoreCase = true) ||
                        prayer.category.contains(query, ignoreCase = true)
            } else if (category.equals("Favoritas", ignoreCase = true) || category.equals("Favoritos", ignoreCase = true)) {
                prayer.isFavorite ||
                        prayer.title.contains("Creio", ignoreCase = true) ||
                        prayer.title.contains("Pai Nosso", ignoreCase = true) ||
                        prayer.title.contains("Ave Maria", ignoreCase = true) ||
                        prayer.title.contains("Glória ao Pai", ignoreCase = true) ||
                        prayer.title.contains("Gloria ao Pai", ignoreCase = true)
            } else if (category.isNotBlank()) {
                prayer.category.equals(category, ignoreCase = true) ||
                        (category.equals("Terços", ignoreCase = true) && prayer.category.equals("Terço", ignoreCase = true)) ||
                        (category.equals("Terço", ignoreCase = true) && prayer.category.equals("Terços", ignoreCase = true)) ||
                        (category.equals("Rosário", ignoreCase = true) && prayer.category.contains("Rosário", ignoreCase = true))
            } else {
                false // Home view renders the vertical Category Buttons column when category is blank and query is blank
            }
        }.sortedBy { it.title.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun cycleTextAlign() {
        val newAlign = when (textAlignState.value) {
            TextAlign.Start -> TextAlign.Center
            TextAlign.Center -> TextAlign.Justify
            else -> TextAlign.Start
        }
        textAlignState.value = newAlign
        val alignName = when (newAlign) {
            TextAlign.Center -> "Center"
            TextAlign.Justify -> "Justify"
            else -> "Start"
        }
        prefs.edit().putString("saved_text_align_name", alignName).apply()
    }

    fun adjustFontSize(delta: Int) {
        val newSize = (fontSizeSp.value + delta).coerceIn(12, 36)
        fontSizeSp.value = newSize
        prefs.edit().putInt("saved_font_size_sp", newSize).apply()
    }

    fun updateCategories(newList: List<String>) {
        categoriesState.value = newList
        val jsonArr = JSONArray(newList)
        prefs.edit().putString("custom_categories_list", jsonArr.toString()).apply()
    }

    fun renameCategory(index: Int, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotBlank() && index in categoriesState.value.indices) {
            val oldName = categoriesState.value[index]
            val list = categoriesState.value.toMutableList()
            list[index] = trimmed
            updateCategories(list)

            viewModelScope.launch(Dispatchers.IO) {
                repository.updateCategoryName(oldName, trimmed)
            }
        }
    }

    fun deleteCategoryDirect(category: String) {
        val list = categoriesState.value.toMutableList()
        list.remove(category)
        updateCategories(list)
    }

    fun saveCategoriesAndSync(newList: List<String>, onResult: ((Boolean, String) -> Unit)? = null) {
        updateCategories(newList)
        syncToFirebaseOnline(onResult)
    }

    fun moveCategory(fromIndex: Int, toIndex: Int) {
        val list = categoriesState.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            updateCategories(list)
        }
    }

    fun deleteCategory(category: String, pin: String): Boolean {
        val savedPin = prefs.getString("admin_pin", "1234") ?: "1234"
        if (pin.isNotBlank() && pin != savedPin) return false
        val list = categoriesState.value.toMutableList()
        list.remove(category)
        updateCategories(list)
        return true
    }

    fun addCategory(newCategoryName: String) {
        val trimmed = newCategoryName.trim()
        if (trimmed.isNotBlank() && !categoriesState.value.contains(trimmed)) {
            val list = categoriesState.value.toMutableList()
            list.add(trimmed)
            updateCategories(list)
        }
    }

    fun checkAdminPin(pin: String): Boolean {
        val savedPin = prefs.getString("admin_pin", "1234") ?: "1234"
        val isCorrect = pin == savedPin
        if (isCorrect) {
            isAdminMode.value = true
        }
        return isCorrect
    }

    fun updateAdminPinWithOldPin(oldPin: String, newPin: String): Boolean {
        val savedPin = prefs.getString("admin_pin", "1234") ?: "1234"
        if (oldPin != savedPin) return false
        prefs.edit().putString("admin_pin", newPin).apply()
        return true
    }

    fun updateAdminPin(newPin: String) {
        prefs.edit().putString("admin_pin", newPin).apply()
    }

    fun exitAdminMode() {
        isAdminMode.value = false
    }

    fun saveSyncUrl(url: String) {
        syncUrlState.value = url.trim()
        prefs.edit().putString("online_sync_url", url.trim()).apply()
    }

    fun selectPrayer(prayer: PrayerEntity?) {
        stopSpeech()
        selectedPrayer.value = prayer
    }

    fun toggleFavorite(prayer: PrayerEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(prayer.id, prayer.isFavorite)
            if (selectedPrayer.value?.id == prayer.id) {
                selectedPrayer.value = selectedPrayer.value?.copy(isFavorite = !prayer.isFavorite)
            }
        }
    }

    fun addOrUpdatePrayer(
        title: String,
        category: String,
        content: String,
        idToEdit: Int = 0,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val prayer = PrayerEntity(
                id = idToEdit,
                title = title.trim(),
                category = category.trim(),
                content = content.trim(),
                isCustom = true
            )
            val generatedId = repository.insert(prayer)
            val finalId = if (idToEdit != 0) idToEdit else generatedId.toInt()
            val savedPrayer = prayer.copy(id = finalId)

            if (selectedPrayer.value?.id == idToEdit || selectedPrayer.value?.id == finalId) {
                selectedPrayer.value = savedPrayer
            }
            // Auto push changes to Firebase online database
            syncToFirebaseOnline(onResult)
        }
    }

    fun deletePrayer(prayer: PrayerEntity, onResult: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            repository.delete(prayer)
            if (selectedPrayer.value?.id == prayer.id) {
                selectedPrayer.value = null
            }
            // Auto push changes to Firebase online database
            syncToFirebaseOnline(onResult)
        }
    }

    fun syncToFirebaseOnline(onResult: ((Boolean, String) -> Unit)? = null) {
        val urlStr = syncUrlState.value.trim()
        if (urlStr.isBlank() || !urlStr.startsWith("http")) {
            onResult?.invoke(true, "Salvo localmente no dispositivo.")
            return
        }

        isSyncing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonPayload = exportPrayersAsJson()
                val targetUrl = if (urlStr.endsWith(".json")) urlStr else "$urlStr/prayers.json"
                val url = URL(targetUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "PUT"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

                connection.outputStream.use { os ->
                    os.write(jsonPayload.toByteArray(Charsets.UTF_8))
                }

                val code = connection.responseCode
                isSyncing.value = false

                withContext(Dispatchers.Main) {
                    if (code in 200..299) {
                        val msg = "Salvo e sincronizado com o Firebase com sucesso!"
                        syncStatusMessage.value = msg
                        onResult?.invoke(true, msg)
                    } else {
                        val msg = "Salvo localmente no dispositivo (Servidor online indisponível - HTTP $code)."
                        syncStatusMessage.value = msg
                        onResult?.invoke(true, msg)
                    }
                }
            } catch (e: Exception) {
                isSyncing.value = false
                withContext(Dispatchers.Main) {
                    val msg = "Salvo localmente no dispositivo."
                    syncStatusMessage.value = msg
                    onResult?.invoke(true, msg)
                }
            }
        }
    }

    fun toggleSpeech(text: String) {
        if (isSpeaking.value) {
            stopSpeech()
        } else {
            if (isTtsReady.value && tts != null) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PrayerTTS")
                isSpeaking.value = true
            }
        }
    }

    fun stopSpeech() {
        if (tts != null && tts?.isSpeaking == true) {
            tts?.stop()
        }
        isSpeaking.value = false
    }

    suspend fun exportPrayersAsJson(): String = withContext(Dispatchers.IO) {
        val prayers = repository.allPrayers.first()
        val jsonArray = JSONArray()
        for (prayer in prayers) {
            val obj = JSONObject().apply {
                put("id", prayer.id)
                put("title", prayer.title)
                put("category", prayer.category)
                put("content", prayer.content)
                put("orderIndex", prayer.orderIndex)
                put("isFavorite", prayer.isFavorite)
                put("isCustom", prayer.isCustom)
            }
            jsonArray.put(obj)
        }
        jsonArray.toString(2)
    }

    fun importPrayersFromJson(jsonString: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val trimmed = jsonString.trim()
                val newPrayers = mutableListOf<PrayerEntity>()

                if (trimmed.startsWith("[")) {
                    val jsonArray = JSONArray(trimmed)
                    for (i in 0 until jsonArray.length()) {
                        if (!jsonArray.isNull(i)) {
                            val obj = jsonArray.optJSONObject(i)
                            if (obj != null) {
                                parsePrayerObject(obj, i)?.let { newPrayers.add(it) }
                            }
                        }
                    }
                } else if (trimmed.startsWith("{")) {
                    val jsonObject = JSONObject(trimmed)
                    val keys = jsonObject.keys()
                    var idx = 0
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val obj = jsonObject.optJSONObject(key)
                        if (obj != null) {
                            parsePrayerObject(obj, idx++)?.let { newPrayers.add(it) }
                        }
                    }
                }

                if (newPrayers.isNotEmpty()) {
                    repository.insertPrayers(newPrayers)
                    withContext(Dispatchers.Main) {
                        onSuccess(newPrayers.size)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Nenhuma oração válida foi encontrada no código JSON.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Erro ao ler JSON: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun parsePrayerObject(obj: JSONObject, fallbackIndex: Int): PrayerEntity? {
        val id = obj.optInt("id", 0)
        val title = obj.optString("title", "")
        val category = obj.optString("category", "Orações")
        val content = obj.optString("content", "")
        val orderIndex = obj.optInt("orderIndex", fallbackIndex + 1)
        val isFavorite = obj.optBoolean("isFavorite", false)
        val isCustom = obj.optBoolean("isCustom", false)

        return if (title.isNotBlank() && content.isNotBlank()) {
            PrayerEntity(
                id = id,
                title = title,
                category = category,
                content = content,
                orderIndex = orderIndex,
                isFavorite = isFavorite,
                isCustom = isCustom
            )
        } else null
    }

    fun syncFromOnlineUrl(urlToFetch: String, silent: Boolean = false) {
        val trimmed = urlToFetch.trim()
        if (trimmed.isBlank()) {
            if (!silent) syncStatusMessage.value = "Informe uma URL válida para sincronização."
            return
        }

        val formattedUrl = if (trimmed.endsWith(".json")) trimmed else "${trimmed.removeSuffix("/")}/prayers.json"

        isSyncing.value = true
        if (!silent) syncStatusMessage.value = "Conectando ao servidor e baixando orações..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(formattedUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                    importPrayersFromJson(
                        jsonString = jsonText,
                        onSuccess = { count ->
                            isSyncing.value = false
                            syncStatusMessage.value = "Sincronização concluída com sucesso! $count orações atualizadas."
                        },
                        onError = { err ->
                            isSyncing.value = false
                            if (!silent) syncStatusMessage.value = err
                        }
                    )
                } else {
                    isSyncing.value = false
                    if (!silent) syncStatusMessage.value = "Erro no servidor (Código ${connection.responseCode})."
                }
            } catch (e: Exception) {
                isSyncing.value = false
                if (!silent) syncStatusMessage.value = "Falha de conexão: Verifique a internet e a URL."
            }
        }
    }

    // Daily Liturgy State
    val liturgiaState = MutableStateFlow(loadSavedLiturgia())

    fun refreshLiturgyCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = liturgyPrefetchManager.loadTodayLiturgyAndPrefetch()
            withContext(Dispatchers.Main) {
                liturgiaState.value = entity.toDailyLiturgyData()
            }
        }
    }

    private fun loadSavedLiturgia(): DailyLiturgyData {
        val defaultData = getTodayLiturgiaData()
        var savedFirst = prefs.getString("liturgia_first_reading", null)
        var savedPsalm = prefs.getString("liturgia_psalm", null)
        var savedSecond = prefs.getString("liturgia_second_reading", null)
        var savedGospel = prefs.getString("liturgia_gospel", null)
        var savedDay = prefs.getString("liturgia_day_of_week", null)
        var savedWeek = prefs.getString("liturgia_week", null)
        var savedSeason = prefs.getString("liturgia_season", null)
        var savedSaint = prefs.getString("liturgia_saint", null)

        val validDays = listOf("DOMINGO", "SEGUNDA-FEIRA", "TERÇA-FEIRA", "QUARTA-FEIRA", "QUINTA-FEIRA", "SEXTA-FEIRA", "SÁBADO")
        if (savedDay == null || !validDays.contains(savedDay.trim().uppercase()) || savedDay.contains("ASSISTI", ignoreCase = true)) {
            savedDay = defaultData.dayOfWeek
        }
        if (savedFirst != null && (savedFirst.length > 50 || savedFirst.contains("ASSISTI", ignoreCase = true))) {
            savedFirst = defaultData.firstReading
        }
        if (savedGospel != null && (savedGospel.length > 50 || savedGospel.contains("ASSISTI", ignoreCase = true))) {
            savedGospel = defaultData.gospel
        }

        val cal = java.util.Calendar.getInstance()
        if (cal.get(java.util.Calendar.MONTH) == java.util.Calendar.AUGUST && cal.get(java.util.Calendar.DAY_OF_MONTH) == 5) {
            savedSaint = "Dedicação da Basílica de Santa Maria Maior"
        } else if (savedSaint != null && savedSaint.contains("João Maria Vianney", ignoreCase = true) && cal.get(java.util.Calendar.DAY_OF_MONTH) != 4) {
            savedSaint = ""
        }

        return DailyLiturgyData(
            firstReading = savedFirst ?: defaultData.firstReading,
            psalm = savedPsalm ?: defaultData.psalm,
            secondReading = savedSecond ?: defaultData.secondReading,
            gospel = savedGospel ?: defaultData.gospel,
            dayOfWeek = savedDay ?: defaultData.dayOfWeek,
            liturgicalWeek = savedWeek ?: defaultData.liturgicalWeek,
            liturgicalSeason = savedSeason ?: defaultData.liturgicalSeason,
            liturgicalColorHex = defaultData.liturgicalColorHex,
            saintOfDay = savedSaint ?: defaultData.saintOfDay
        )
    }

    private fun getTodayLiturgiaData(): DailyLiturgyData {
        val cal = java.util.Calendar.getInstance()
        val dayOfWeekNum = cal.get(java.util.Calendar.DAY_OF_WEEK)
        val dayOfWeekStr = when (dayOfWeekNum) {
            java.util.Calendar.SUNDAY -> "DOMINGO"
            java.util.Calendar.MONDAY -> "SEGUNDA-FEIRA"
            java.util.Calendar.TUESDAY -> "TERÇA-FEIRA"
            java.util.Calendar.WEDNESDAY -> "QUARTA-FEIRA"
            java.util.Calendar.THURSDAY -> "QUINTA-FEIRA"
            java.util.Calendar.FRIDAY -> "SEXTA-FEIRA"
            java.util.Calendar.SATURDAY -> "SÁBADO"
            else -> "QUARTA-FEIRA"
        }

        val isAug5 = (cal.get(java.util.Calendar.MONTH) == java.util.Calendar.AUGUST && cal.get(java.util.Calendar.DAY_OF_MONTH) == 5)
        val saint = if (isAug5) "Dedicação da Basílica de Santa Maria Maior" else ""

        return DailyLiturgyData(
            firstReading = "1ª leitura Jr 31,1-7",
            psalm = "Salmo 23,2-7",
            secondReading = if (dayOfWeekNum == java.util.Calendar.SUNDAY) "2ª leitura 1Cor 12,12-30" else null,
            gospel = "Evangelho Mt 15,21-28",
            dayOfWeek = dayOfWeekStr,
            liturgicalWeek = "18ª Semana",
            liturgicalSeason = "Tempo Comum",
            liturgicalColorHex = "#2E7D32",
            saintOfDay = saint
        )
    }

    fun updateLiturgiaData(newData: DailyLiturgyData) {
        prefs.edit().apply {
            putString("liturgia_first_reading", newData.firstReading)
            putString("liturgia_psalm", newData.psalm)
            putString("liturgia_second_reading", newData.secondReading)
            putString("liturgia_gospel", newData.gospel)
            putString("liturgia_day_of_week", newData.dayOfWeek)
            putString("liturgia_week", newData.liturgicalWeek)
            putString("liturgia_season", newData.liturgicalSeason)
            putString("liturgia_saint", newData.saintOfDay)
            apply()
        }
        liturgiaState.value = newData

        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val entity = LiturgyEntity(
            dateString = todayStr,
            firstReading = newData.firstReading,
            psalm = newData.psalm,
            secondReading = newData.secondReading,
            gospel = newData.gospel,
            dayOfWeek = newData.dayOfWeek,
            liturgicalWeek = newData.liturgicalWeek,
            liturgicalSeason = newData.liturgicalSeason,
            liturgicalColorHex = newData.liturgicalColorHex,
            saintOfDay = newData.saintOfDay
        )

        viewModelScope.launch(Dispatchers.IO) {
            liturgyPrefetchManager.saveOrUpdateLiturgy(entity)
        }
    }

    fun clearAllPrayersForCleanInstall() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllPrayers()
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}

data class DailyLiturgyData(
    val firstReading: String = "1ª leitura Jr 31,1-7",
    val psalm: String = "Salmo 23,2-7",
    val secondReading: String? = null,
    val gospel: String = "Evangelho Mt 15,21-28",
    val dayOfWeek: String = "QUARTA-FEIRA",
    val liturgicalWeek: String = "18ª Semana",
    val liturgicalSeason: String = "Tempo Comum",
    val liturgicalColorHex: String = "#2E7D32",
    val saintOfDay: String = "Dedicação da Basílica de Santa Maria Maior"
)

private fun sanitizeBiblicalCitation(raw: String, defaultCitation: String): String {
    if (raw.isBlank()) return defaultCitation
    val trimmed = raw.trim()
    if (trimmed.length > 50 || trimmed.contains("\n") || trimmed.contains("ASSISTI", ignoreCase = true) || trimmed.contains("CUMULAI", ignoreCase = true)) {
        val regex = Regex("""([1-3]?\s?[A-Za-zÇçÁáÉéÍíÓóÚúâêîôûãõ]+)\s+(\d+[\d\s,.\-—:]*)""")
        val match = regex.find(trimmed)
        if (match != null) {
            val citation = match.value.trim()
            val prefix = when {
                defaultCitation.startsWith("1ª", ignoreCase = true) -> "1ª leitura "
                defaultCitation.startsWith("Salmo", ignoreCase = true) -> "Salmo "
                defaultCitation.startsWith("2ª", ignoreCase = true) -> "2ª leitura "
                defaultCitation.startsWith("Evangelho", ignoreCase = true) -> "Evangelho "
                else -> ""
            }
            return if (citation.startsWith("1ª") || citation.startsWith("Salmo") || citation.startsWith("2ª") || citation.startsWith("Evangelho")) {
                citation
            } else {
                "$prefix$citation"
            }
        }
        return defaultCitation
    }
    return trimmed
}

fun LiturgyEntity.toDailyLiturgyData(): DailyLiturgyData {
    val cleanFirst = sanitizeBiblicalCitation(firstReading, "1ª leitura Jr 31,1-7")
    val cleanPsalm = sanitizeBiblicalCitation(psalm, "Salmo 23,2-7")
    val cleanSecond = secondReading?.let { sanitizeBiblicalCitation(it, "") }?.ifBlank { null }
    val cleanGospel = sanitizeBiblicalCitation(gospel, "Evangelho Mt 15,21-28")

    val validDays = listOf("DOMINGO", "SEGUNDA-FEIRA", "TERÇA-FEIRA", "QUARTA-FEIRA", "QUINTA-FEIRA", "SEXTA-FEIRA", "SÁBADO")
    val cleanDay = if (validDays.contains(dayOfWeek.trim().uppercase()) && !dayOfWeek.contains("ASSISTI", ignoreCase = true)) {
        dayOfWeek.trim().uppercase()
    } else {
        "QUARTA-FEIRA"
    }

    val cal = java.util.Calendar.getInstance()
    val cleanSaint = if ((cal.get(java.util.Calendar.MONTH) == java.util.Calendar.AUGUST && cal.get(java.util.Calendar.DAY_OF_MONTH) == 5) || dateString.endsWith("08-05")) {
        "Dedicação da Basílica de Santa Maria Maior"
    } else if (saintOfDay.contains("João Maria Vianney", ignoreCase = true) && cal.get(java.util.Calendar.DAY_OF_MONTH) != 4) {
        ""
    } else {
        saintOfDay.trim()
    }

    return DailyLiturgyData(
        firstReading = cleanFirst,
        psalm = cleanPsalm,
        secondReading = cleanSecond,
        gospel = cleanGospel,
        dayOfWeek = cleanDay,
        liturgicalWeek = liturgicalWeek.ifBlank { "18ª Semana" },
        liturgicalSeason = liturgicalSeason.ifBlank { "Tempo Comum" },
        liturgicalColorHex = liturgicalColorHex.ifBlank { "#2E7D32" },
        saintOfDay = cleanSaint
    )
}
