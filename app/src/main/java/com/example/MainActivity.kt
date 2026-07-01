package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainHubScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val isDarkMode by viewModel.isDarkMode.collectAsState()
      
      var showSplash by remember { mutableStateOf(true) }

      MyApplicationTheme(
        darkTheme = isDarkMode,
        dynamicColor = false // Force Blue/Green theme instead of device wallpaper colors
      ) {
        Crossfade(
          targetState = showSplash,
          animationSpec = tween(durationMillis = 600),
          label = "AppStageTransition"
        ) { splashActive ->
          if (splashActive) {
            SplashScreen(
              onTimeout = { showSplash = false }
            )
          } else {
            MainHubScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}

