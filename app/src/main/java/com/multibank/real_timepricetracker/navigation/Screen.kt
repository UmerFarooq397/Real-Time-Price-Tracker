package com.multibank.real_timepricetracker.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Details : Screen("details/{symbol}") {
        fun createRoute(symbol: String) = "details/$symbol"
    }
}
