package com.multibank.real_timepricetracker

import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.data.repository.PriceRepository
import com.multibank.real_timepricetracker.data.websocket.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePriceRepository(
    initialStocks: List<StockItem> = emptyList()
) : PriceRepository {

    private val _stockItems = MutableStateFlow(initialStocks)
    override val stockItems: StateFlow<List<StockItem>> = _stockItems

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    var feedStarted = false
    var feedStopped = false

    override fun startFeed() { feedStarted = true }
    override fun stopFeed()  { feedStopped = true }

    fun emitStocks(stocks: List<StockItem>) { _stockItems.value = stocks }
    fun emitConnectionState(state: ConnectionState) { _connectionState.value = state }
}
