package com.oracao.catholica.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liturgy_cache")
data class LiturgyEntity(
    @PrimaryKey
    val dateString: String, // Format "yyyy-MM-dd"
    val firstReading: String,
    val psalm: String,
    val secondReading: String?,
    val gospel: String,
    val dayOfWeek: String,
    val liturgicalWeek: String,
    val liturgicalSeason: String,
    val liturgicalColorHex: String = "#2E7D32",
    val saintOfDay: String = "São João Maria Vianney",
    val updatedAt: Long = System.currentTimeMillis()
)
