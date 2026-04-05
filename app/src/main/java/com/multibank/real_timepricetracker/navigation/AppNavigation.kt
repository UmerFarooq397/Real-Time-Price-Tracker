package com.multibank.real_timepricetracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.multibank.real_timepricetracker.ui.details.DetailsScreen
import com.multibank.real_timepricetracker.ui.feed.FeedScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {
        composable(route = Screen.Feed.route) {
            FeedScreen(
                onSymbolClick = { symbol ->
                    navController.navigate(Screen.Details.createRoute(symbol))
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("symbol") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "stocks://symbol/{symbol}" }
            )
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
            DetailsScreen(
                symbol = symbol,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
