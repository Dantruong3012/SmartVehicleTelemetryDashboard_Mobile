package com.dantruong.smartvehicletelemetrydashboard_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.dashboard.DashboardScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.splash.SplashScreen
import com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast.WeatherForecastScreen

@Composable
fun AppNavHost(
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route,
        modifier = modifier
    ) {
        composable(route = AppRoute.Splash.route) {
            SplashScreen(
                onNavigateToDashboard = {
                    navController.navigate(AppRoute.Dashboard.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

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
