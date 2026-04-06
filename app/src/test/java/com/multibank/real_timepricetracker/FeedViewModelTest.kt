package com.multibank.real_timepricetracker

import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.data.websocket.ConnectionState
import com.multibank.real_timepricetracker.ui.feed.FeedViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val fakeStocks = listOf(
        StockItem("NVDA", 875.00, change = PriceChange.UP),
        StockItem("TSLA", 245.00, change = PriceChange.DOWN),
        StockItem("AAPL", 175.00)
    )

    private lateinit var fakeRepo: FakePriceRepository
    private lateinit var viewModel: FeedViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fakeRepo = FakePriceRepository(fakeStocks)
        viewModel = FeedViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has feed inactive and disconnected`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse("Feed should not be active initially", state.isFeedActive)
        assertEquals(ConnectionState.DISCONNECTED, state.connectionState)
        collectJob.cancel()
    }

    @Test
    fun `stocks are sorted by price descending`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        val prices = viewModel.uiState.value.stocks.map { it.price }
        assertEquals(prices.sortedDescending(), prices)
        collectJob.cancel()
    }

    @Test
    fun `toggleFeed starts feed and flips isFeedActive to true`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        viewModel.toggleFeed()
        advanceUntilIdle()
        
        assertTrue("Feed should be active after toggle", viewModel.uiState.value.isFeedActive)
        assertTrue("Repository should have received startFeed call", fakeRepo.feedStarted)
        collectJob.cancel()
    }

    @Test
    fun `toggleFeed twice stops feed and flips isFeedActive back to false`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        viewModel.toggleFeed()
        advanceUntilIdle()
        viewModel.toggleFeed()
        advanceUntilIdle()
        
        assertFalse("Feed should be inactive after double toggle", viewModel.uiState.value.isFeedActive)
        assertTrue("Repository should have received stopFeed call", fakeRepo.feedStopped)
        collectJob.cancel()
    }

    @Test
    fun `uiState reflects updated stock prices from repository`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        val updatedStocks = listOf(
            StockItem("NVDA", 900.00, change = PriceChange.UP),
            StockItem("AAPL", 200.00, change = PriceChange.UP),
            StockItem("TSLA", 230.00, change = PriceChange.DOWN)
        )
        fakeRepo.emitStocks(updatedStocks)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(900.00, state.stocks.first().price, 0.001)
        assertEquals("NVDA", state.stocks.first().symbol)
        collectJob.cancel()
    }

    @Test
    fun `uiState reflects connection state changes`() = runTest {
        val collectJob = launch(dispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()
        
        fakeRepo.emitConnectionState(ConnectionState.CONNECTED)
        advanceUntilIdle()
        assertEquals(ConnectionState.CONNECTED, viewModel.uiState.value.connectionState)

        fakeRepo.emitConnectionState(ConnectionState.DISCONNECTED)
        advanceUntilIdle()
        assertEquals(ConnectionState.DISCONNECTED, viewModel.uiState.value.connectionState)
        collectJob.cancel()
    }
}
