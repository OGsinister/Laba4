package com.example.testml.navigation

sealed class Screens(val route: String) {
    object MainScreen: Screens(route = "MainScreen")
    object ResultScreen: Screens(route = "ResultScreen")
}