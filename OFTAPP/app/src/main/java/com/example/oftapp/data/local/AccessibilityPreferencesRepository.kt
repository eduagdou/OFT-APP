package com.example.oftapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessibility_settings")

data class AccessibilitySettings(
    val modoTema: String = "SISTEMA", // "SISTEMA", "CLARO", "OSCURO"
    val altoContraste: Boolean = false,
    val escalaBotones: Float = 1.0f, // 1.0f (Normal 100%), 1.15f (+15%), 1.25f (+25%), 1.35f (+35%)
    val controlesGrandes: Boolean = false,
    val lecturaVozAutomatica: Boolean = false
)

class AccessibilityPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val MODO_TEMA = stringPreferencesKey("modo_tema")
        val ALTO_CONTRASTE = booleanPreferencesKey("alto_contraste")
        val ESCALA_BOTONES = floatPreferencesKey("escala_botones")
        val CONTROLES_GRANDES = booleanPreferencesKey("controles_grandes")
        val LECTURA_VOZ_AUTOMATICA = booleanPreferencesKey("lectura_voz_automatica")
    }

    val accessibilitySettingsFlow: Flow<AccessibilitySettings> = context.dataStore.data.map { preferences ->
        AccessibilitySettings(
            modoTema = preferences[PreferenceKeys.MODO_TEMA] ?: "SISTEMA",
            altoContraste = preferences[PreferenceKeys.ALTO_CONTRASTE] ?: false,
            escalaBotones = preferences[PreferenceKeys.ESCALA_BOTONES] ?: 1.0f,
            controlesGrandes = preferences[PreferenceKeys.CONTROLES_GRANDES] ?: false,
            lecturaVozAutomatica = preferences[PreferenceKeys.LECTURA_VOZ_AUTOMATICA] ?: false
        )
    }

    suspend fun setModoTema(modo: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MODO_TEMA] = modo
        }
    }

    suspend fun setAltoContraste(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ALTO_CONTRASTE] = enabled
        }
    }

    suspend fun setEscalaBotones(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ESCALA_BOTONES] = scale
            preferences[PreferenceKeys.CONTROLES_GRANDES] = scale > 1.0f
        }
    }

    suspend fun setControlesGrandes(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CONTROLES_GRANDES] = enabled
            preferences[PreferenceKeys.ESCALA_BOTONES] = if (enabled) 1.25f else 1.0f
        }
    }

    suspend fun setLecturaVozAutomatica(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LECTURA_VOZ_AUTOMATICA] = enabled
        }
    }
}
