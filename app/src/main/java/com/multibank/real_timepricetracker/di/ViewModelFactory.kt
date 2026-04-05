package com.multibank.real_timepricetracker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.multibank.real_timepricetracker.data.repository.PriceRepository

class ViewModelFactory(
    private val repository: PriceRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        throw UnsupportedOperationException("ViewModels are disabled for design-only mode")
    }
}
