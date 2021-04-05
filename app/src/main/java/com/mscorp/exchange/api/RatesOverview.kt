package com.mscorp.exchange.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mscorp.exchange.db.MapTypeConverter

@Entity(tableName = "rates")
data class RatesOverview(
	@PrimaryKey
	val id: Int,

	@TypeConverters(MapTypeConverter::class)
	val rates: HashMap<String, Double>?
)