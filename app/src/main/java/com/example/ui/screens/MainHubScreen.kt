package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.viewmodel.MainViewModel

@Composable
fun MainHubScreen(
    viewModel: MainViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showUpload by viewModel.showUploadApkScreen.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentTab == "home" && !showUpload,
                        onClick = { viewModel.setCurrentTab("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home Tab") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_tab_home")
                    )
                    NavigationBarItem(
                        selected = currentTab == "analysis" && !showUpload,
                        onClick = { viewModel.setCurrentTab("analysis") },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analysis Tab") },
                        label = { Text("Analysis") },
                        modifier = Modifier.testTag("nav_tab_analysis")
                    )
                    NavigationBarItem(
                        selected = currentTab == "reports" && !showUpload,
                        onClick = { viewModel.setCurrentTab("reports") },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Reports Tab") },
                        label = { Text("Reports") },
                        modifier = Modifier.testTag("nav_tab_reports")
                    )
                    NavigationBarItem(
                        selected = currentTab == "settings" && !showUpload,
                        onClick = { viewModel.setCurrentTab("settings") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings Tab") },
                        label = { Text("Settings") },
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                Crossfade(targetState = currentTab, label = "TabTransition") { tab ->
                    when (tab) {
                        "home" -> HomeScreen(viewModel = viewModel)
                        "analysis" -> AnalysisScreen(viewModel = viewModel)
                        "reports" -> ReportsScreen(viewModel = viewModel)
                        "settings" -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }

        // Upload APK Full-screen Overlay slide transition
        AnimatedVisibility(
            visible = showUpload,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            UploadApkScreen(
                viewModel = viewModel,
                onBack = { viewModel.setShowUploadApkScreen(false) }
            )
        }
    }
}
