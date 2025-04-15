package com.tutorial.betterlife

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen() }
        composable(Screen.Calendar.route) { CalendarScreen() }
        composable(Screen.ListView.route) { ListViewScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}