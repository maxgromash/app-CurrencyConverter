package com.mscorp.exchange.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MapTypeConverter {

    @TypeConverter
    @JvmStatic
    fun stringToMap(value: String): HashMap<String, Double> {
        return Gson().fromJson(value, object : TypeToken<HashMap<String, Double>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun mapToString(value: HashMap<String, Double>?): String {
        return if (value == null) "" else Gson().toJson(value)
    }
}
