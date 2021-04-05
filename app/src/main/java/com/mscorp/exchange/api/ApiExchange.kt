package com.mscorp.exchange.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiExchange {
    @GET("/v6/latest")
    suspend fun getRates(): RatesOverview
}