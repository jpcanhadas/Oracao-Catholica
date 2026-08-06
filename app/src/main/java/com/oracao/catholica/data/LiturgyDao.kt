package com.oracao.catholica.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LiturgyDao {
    @Query("SELECT * FROM liturgy_cache WHERE dateString = :date LIMIT 1")
    suspend fun getLiturgyForDate(date: String): LiturgyEntity?

    @Query("SELECT * FROM liturgy_cache WHERE dateString >= :startDate ORDER BY dateString ASC")
    suspend fun getFutureLiturgies(startDate: String): List<LiturgyEntity>

    @Query("SELECT COUNT(*) FROM liturgy_cache WHERE dateString >= :todayDate")
    suspend fun countFutureLiturgies(todayDate: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiturgies(liturgies: List<LiturgyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiturgy(liturgy: LiturgyEntity)

    @Query("DELETE FROM liturgy_cache WHERE dateString < :todayDate")
    suspend fun deletePastLiturgies(todayDate: String)
}
