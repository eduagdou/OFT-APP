package com.example.oftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.oftapp.data.local.AccessibilityPreferencesRepository
import com.example.oftapp.data.local.AccessibilitySettings
import com.example.oftapp.ui.navigation.OftAppNavigation
import com.example.oftapp.ui.theme.OFTAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val accessibilityRepo = remember { AccessibilityPreferencesRepository(context) }
            val settings by accessibilityRepo.accessibilitySettingsFlow.collectAsState(initial = AccessibilitySettings())

            OFTAPPTheme(
                modoTema = settings.modoTema,
                altoContraste = settings.altoContraste,
                escalaBotones = settings.escalaBotones
            ) {
                OftAppNavigation()
            }
        }
    }
}
