package com.globerate.app.ui.screens.home.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globerate.app.BuildConfig
import com.globerate.app.ui.screens.home.viewmodel.states.RatesUiState
import com.globerate.app.utils.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    private val apiKey = BuildConfig.API_KEY
    private val apiService = RetrofitInstance.api

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
                Log.d("ApiResponse", "fetchRates: $e")
                _ratesUiState.value = RatesUiState.Error("Unable to fetch the data")
            }
        }
    }



@SuppressLint("DefaultLocale")
fun calculateConversion(
    amountInput: String,
    fromCurrency: String,
    toCurrency: String,
    rates: Map<String, String>,
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