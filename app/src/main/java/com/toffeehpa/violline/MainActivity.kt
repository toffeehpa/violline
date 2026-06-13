package com.toffeehpa.violline

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.Lucide
import androidx.compose.foundation.clickable
import com.composables.core.Icon
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Settings
import com.toffeehpa.violline.ui.home.HomeScreen
import com.toffeehpa.violline.ui.settings.SettingsScreen
import com.toffeehpa.violline.ui.theme.ViollineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViollineTheme {
                val navController = rememberNavController()
                val colors = ViollineTheme.colors
                val typography = ViollineTheme.typography
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.background)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") { HomeScreen() }
                            composable("settings") { SettingsScreen() }
                        }
                    }

                    // Bottom Navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surface)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            Triple("home", "Home", Lucide.House),
                            Triple("settings", "Settings", Lucide.Settings)
                        ).forEach { (route, label, icon) ->
                            val selected = currentRoute == route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .clickable { navController.navigate(route) }
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) colors.textPrimary else colors.textSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                BasicText(
                                    text = label,
                                    style = TextStyle(
                                        fontFamily = typography.fontFamily,
                                        fontSize = 11.sp,
                                        color = if (selected) colors.textPrimary else colors.textSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}