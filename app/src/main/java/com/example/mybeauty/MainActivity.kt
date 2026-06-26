package com.example.mybeauty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import com.example.mybeauty.ui.screens.DetailScreen
import com.example.mybeauty.ui.screens.ListScreen
import com.example.mybeauty.ui.screens.StatsScreen
import com.example.mybeauty.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val navItems = listOf(
                NavItem("list", "Список", Icons.Filled.List),
                NavItem("stats", "Статистика", Icons.Filled.BarChart),
                NavItem("settings", "Настройки", Icons.Filled.Settings)
            )

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        navItems.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo("list") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "list",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("list") {
                        ListScreen(
                            onNavigateToDetail = { id ->
                                navController.navigate("detail/$id")
                            },
                            onNavigateToNew = {
                                navController.navigate("detail/new")
                            }
                        )
                    }
                    composable("detail/{procedureId}") { backStackEntry ->
                        val procedureId = backStackEntry.arguments?.getString("procedureId")
                        DetailScreen(
                            procedureId = if (procedureId == "new") null else procedureId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("stats") {
                        StatsScreen()
                    }
                    composable("settings") {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)