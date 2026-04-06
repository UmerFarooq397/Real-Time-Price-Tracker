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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    viewModel: FeedViewModel,
    onSymbolClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                        state = uiState.connectionState,
                        modifier = Modifier.padding(start = 16.dp, end = 4.dp)
                    )
                },
                actions = {
                    FeedToggleButton(
                        isActive = uiState.isFeedActive,
                        onToggle = { viewModel.toggleFeed() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (!uiState.isFeedActive) {
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
                    items = uiState.stocks,
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
            delay(1_000)
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
