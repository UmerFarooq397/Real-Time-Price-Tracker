package com.multibank.real_timepricetracker.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.data.websocket.ConnectionState
import com.multibank.real_timepricetracker.data.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.random.Random

private data class PriceMessage(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("price") val price: Double
)

class PriceRepositoryImpl(
    private val webSocketManager: WebSocketManager = WebSocketManager()
) : PriceRepository {

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var sendJob: Job? = null
    private var collectJob: Job? = null

    override val connectionState: StateFlow<ConnectionState> = webSocketManager.connectionState

    private val symbols = listOf(
        "AAPL", "GOOG", "TSLA", "AMZN", "MSFT",
        "NVDA", "META", "NFLX", "AMD", "INTC",
        "PYPL", "ADBE", "CRM", "ORCL", "IBM",
        "QCOM", "TXN", "AVGO", "MU", "NOW",
        "SNOW", "UBER", "LYFT", "SPOT", "COIN"
    )

    private val initialPrices = symbols.associateWith { 0.0 }

    private val _stockItems = MutableStateFlow(
        symbols.map { symbol ->
            StockItem(symbol = symbol, price = initialPrices[symbol] ?: 0.0)
        }
    )
    override val stockItems: StateFlow<List<StockItem>> = _stockItems.asStateFlow()

    override fun startFeed() {
        webSocketManager.connect()
        startCollecting()
        startSending()
    }

    override fun stopFeed() {
        sendJob?.cancel()
        collectJob?.cancel()
        webSocketManager.disconnect()
    }

    private fun startCollecting() {
        collectJob?.cancel()
        collectJob = scope.launch {
            webSocketManager.messages.collect { json ->
                runCatching {
                    val message = gson.fromJson(json, PriceMessage::class.java)
                    if (message?.symbol != null) updatePrice(message.symbol, message.price)
                }
            }
        }
    }

    private fun startSending() {
        sendJob?.cancel()
        sendJob = scope.launch {
            while (true) {
                delay(2_000)
                val currentItems = _stockItems.value
                // Pre-compute a new price for every symbol in this tick
                val newPrices = currentItems.associate { it.symbol to randomPrice(it.price) }

                if (connectionState.value == ConnectionState.CONNECTED) {
                    // WebSocket path: send each price and let the echo update the UI
                    newPrices.forEach { (symbol, price) ->
                        val payload = gson.toJson(PriceMessage(symbol, price))
                        webSocketManager.send(payload)
                    }
                } else {
                    // Fallback path: update prices locally so the UI always animates
                    // even when the echo server is unreachable
                    _stockItems.value = currentItems.map { item ->
                        val newPrice = newPrices[item.symbol] ?: item.price
                        val change = when {
                            newPrice > item.price -> PriceChange.UP
                            newPrice < item.price -> PriceChange.DOWN
                            else -> PriceChange.NEUTRAL
                        }
                        item.copy(previousPrice = item.price, price = newPrice, change = change)
                    }
                }
            }
        }
    }

    private fun randomPrice(current: Double): Double {
        // If the current price is 0.0, we use a small base value to start the random walk
        val base = if (current == 0.0) Random.nextDouble(10.0, 200.0) else current
        val pct = Random.nextDouble(-0.02, 0.02)
        val raw = base * (1.0 + pct)
        return (raw * 100).roundToLong() / 100.0
    }

    private fun updatePrice(symbol: String, newPrice: Double) {
        _stockItems.value = _stockItems.value.map { item ->
            if (item.symbol == symbol) {
                val change = when {
                    newPrice > item.price -> PriceChange.UP
                    newPrice < item.price -> PriceChange.DOWN
                    else -> PriceChange.NEUTRAL
                }
                item.copy(previousPrice = item.price, price = newPrice, change = change)
            } else {
                item
            }
        }
    }
}
