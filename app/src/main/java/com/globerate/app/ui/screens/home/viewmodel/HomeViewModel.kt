package com.globerate.app.ui.screens.home.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globerate.app.data.model.CurrencyResponse
import com.globerate.app.data.remote.CurrencyApiService
import com.globerate.app.ui.screens.home.viewmodel.states.RatesUiState
import com.globerate.app.utils.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.text.uppercase

class HomeViewModel(private var apiService: CurrencyApiService) : ViewModel() {

    private val apiKey = "a4a4eea86e534c8698807763c44e3c14"
    // private val apiService = RetrofitInstance.api

    private val _ratesUiState = MutableStateFlow<RatesUiState>(RatesUiState.Idle)
    val ratesUiState: StateFlow<RatesUiState> = _ratesUiState

    private val _date = MutableStateFlow<String>("")
    val date: StateFlow<String> = _date

    private val _rates = MutableStateFlow<Map<String, String>>(emptyMap())
    val rates: StateFlow<Map<String, String>> = _rates

    private val _currencyList = MutableStateFlow<List<String>>(emptyList())
    val currencyList: StateFlow<List<String>> = _currencyList

    init {
        fetchRates()
    }


    fun fetchRates() {
        viewModelScope.launch {
            _ratesUiState.value = RatesUiState.Loading
            try {
                val response = apiService.getLatestRates(apiKey)
                if (response.isSuccessful && response.body() != null) {
                    val values = response.body()!!
                    _date.value = values.date
                    _rates.value = values.rates
                    _currencyList.value = values.rates.keys.toList()

                    _ratesUiState.value = RatesUiState.Success
                } else {
                    _ratesUiState.value = RatesUiState.Error(message = response.message())
                }
            } catch (e: Exception) {
                _ratesUiState.value = RatesUiState.Error("Unable to fetch the data")
            }
        }
    }

//    @SuppressLint("DefaultLocale")
//    fun calculateConversion(
//        amountInput: String,
//        fromCurrency: String,
//        toCurrency: String,
//        rates: Map<String, String>,
//    ): String {
//        val fromRate = rates[fromCurrency]?.toDoubleOrNull()
//        val toRate = rates[toCurrency]?.toDoubleOrNull()
//        val amt = amountInput.toDoubleOrNull()
//
//        return if (fromRate != null && toRate != null && amt != null) {
//            val converted = (toRate / fromRate) * amt
//            String.format("%.2f", converted)
//        } else {
//            ""
//        }
//    }
//@SuppressLint("DefaultLocale")
//fun calculateConversion(
//    amountInput: String,
//    fromCurrency: String,
//    toCurrency: String,
//    rates: Map<String, String>
//): String {
//    val amount = amountInput.toDoubleOrNull()
//    if (amount == null || amount < 0) return ""
//
//    val fromRate = rates[fromCurrency]?.toDoubleOrNull() ?: return ""
//    val toRate = rates[toCurrency]?.toDoubleOrNull() ?: return ""
//
//    val baseAmount = amount / fromRate
//    val result = baseAmount * toRate
//    return String.format("%.2f", result)
//}
//    @SuppressLint("DefaultLocale")
//    fun calculateConversion(
//        amountInput: String,
//        fromCurrency: String,
//        toCurrency: String,
//        rates: Map<String, String>
//    ): String {
//        val amount = amountInput.toDoubleOrNull()
//        val fromRate = rates[fromCurrency]?.toDoubleOrNull()
//        val toRate = rates[toCurrency]?.toDoubleOrNull()
//
//        if (
//            amount == null || amount < 0 || fromRate == null || toRate == null ||
//            amount.isInfinite() || fromRate.isInfinite() || toRate.isInfinite()
//        ) return ""
//
//        val baseAmount = amount / fromRate
//        val result = baseAmount * toRate
//        return String.format("%.2f", result)
//    }

    @SuppressLint("DefaultLocale")
    fun calculateConversion(
        amountInput: String,
        fromCurrency: String,
        toCurrency: String,
        rates: Map<String, String>
    ): String {
        val amount = amountInput.toDoubleOrNull()
        val fromRate = rates[fromCurrency]?.toDoubleOrNull()
        val toRate = rates[toCurrency]?.toDoubleOrNull()

        if (
            amount == null || amount < 0 ||
            fromRate == null || fromRate == 0.0 ||
            toRate == null || toRate == 0.0 ||
            amount.isInfinite() || fromRate.isInfinite() || toRate.isInfinite() ||
            amount.isNaN() || fromRate.isNaN() || toRate.isNaN()
        ) return ""

        val baseAmount = amount / fromRate
        val result = baseAmount * toRate
        return String.format("%.2f", result)
    }


}