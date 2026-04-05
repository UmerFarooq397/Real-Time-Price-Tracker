package com.multibank.real_timepricetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.multibank.real_timepricetracker.navigation.AppNavigation
import com.multibank.real_timepricetracker.ui.theme.RealTimePriceTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealTimePriceTrackerTheme {
                AppNavigation()
            }
        }
    }
}
