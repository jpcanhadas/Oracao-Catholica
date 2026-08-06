package com.oracao.catholica.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayers ORDER BY orderIndex ASC, title ASC")
    fun getAllPrayers(): Flow<List<PrayerEntity>>

    @Query("SELECT * FROM prayers WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoritePrayers(): Flow<List<PrayerEntity>>

    @Query("SELECT * FROM prayers WHERE category = :category ORDER BY orderIndex ASC, title ASC")
    fun getPrayersByCategory(category: String): Flow<List<PrayerEntity>>

    @Query("SELECT * FROM prayers WHERE id = :id")
    suspend fun getPrayerById(id: Int): PrayerEntity?

    @Query("SELECT COUNT(*) FROM prayers")
    suspend fun getPrayerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayer(prayer: PrayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayers(prayers: List<PrayerEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeedPrayers(prayers: List<PrayerEntity>)

    @Update
    suspend fun updatePrayer(prayer: PrayerEntity)

    @Query("UPDATE prayers SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("UPDATE prayers SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateCategoryName(oldCategory: String, newCategory: String)

    @Delete
    suspend fun deletePrayer(prayer: PrayerEntity)

    @Query("DELETE FROM prayers")
    suspend fun deleteAllPrayers()
}
