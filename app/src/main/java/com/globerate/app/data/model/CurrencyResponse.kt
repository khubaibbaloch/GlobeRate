package com.globerate.app.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("base") val base: String,
    @SerializedName("date") val date: String,
    @SerializedName("rates") val rates: Map<String, String>
)
