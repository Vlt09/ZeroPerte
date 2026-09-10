package com.vlt.zeroperte.ui.ViewModel

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import com.vlt.zeroperte.data.AppSettingsRepository
import com.vlt.zeroperte.data.model.domain.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    application: Application,
    private val repository: AppSettingsRepository
) : ViewModel() {

    data class AppSettingsUiState(
        val darkModeEnabled: Boolean,
        val language: AppLanguage
    )

    private fun systemDarkModeDefault(application: Application): Boolean {
        val uiMode = application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    private val _uiState = MutableStateFlow(
        AppSettingsUiState(
            darkModeEnabled = repository.isDarkModeEnabled(systemDarkModeDefault(application)),
            language = repository.getLanguage()
        )
    )
    val uiState: StateFlow<AppSettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_uiState.value.darkModeEnabled
        repository.setDarkModeEnabled(newValue)
        _uiState.update { it.copy(darkModeEnabled = newValue) }
    }

    fun setLanguage(language: AppLanguage) {
        repository.setLanguage(language)
        _uiState.update { it.copy(language = language) }
    }
}
