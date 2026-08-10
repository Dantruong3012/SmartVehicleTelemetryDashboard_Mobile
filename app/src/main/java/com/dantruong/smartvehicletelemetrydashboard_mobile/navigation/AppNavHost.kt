package com.dantruong.smartvehicletelemetrydashboard_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard.DashboardScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast.WeatherForecastScreen

@Composable
fun AppNavHost(
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Dashboard.route,
        modifier = modifier
    ) {
        composable(route = AppRoute.Dashboard.route) {
            DashboardScreen(
                onExitApp = onExitApp,
                onOpenWeatherForecast = {
                    navController.navigate(AppRoute.WeatherForecast.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = AppRoute.WeatherForecast.route) {
            WeatherForecastScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
