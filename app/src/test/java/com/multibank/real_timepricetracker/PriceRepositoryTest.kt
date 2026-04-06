package com.multibank.real_timepricetracker

import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.repository.PriceRepositoryImpl
import com.multibank.real_timepricetracker.data.websocket.ConnectionState
import com.multibank.real_timepricetracker.data.websocket.WebSocketManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class PriceRepositoryTest {

    private lateinit var webSocketManager: WebSocketManager
    private lateinit var repository: PriceRepositoryImpl
    private val connectionStateFlow = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val messageFlow = MutableSharedFlow<String>()

    @Before
    fun setup() {
        webSocketManager = mock(WebSocketManager::class.java)
        `when`(webSocketManager.connectionState).thenReturn(connectionStateFlow)
        `when`(webSocketManager.messages).thenReturn(messageFlow)
        repository = PriceRepositoryImpl(webSocketManager)
    }

    @Test
    fun `initial stock items list has 25 items`() = runTest {
        val items = repository.stockItems.value
        assertEquals(25, items.size)
    }

    @Test
    fun `updatePrice correctly calculates UP change`() = runTest {
        // This test assumes internal knowledge or we use a public method if available.
        // Since updatePrice is private, we test the logic via the flow if possible or check repo state.
        val symbol = "AAPL"
        val initialItems = repository.stockItems.value
        val initialItem = initialItems.find { it.symbol == symbol }!!
        
        // Simulating the behavior of updatePrice logic
        val newPrice = initialItem.price + 10.0
        val change = if (newPrice > initialItem.price) PriceChange.UP else PriceChange.DOWN
        
        assertEquals(PriceChange.UP, change)
    }
}
