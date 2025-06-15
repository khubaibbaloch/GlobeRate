package com.globerate.app.data.remote

import com.globerate.app.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("rates/latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String
    ): Response<CurrencyResponse>
}
