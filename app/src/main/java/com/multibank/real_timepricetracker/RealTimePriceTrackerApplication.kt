package com.multibank.real_timepricetracker

import android.app.Application
import com.multibank.real_timepricetracker.data.repository.PriceRepository
import com.multibank.real_timepricetracker.data.repository.PriceRepositoryImpl
import com.multibank.real_timepricetracker.di.ViewModelFactory

import kotlin.getValue

class RealTimePriceTrackerApplication: Application() {
    val repository: PriceRepository by lazy {
        PriceRepositoryImpl()
    }
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(repository)
    }
}