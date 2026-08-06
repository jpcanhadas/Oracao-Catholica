package com.oracao.catholica.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PrayerEntity::class, LiturgyEntity::class], version = 2, exportSchema = false)
abstract class PrayerDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
    abstract fun liturgyDao(): LiturgyDao

    companion object {
        @Volatile
        private var INSTANCE: PrayerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PrayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrayerDatabase::class.java,
                    "catholic_prayers_db"
                )
                    .addCallback(PrayerDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PrayerDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.prayerDao()?.let { dao ->
                        dao.insertSeedPrayers(PrayerSeedData.getInitialPrayers())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.prayerDao()?.let { dao ->
                        dao.insertSeedPrayers(PrayerSeedData.getInitialPrayers())
                    }
                }
            }
        }
    }
}
