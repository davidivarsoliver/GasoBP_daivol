package com.example.gasobp_daivol.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class UserPreferences(private val context: Context) {

    private val PREFS_NAME = "Configuraciones"

    // Obtener el UID del usuario actual en Firebase
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "defaultUserId"

    private val prefs: SharedPreferences =
        context.getSharedPreferences("${PREFS_NAME}_$userId", Context.MODE_PRIVATE)

    var modoNoche: Boolean
        get() = prefs.getBoolean(MODO_NOCHE, false)
        set(value) = prefs.edit().putBoolean(MODO_NOCHE, value).apply()

    var idioma: String
        get() = prefs.getString(IDIOMA, "es") ?: "es"
        set(value) {
            prefs.edit().putString(IDIOMA, value).apply()
            // Llamar a la función para cambiar dinámicamente el idioma
            changeLanguage(value)
        }
    private fun changeLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }


    companion object {
        private const val MODO_NOCHE = "modoNoche"
        private const val IDIOMA = "idioma"
    }
}

