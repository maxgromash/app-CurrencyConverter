package com.mscorp.exchange.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mscorp.exchange.api.RatesOverview

@Dao
interface RatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: RatesOverview)

    @Query("SELECT * from rates")
    fun getAllRates(): LiveData<List<RatesOverview>>

    @Query("DELETE FROM rates")
    suspend fun delete()
}