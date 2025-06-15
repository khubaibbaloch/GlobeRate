package com.globerate.app.utils

import com.globerate.app.data.remote.CurrencyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.currencyfreaks.com/v2.0/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: CurrencyApiService by lazy {
        retrofit.create(CurrencyApiService::class.java)
    }
}
