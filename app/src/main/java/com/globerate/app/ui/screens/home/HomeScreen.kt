package com.globerate.app.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.globerate.app.utils.RetrofitInstance
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.globerate.app.data.model.CurrencyResponse
import com.globerate.app.ui.screens.home.components.CurrencyDropdown
import com.globerate.app.ui.screens.home.components.WaitingResponseDialog
import com.globerate.app.ui.screens.home.viewmodel.HomeViewModel
import com.globerate.app.ui.screens.home.viewmodel.states.RatesUiState

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {

    val fromCurrency = remember { mutableStateOf("USD") }
    val toCurrency = remember { mutableStateOf("INR") }
    val amount = remember { mutableStateOf("") }
    val result = remember { mutableStateOf("") }

    val date by homeViewModel.date.collectAsState()
    val rates by homeViewModel.rates.collectAsState()
    val currencyList by homeViewModel.currencyList.collectAsState()
    val ratesUiState by homeViewModel.ratesUiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GlobeRate") }, modifier = Modifier
                    .graphicsLayer {
                        shadowElevation = 2.dp.toPx()

                    })
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            when (ratesUiState) {
                is RatesUiState.Loading -> {
                    WaitingResponseDialog(showDialog = true, message = "Processing...")
                }

                is RatesUiState.Success -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CurrencyDropdown(
                            label = "From Currency",
                            currencyList = currencyList,
                            selectedCurrency = fromCurrency,
                            onValueChange = {
                                result.value = homeViewModel.calculateConversion(
                                    amount.value,
                                    fromCurrency.value,
                                    toCurrency.value,
                                    rates
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CurrencyDropdown(
                            label = "To Currency",
                            currencyList = currencyList,
                            selectedCurrency = toCurrency,
                            onValueChange = {
                                result.value = homeViewModel.calculateConversion(
                                    amount.value,
                                    fromCurrency.value,
                                    toCurrency.value,
                                    rates
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Amount input
                    OutlinedTextField(
                        value = amount.value,
                        onValueChange = {
                            amount.value = it
                            result.value = homeViewModel.calculateConversion(
                                it,
                                fromCurrency.value,
                                toCurrency.value,
                                rates
                            )
                        },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    // Result
                    if (result.value.isNotEmpty()) {
                        Text(
                            text = "Converted: ${result.value} ${toCurrency.value}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Optional info
                    Text(
                        text = "ListSize: ${currencyList.size} Date: $date",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                is RatesUiState.Error -> {
                    val message = (ratesUiState as RatesUiState.Error).message
                    Text(
                        text = "Error: $message",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


                else -> {}

            }


        }
    }
}
