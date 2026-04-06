package com.multibank.real_timepricetracker

import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.model.StockItem
import org.junit.Assert.assertEquals
import org.junit.Test

class StockItemTest {

    @Test
    fun `default StockItem has NEUTRAL change and zero previousPrice`() {
        val item = StockItem(symbol = "AAPL", price = 175.00)
        assertEquals(PriceChange.NEUTRAL, item.change)
        assertEquals(0.0, item.previousPrice, 0.0)
    }

    @Test
    fun `copy preserves all fields correctly`() {
        val original = StockItem("GOOG", 140.00)
        val updated = original.copy(
            price = 145.00,
            previousPrice = 140.00,
            change = PriceChange.UP
        )
        assertEquals("GOOG", updated.symbol)
        assertEquals(145.00, updated.price, 0.001)
        assertEquals(140.00, updated.previousPrice, 0.001)
        assertEquals(PriceChange.UP, updated.change)
    }

    @Test
    fun `two StockItems with same data are equal`() {
        val a = StockItem("TSLA", 245.00, 230.00, PriceChange.UP)
        val b = StockItem("TSLA", 245.00, 230.00, PriceChange.UP)
        assertEquals(a, b)
    }

    @Test
    fun `sorted list orders by price descending`() {
        val items = listOf(
            StockItem("INTC", 35.00),
            StockItem("AVGO", 1450.00),
            StockItem("NVDA", 875.00)
        )
        val sorted = items.sortedByDescending { it.price }
        assertEquals("AVGO", sorted[0].symbol)
        assertEquals("NVDA", sorted[1].symbol)
        assertEquals("INTC", sorted[2].symbol)
    }
}
