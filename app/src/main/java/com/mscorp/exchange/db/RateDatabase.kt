package com.mscorp.exchange.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mscorp.exchange.api.RatesOverview


@Database(entities = [RatesOverview::class], version = 1, exportSchema = false)
@TypeConverters(MapTypeConverter::class)
abstract class RateDatabase : RoomDatabase() {

    abstract fun userDao(): RatesDao

    companion object {
        private var INSTANCE: RateDatabase? = null

        fun getInstance(context: Context): RateDatabase? {
            if (INSTANCE == null) {
                synchronized(RateDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RateDatabase::class.java, "rates.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }

}
