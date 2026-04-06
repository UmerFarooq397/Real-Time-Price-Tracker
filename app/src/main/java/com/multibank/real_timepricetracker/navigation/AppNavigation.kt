package com.multibank.real_timepricetracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.multibank.real_timepricetracker.di.ViewModelFactory
import com.multibank.real_timepricetracker.ui.details.DetailsScreen
import com.multibank.real_timepricetracker.ui.details.DetailsViewModel
import com.multibank.real_timepricetracker.ui.feed.FeedScreen
import com.multibank.real_timepricetracker.ui.feed.FeedViewModel

@Composable
fun AppNavigation(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {
        composable(route = Screen.Feed.route) {
            val viewModel: FeedViewModel = viewModel(factory = viewModelFactory)
            FeedScreen(
                viewModel = viewModel,
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
        ) {
            val viewModel: DetailsViewModel = viewModel(factory = viewModelFactory)
            DetailsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
