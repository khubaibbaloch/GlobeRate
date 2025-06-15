package com.globerate.app.ui.screens.home.viewmodel.states

//sealed class RatesUiState<out T> {
//    data class Success<T>(val data: T) : RatesUiState<T>()
//    data class Error(val message: String) : RatesUiState<Nothing>()
//    object Idle : RatesUiState<Nothing>()
//    object Loading : RatesUiState<Nothing>()
//}
//


sealed class RatesUiState {
    object Success : RatesUiState()
    data class Error(val message: String) : RatesUiState()
    object Loading : RatesUiState()
    object Idle : RatesUiState()
}

