package com.multibank.real_timepricetracker.ui.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.data.websocket.ConnectionState
import com.multibank.real_timepricetracker.ui.theme.PriceDown
import com.multibank.real_timepricetracker.ui.theme.PriceDownSurface
import com.multibank.real_timepricetracker.ui.theme.PriceUp
import com.multibank.real_timepricetracker.ui.theme.PriceUpSurface
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onSymbolClick: (String) -> Unit
) {
    var isFeedActive by remember { mutableStateOf(false) }
    val connectionState = if (isFeedActive) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED

    // 25 Stock Symbols for long scrollable list
    val dummyStocks = remember {
        listOf(
            StockItem("NVDA", 892.12, 880.00, PriceChange.UP),
            StockItem("NFLX", 612.40, 615.00, PriceChange.DOWN),
            StockItem("META", 495.10, 490.20, PriceChange.UP),
            StockItem("MSFT", 412.30, 415.50, PriceChange.DOWN),
            StockItem("TSLA", 238.45, 238.45, PriceChange.NEUTRAL),
            StockItem("AAPL", 182.52, 181.10, PriceChange.UP),
            StockItem("AMZN", 175.22, 174.10, PriceChange.UP),
            StockItem("GOOG", 142.65, 143.20, PriceChange.DOWN),
            StockItem("ADBE", 530.15, 525.00, PriceChange.UP),
            StockItem("CRM", 305.20, 310.00, PriceChange.DOWN),
            StockItem("AMD", 180.50, 175.00, PriceChange.UP),
            StockItem("INTC", 43.20, 44.50, PriceChange.DOWN),
            StockItem("PYPL", 65.40, 64.00, PriceChange.UP),
            StockItem("ORCL", 125.80, 127.00, PriceChange.DOWN),
            StockItem("IBM", 190.20, 188.50, PriceChange.UP),
            StockItem("QCOM", 155.30, 158.00, PriceChange.DOWN),
            StockItem("TXN", 170.45, 169.00, PriceChange.UP),
            StockItem("AVGO", 1305.20, 1290.00, PriceChange.UP),
            StockItem("MU", 95.10, 98.00, PriceChange.DOWN),
            StockItem("NOW", 750.40, 745.00, PriceChange.UP),
            StockItem("SNOW", 230.15, 235.00, PriceChange.DOWN),
            StockItem("UBER", 78.50, 76.00, PriceChange.UP),
            StockItem("LYFT", 18.20, 19.50, PriceChange.DOWN),
            StockItem("SPOT", 260.40, 255.00, PriceChange.UP),
            StockItem("COIN", 240.50, 250.00, PriceChange.DOWN)
        ).sortedByDescending { it.price }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Price Tracker",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    ConnectionIndicator(
                        state = connectionState,
                        modifier = Modifier.padding(start = 16.dp, end = 4.dp)
                    )
                },
                actions = {
                    FeedToggleButton(
                        isActive = isFeedActive,
                        onToggle = { isFeedActive = !isFeedActive }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (!isFeedActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap Start to begin\nreceiving live prices",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(
                    items = dummyStocks,
                    key = { it.symbol }
                ) { stock ->
                    StockRow(
                        stock = stock,
                        onClick = { onSymbolClick(stock.symbol) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun ConnectionIndicator(
    state: ConnectionState,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        ConnectionState.CONNECTED    -> Color(0xFF4CAF50)
        ConnectionState.DISCONNECTED -> Color(0xFFE53935)
        ConnectionState.CONNECTING   -> Color(0xFFFF9800)
    }
    val label = when (state) {
        ConnectionState.CONNECTED    -> "Connected"
        ConnectionState.DISCONNECTED -> "Disconnected"
        ConnectionState.CONNECTING   -> "Connecting…"
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun FeedToggleButton(
    isActive: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = if (isActive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.primary

    Button(
        onClick = onToggle,
        modifier = Modifier.padding(end = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(text = if (isActive) "Stop" else "Start")
    }
}

@Composable
private fun StockRow(
    stock: StockItem,
    onClick: () -> Unit
) {
    // Flash animation: briefly color the background green (UP) or red (DOWN)
    var isFlashing by remember { mutableStateOf(false) }

    LaunchedEffect(stock.price) {
        if (stock.change != PriceChange.NEUTRAL) {
            isFlashing = true
            delay(1000)
            isFlashing = false
        }
    }

    val flashTarget = when {
        isFlashing && stock.change == PriceChange.UP   -> PriceUpSurface
        isFlashing && stock.change == PriceChange.DOWN -> PriceDownSurface
        else -> Color.Transparent
    }

    val backgroundColor by animateColorAsState(
        targetValue = flashTarget,
        animationSpec = tween(durationMillis = 300),
        label = "flashColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Symbol + change indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stock.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(72.dp)
            )
            PriceChangeArrow(change = stock.change)
        }

        // Price
        Text(
            text = "$${String.format("%.2f", stock.price)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = when (stock.change) {
                PriceChange.UP      -> PriceUp
                PriceChange.DOWN    -> PriceDown
                PriceChange.NEUTRAL -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun PriceChangeArrow(change: PriceChange, modifier: Modifier = Modifier) {
    val (arrow, color) = when (change) {
        PriceChange.UP      -> "↑" to PriceUp
        PriceChange.DOWN    -> "↓" to PriceDown
        PriceChange.NEUTRAL -> "–" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = arrow,
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}
