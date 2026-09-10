package com.vlt.zeroperte.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.vlt.zeroperte.data.model.domain.AppLanguage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    application: Application
) {

    private val preferences: SharedPreferences = application.getSharedPreferences(
        "app_settings_pref",
        Context.MODE_PRIVATE
    )

    fun isDarkModeEnabled(systemDefault: Boolean): Boolean =
        preferences.getBoolean(KEY_DARK_MODE, systemDefault)

    fun setDarkModeEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun getLanguage(): AppLanguage =
        AppLanguage.fromTag(preferences.getString(KEY_LANGUAGE, AppLanguage.French.tag) ?: AppLanguage.French.tag)

    fun setLanguage(language: AppLanguage) {
        preferences.edit().putString(KEY_LANGUAGE, language.tag).apply()
    }

    companion object {
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_LANGUAGE = "language"
    }
}
