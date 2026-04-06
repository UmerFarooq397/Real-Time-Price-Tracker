package com.multibank.real_timepricetracker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.multibank.real_timepricetracker.data.repository.PriceRepository
import com.multibank.real_timepricetracker.ui.details.DetailsViewModel
import com.multibank.real_timepricetracker.ui.feed.FeedViewModel

class ViewModelFactory(
    private val repository: PriceRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(FeedViewModel::class.java) ->
                FeedViewModel(repository) as T

            modelClass.isAssignableFrom(DetailsViewModel::class.java) ->
                DetailsViewModel(extras.createSavedStateHandle(), repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

