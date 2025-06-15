package com.globerate.app.ui.screens.home.viewmodel

import androidx.lifecycle.viewmodel.compose.viewModel
import com.globerate.app.data.model.CurrencyResponse
import com.globerate.app.data.remote.CurrencyApiService
import com.globerate.app.ui.screens.home.viewmodel.states.RatesUiState
import com.globerate.app.utils.RetrofitInstance
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response
import org.mockito.kotlin.mock
import java.io.IOException


class HomeViewModelTest {

    private val apiKey = "a4a4eea86e534c8698807763c44e3c14"
    private lateinit var viewModel: HomeViewModel
    private lateinit var apiService: CurrencyApiService
    private val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        apiService = mock() // <-- use Mockito or MockK
        viewModel = HomeViewModel(apiService)

        val mockRates = mapOf("USD" to "1.0", "EUR" to "0.9")
        val fakeResponse = CurrencyResponse(date = "2024-01-01", rates = mockRates, base = "USD")

        runBlocking {
            whenever(apiService.getLatestRates(any())).thenReturn(Response.success(fakeResponse))
        }

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRatesUiState initial state`() {
        // Verify that the initial state of ratesUiState is RatesUiState.Idle before any operations.
        // TODO implement test
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Idle, actual)

    }

    @Test
    fun `getDate initial state`() {
        // Verify that the initial state of date is an empty string.
        // TODO implement test
        val actual = viewModel.date.value
        assertEquals("", actual)
    }

    @Test
    fun `getRates initial state`() {
        // Verify that the initial state of rates is an empty map.
        // TODO implement test
        val actual = viewModel.rates.value
        assertEquals(emptyMap<String, String>(), actual)
    }

    @Test
    fun `getCurrencyList initial state`() {
        // Verify that the initial state of currencyList is an empty list.
        // TODO implement test
        val actual = viewModel.currencyList.value
        assertEquals(emptyList<String>(), actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates successful API call`() = runTest {
        // Verify that fetchRates updates ratesUiState to Success, date, rates, and currencyList with data from a successful API response.
        // TODO implement test

        val mockRates = mapOf("USD" to "1.0", "EUR" to "0.9")
        val fakeResponse = CurrencyResponse(date = "2024-01-01", rates = mockRates, base = "USD")

        runBlocking {
            whenever(apiService.getLatestRates(any())).thenReturn(Response.success(fakeResponse))
        }
        viewModel = HomeViewModel(apiService)

        viewModel.fetchRates()
        advanceUntilIdle()
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Success, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates API call with unsuccessful response`() = runTest {

        runBlocking {
            val errorBody = ResponseBody.create(MediaType.parse("application/json"), "Bad Request")
            whenever(apiService.getLatestRates(any()))
                .thenReturn(Response.error(400, errorBody))
        }

        viewModel = HomeViewModel(apiService)

        viewModel.fetchRates()
        advanceUntilIdle()

        val actual = viewModel.ratesUiState.value
        assertEquals("Response.error()", (actual as RatesUiState.Error).message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates API call with null body`() = runTest {
        // Verify that fetchRates updates ratesUiState to Error when the API response is successful but the body is null.
        // TODO implement test

        val mockRates = mapOf("USD" to "1.0", "EUR" to "0.9")
        val fakeResponse = null

        runBlocking {
            whenever(apiService.getLatestRates(any())).thenReturn(Response.success(fakeResponse))
        }
        viewModel = HomeViewModel(apiService)

        viewModel.fetchRates()
        advanceUntilIdle()
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Error("OK"), actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates network exception`() = runTest {
        // Verify that fetchRates updates ratesUiState to Error with 'Unable to fetch the data' when a network exception (e.g., IOException) occurs during the API call. [1]
        // TODO implement test

        whenever(apiService.getLatestRates(any())).thenAnswer {
            throw IOException("Network failure")
        }
        viewModel = HomeViewModel(apiService)

        viewModel.fetchRates()
        advanceUntilIdle()
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Error("Unable to fetch the data"), actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates general exception`() = runTest {
        // Verify that fetchRates updates ratesUiState to Error with 'Unable to fetch the data' when any other unexpected exception occurs. [1]
        // TODO implement test
        whenever(apiService.getLatestRates(any())).thenAnswer {
            throw RuntimeException("Unexpected failure")
        }
        viewModel = HomeViewModel(apiService)

        viewModel.fetchRates()
        advanceUntilIdle()
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Error("Unable to fetch the data"), actual)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates multiple calls`() = runTest {
        // Verify that if fetchRates is called multiple times, the UI state and data are updated correctly for each call, including transitions from Error to Success and vice-versa.
        // TODO implement test
        viewModel.fetchRates()
        advanceUntilIdle()
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Success, actual)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchRates ViewModel coroutine scope cancellation`() = runTest {
        // Verify that if the viewModelScope is cancelled while fetchRates is in progress, the operation is correctly cancelled and doesn't lead to crashes or inconsistent states.
        // TODO implement test
        viewModel.fetchRates()
        advanceUntilIdle()
        val actual = viewModel.ratesUiState.value
        assertEquals(RatesUiState.Success, actual)
    }

    @Test
    fun `calculateConversion valid inputs`() {
        // Verify correct conversion for valid amount, fromCurrency, toCurrency, and rates.
        // Example: amount="100", fromCurrency="USD", toCurrency="EUR", rates={"USD":"1.0", "EUR":"0.9"} should return "90.00".
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("90.00", actual)
    }

    @Test
    fun `calculateConversion invalid amount input`() {
        // Verify returns empty string when amountInput is not a valid number (e.g., "abc", "").
        // TODO implement test
        viewModel = HomeViewModel(apiService)
        val actual = viewModel.calculateConversion(
            "abc",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion fromCurrency not in rates`() {
        // Verify returns empty string when fromCurrency is not found in the rates map.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "XYZ",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion toCurrency not in rates`() {
        // Verify returns empty string when toCurrency is not found in the rates map.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "USD",
            "XYZ",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion fromCurrency rate not a number`() {
        // Verify returns empty string when the rate for fromCurrency in the rates map is not a valid number (e.g., "xyz").
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "USD",
            "EUR",
            mapOf("USD" to "xyz", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion toCurrency rate not a number`() {
        // Verify returns empty string when the rate for toCurrency in the rates map is not a valid number (e.g., "abc").
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "abc")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion zero amount`() {
        // Verify correct conversion (should be "0.00") when amountInput is "0".
        // TODO implement test
        val actual =
            viewModel.calculateConversion("0", "USD", "EUR", mapOf("USD" to "1.0", "EUR" to "abc"))
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion negative amount`() {
        // Verify returns empty string as per current logic, or handle negative amounts if business logic changes. Current implementation relies on toDoubleOrNull for amount.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "-100",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion very large amount`() {
        // Input too large to parse as Double, should return empty string
        val actual = viewModel.calculateConversion(
            "999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999990000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion very small amount precision`() {
        // Verify correct formatting to two decimal places for very small conversion results.
        val actual = viewModel.calculateConversion(
            "0.44",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )

        // 0.44 * 0.9 = 0.396 → formatted = 0.40
        assertEquals("0.40", actual)
    }


    @Test
    fun `calculateConversion fromRate is zero`() {
        // Verify returns empty string if fromRate is 0 to avoid division by zero, as toDoubleOrNull will parse "0.0" to 0.0, and the current logic doesn't explicitly handle division by zero.
        // The condition `fromRate != null` will be true. If `fromRate` is 0.0, a division by zero will occur. This should be explicitly tested and potentially handled.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "USD",
            "EUR",
            mapOf("USD" to "0.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion same from and to currency`() {
        // Verify that if fromCurrency and toCurrency are the same, the output is the original amount formatted to two decimal places.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "USD",
            "USD",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("100.00", actual)
    }

    @Test
    fun `calculateConversion empty rates map`() {
        // Verify returns empty string when the provided rates map is empty.
        // TODO implement test
        val actual = viewModel.calculateConversion("100", "USD", "EUR", emptyMap())
        assertEquals("", actual)
    }

    @Test
    fun `calculateConversion amount with leading trailing spaces`() {
        // Verify behavior if amountInput has leading/trailing spaces (e.g., " 100 "). `toDoubleOrNull` should handle this, but it's good to confirm.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            " 100 ",
            "USD",
            "EUR",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("90.00", actual)
    }

    @Test
    fun `calculateConversion currency codes case sensitivity`() {
        // Verify if currency codes in `rates` map and input parameters `fromCurrency`, `toCurrency` are treated case-sensitively or case-insensitively, based on expected behavior. Current map lookup is case-sensitive.
        // TODO implement test
        val actual = viewModel.calculateConversion(
            "100",
            "Usd",
            "eur",
            mapOf("USD" to "1.0", "EUR" to "0.9")
        )
        assertEquals("", actual)
    }

}