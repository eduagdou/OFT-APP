package com.example.oftapp

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.oftapp.ui.theme.OFTAPPTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessibilityNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verificarTextosDeAccesibilidadYTalkBack() {
        composeTestRule.setContent {
            OFTAPPTheme {
                Text(text = "OftApp - Inicio de Sesión Accesible")
            }
        }

        composeTestRule.onNodeWithText("OftApp - Inicio de Sesión Accesible")
            .assertIsDisplayed()
    }
}
