package com.multibank.real_timepricetracker

import androidx.lifecycle.SavedStateHandle
import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.ui.details.DetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val symbol = "AAPL"
    private val fakeStocks = listOf(
        StockItem("AAPL", 175.00),
        StockItem("NVDA", 875.00)
    )

    private lateinit var fakeRepo: FakePriceRepository
    private lateinit var viewModel: DetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fakeRepo = FakePriceRepository(fakeStocks)
        viewModel = DetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("symbol" to symbol)),
            repository = fakeRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects the correct symbol and description`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        assertEquals(symbol, viewModel.symbol)
        assertEquals(fakeStocks.first().price, viewModel.uiState.value.stock?.price)
        collectJob.cancel()
    }

    @Test
    fun `uiState updates when repository emits new price for current symbol`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()

        val updatedStocks = listOf(
            StockItem("AAPL", 180.00, change = PriceChange.UP),
            StockItem("NVDA", 875.00)
        )
        fakeRepo.emitStocks(updatedStocks)
        advanceUntilIdle()

        assertEquals(180.00, viewModel.uiState.value.stock?.price)
        assertEquals(PriceChange.UP, viewModel.uiState.value.stock?.change)
        collectJob.cancel()
    }
}
