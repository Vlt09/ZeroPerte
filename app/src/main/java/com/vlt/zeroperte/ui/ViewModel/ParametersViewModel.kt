package com.vlt.zeroperte.ui.ViewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.vlt.zeroperte.business.FoodStatusCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class ParametersViewModel @Inject constructor(application: Application) : ViewModel() {

    sealed interface ParametersUiState {
        data class Content(
            val notificationsEnabled: Boolean,
            val notifDelay: Int
        ) : ParametersUiState
    }

    private val preferenceNotif: SharedPreferences = application.getSharedPreferences(
        "notif_pref",
        Context.MODE_PRIVATE
    )

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<ParametersUiState> = _uiState.asStateFlow()

    private fun loadInitialState(): ParametersUiState.Content {
        val enabled = preferenceNotif.getBoolean("notif_enabled", false)
        val delay = preferenceNotif.getInt("notif_delay", FoodStatusCalculator.soonExpiredDays)
        return ParametersUiState.Content(
            notificationsEnabled = enabled,
            notifDelay = delay
        )
    }

    fun updateNotifActivation() {
        val current = _uiState.value
        val newEnabled = !current.notificationsEnabled

        saveNotifActivationToPreference(newEnabled)

        val newDelay = if (newEnabled && preferenceNotif.getInt("notif_delay", -1) == -1) {
            FoodStatusCalculator.soonExpiredDays.also { saveNotifDelayToPreference(it) }
        } else {
            current.notifDelay
        }

        _uiState.update { current.copy(notificationsEnabled = newEnabled, notifDelay = newDelay) }
    }

    fun updateNotifDelay(notifDelay: Int) {
        saveNotifDelayToPreference(notifDelay)

        _uiState.update { state ->
            state.copy(notifDelay = notifDelay)
        }
    }

    private fun saveNotifDelayToPreference(notifDelay: Int) {
        preferenceNotif.edit().putInt("notif_delay", notifDelay).apply()
        Log.i("ParametersVm", "notif_delay saved: $notifDelay")
    }

    private fun saveNotifActivationToPreference(notifEnabled: Boolean) {
        preferenceNotif.edit().putBoolean("notif_enabled", notifEnabled).apply()
        Log.i("ParametersVm", "notif_enabled saved: $notifEnabled")
    }
}
