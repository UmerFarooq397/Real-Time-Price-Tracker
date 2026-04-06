package com.multibank.real_timepricetracker.ui.feed

import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.data.websocket.ConnectionState

data class FeedUiState(
    val stocks: List<StockItem> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val isFeedActive: Boolean = false
)
