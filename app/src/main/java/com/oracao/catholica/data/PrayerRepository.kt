package com.oracao.catholica.data

import kotlinx.coroutines.flow.Flow

class PrayerRepository(private val prayerDao: PrayerDao) {
    val allPrayers: Flow<List<PrayerEntity>> = prayerDao.getAllPrayers()
    val favoritePrayers: Flow<List<PrayerEntity>> = prayerDao.getFavoritePrayers()

    fun getPrayersByCategory(category: String): Flow<List<PrayerEntity>> {
        return prayerDao.getPrayersByCategory(category)
    }

    suspend fun getPrayerById(id: Int): PrayerEntity? {
        return prayerDao.getPrayerById(id)
    }

    suspend fun insert(prayer: PrayerEntity): Long {
        return prayerDao.insertPrayer(prayer)
    }

    suspend fun insertPrayers(prayers: List<PrayerEntity>) {
        prayerDao.insertPrayers(prayers)
    }

    suspend fun insertSeedPrayers(prayers: List<PrayerEntity>) {
        prayerDao.insertSeedPrayers(prayers)
    }

    suspend fun update(prayer: PrayerEntity) {
        prayerDao.updatePrayer(prayer)
    }

    suspend fun toggleFavorite(id: Int, currentFavorite: Boolean) {
        prayerDao.updateFavoriteStatus(id, !currentFavorite)
    }

    suspend fun updateCategoryName(oldCategory: String, newCategory: String) {
        prayerDao.updateCategoryName(oldCategory, newCategory)
    }

    suspend fun delete(prayer: PrayerEntity) {
        prayerDao.deletePrayer(prayer)
    }

    suspend fun getPrayerCount(): Int {
        return prayerDao.getPrayerCount()
    }

    suspend fun deleteAllPrayers() {
        prayerDao.deleteAllPrayers()
    }
}
