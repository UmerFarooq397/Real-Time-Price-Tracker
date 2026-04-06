package com.multibank.real_timepricetracker.ui.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multibank.real_timepricetracker.R
import com.multibank.real_timepricetracker.data.model.PriceChange
import com.multibank.real_timepricetracker.data.model.StockItem
import com.multibank.real_timepricetracker.ui.feed.PriceChangeArrow
import com.multibank.real_timepricetracker.ui.theme.PriceDown
import com.multibank.real_timepricetracker.ui.theme.PriceDownSurface
import com.multibank.real_timepricetracker.ui.theme.PriceUp
import com.multibank.real_timepricetracker.ui.theme.PriceUpSurface
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val symbol = viewModel.symbol
    val stock = uiState.stock ?: StockItem(symbol, 0.0, 0.0, PriceChange.NEUTRAL)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.market_details),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PriceCard(
                symbol = symbol,
                price = stock.price,
                change = stock.change
            )
            // Description card
            Column {
                Text(
                    text = stringResource(R.string.about_the_company),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PriceCard(
    symbol: String,
    price: Double,
    change: PriceChange
) {
    var isFlashing by remember { mutableStateOf(false) }

    LaunchedEffect(price) {
        if (change != PriceChange.NEUTRAL) {
            isFlashing = true
            delay(1000)
            isFlashing = false
        }
    }

    val flashTarget = when {
        isFlashing && change == PriceChange.UP   -> PriceUpSurface
        isFlashing && change == PriceChange.DOWN -> PriceDownSurface
        else -> MaterialTheme.colorScheme.surface
    }

    val backgroundColor by animateColorAsState(
        targetValue = flashTarget,
        animationSpec = tween(durationMillis = 400),
        label = "priceFlash"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.price_format, price),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = when (change) {
                    PriceChange.UP      -> PriceUp
                    PriceChange.DOWN    -> PriceDown
                    PriceChange.NEUTRAL -> MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriceChangeArrow(change = change)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (change) {
                        PriceChange.UP      -> stringResource(R.string.bullish_movement)
                        PriceChange.DOWN    -> stringResource(R.string.bearish_movement)
                        PriceChange.NEUTRAL -> stringResource(R.string.stable)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = when (change) {
                        PriceChange.UP      -> PriceUp
                        PriceChange.DOWN    -> PriceDown
                        PriceChange.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
