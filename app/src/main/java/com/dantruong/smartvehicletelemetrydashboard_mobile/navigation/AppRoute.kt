package com.dantruong.smartvehicletelemetrydashboard_mobile.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Dashboard : AppRoute("dashboard")
    data object WeatherForecast : AppRoute("weather_forecast")
}
