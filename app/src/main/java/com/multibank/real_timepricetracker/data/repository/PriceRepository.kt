package com.multibank.real_timepricetracker.data.repository

import com.multibank.real_timepricetracker.data.websocket.ConnectionState
import com.multibank.real_timepricetracker.data.model.StockItem
import kotlinx.coroutines.flow.StateFlow

interface PriceRepository {
    val stockItems: StateFlow<List<StockItem>>
    val connectionState: StateFlow<ConnectionState>
    fun startFeed()
    fun stopFeed()
}
