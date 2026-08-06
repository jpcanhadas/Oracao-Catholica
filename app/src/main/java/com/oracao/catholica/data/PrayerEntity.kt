package com.oracao.catholica.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayers")
data class PrayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String, // e.g. "Orações", "Novenas", "Terços", "Salmos", "Ladainhas"
    val content: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val orderIndex: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
