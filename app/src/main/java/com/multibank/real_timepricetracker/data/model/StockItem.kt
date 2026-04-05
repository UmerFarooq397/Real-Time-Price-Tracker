package com.multibank.real_timepricetracker.data.model

data class StockItem(
    val symbol: String,
    val price: Double,
    val previousPrice: Double = 0.0,
    val change: PriceChange = PriceChange.NEUTRAL
)

enum class PriceChange { UP, DOWN, NEUTRAL }
