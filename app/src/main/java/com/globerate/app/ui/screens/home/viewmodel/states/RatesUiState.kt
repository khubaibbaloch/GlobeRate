package com.globerate.app.ui.screens.home.viewmodel.states


sealed class RatesUiState {
    object Success : RatesUiState()
    data class Error(val message: String) : RatesUiState()
    object Loading : RatesUiState()
    object Idle : RatesUiState()
}

