package com.multibank.real_timepricetracker.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.real_timepricetracker.data.repository.PriceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FeedViewModel(
    private val repository: PriceRepository
) : ViewModel() {

    private val _isFeedActive = MutableStateFlow(false)

    val uiState: StateFlow<FeedUiState> = combine(
        repository.stockItems,
        repository.connectionState,
        _isFeedActive
    ) { stocks, connectionState, isFeedActive ->
        FeedUiState(
            stocks = stocks.sortedByDescending { it.price },
            connectionState = connectionState,
            isFeedActive = isFeedActive
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        // Seed with pre-loaded stocks so the list is ready before the first combine emission
        initialValue = FeedUiState(
            stocks = repository.stockItems.value.sortedByDescending { it.price },
            connectionState = repository.connectionState.value
        )
    )

    fun toggleFeed() {
        if (_isFeedActive.value) {
            _isFeedActive.value = false
            repository.stopFeed()
        } else {
            _isFeedActive.value = true
            repository.startFeed()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopFeed()
    }
}
