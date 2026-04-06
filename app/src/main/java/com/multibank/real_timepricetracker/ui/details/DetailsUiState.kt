package com.multibank.real_timepricetracker.ui.details

import com.multibank.real_timepricetracker.data.model.StockItem

data class DetailsUiState(
    val stock: StockItem? = null,
    val description: String = ""
)
