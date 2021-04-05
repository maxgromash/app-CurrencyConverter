package com.mscorp.exchange

import android.content.Context
import androidx.lifecycle.LiveData
import com.mscorp.exchange.api.ApiService
import com.mscorp.exchange.api.RatesOverview
import com.mscorp.exchange.api.Response
import com.mscorp.exchange.db.RateDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainRepository(context: Context) {

    private val api = ApiService.getApi()
    private val db = RateDatabase.getInstance(context)

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Response<T> {
        //Переходим корутину в параллельный поток
        return withContext(Dispatchers.IO) {
            try {
                Response.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Response.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Response.Failure(true, null, null)
                    }
                }
            }
        }
    }

    fun getLiveDAtat(): LiveData<List<RatesOverview>>? {
        return db?.userDao()?.getAllRates()
    }

    suspend fun getData(): LiveData<List<RatesOverview>>? {
        try {
            val res = safeApiCall {
                api.getRates()
            }

            if (res is Response.Success) {
                db?.userDao()?.insert(res.value)
            }

            return db?.userDao()?.getAllRates()
        } catch (ex: Exception) { }
        return null
    }
}

