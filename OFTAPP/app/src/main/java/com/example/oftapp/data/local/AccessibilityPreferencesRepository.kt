package com.example.oftapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessibility_settings")

data class AccessibilitySettings(
    val altoContraste: Boolean = false,
    val escalaTexto: Float = 1.0f,
    val controlesGrandes: Boolean = false,
    val lecturaVozAutomatica: Boolean = false
)

class AccessibilityPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val ALTO_CONTRASTE = booleanPreferencesKey("alto_contraste")
        val ESCALA_TEXTO = floatPreferencesKey("escala_texto")
        val CONTROLES_GRANDES = booleanPreferencesKey("controles_grandes")
        val LECTURA_VOZ_AUTOMATICA = booleanPreferencesKey("lectura_voz_automatica")
    }

    val accessibilitySettingsFlow: Flow<AccessibilitySettings> = context.dataStore.data.map { preferences ->
        AccessibilitySettings(
            altoContraste = preferences[PreferenceKeys.ALTO_CONTRASTE] ?: false,
            escalaTexto = preferences[PreferenceKeys.ESCALA_TEXTO] ?: 1.0f,
            controlesGrandes = preferences[PreferenceKeys.CONTROLES_GRANDES] ?: false,
            lecturaVozAutomatica = preferences[PreferenceKeys.LECTURA_VOZ_AUTOMATICA] ?: false
        )
    }

    suspend fun setAltoContraste(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ALTO_CONTRASTE] = enabled
        }
    }

    suspend fun setEscalaTexto(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ESCALA_TEXTO] = scale
        }
    }

    suspend fun setControlesGrandes(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CONTROLES_GRANDES] = enabled
        }
    }

    suspend fun setLecturaVozAutomatica(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LECTURA_VOZ_AUTOMATICA] = enabled
        }
    }
}
