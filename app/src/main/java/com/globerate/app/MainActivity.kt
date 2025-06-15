package com.globerate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.globerate.app.ui.screens.home.HomeScreen
import com.globerate.app.ui.screens.home.viewmodel.HomeViewModel
import com.globerate.app.ui.theme.GlobeRateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        val homeViewmodel: HomeViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            GlobeRateTheme {
                HomeScreen(homeViewModel = homeViewmodel)
            }
        }
    }
}
