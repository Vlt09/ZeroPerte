package com.vlt.zeroperte.ui.Composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlt.zeroperte.R
import com.vlt.zeroperte.data.model.domain.AppLanguage
import com.vlt.zeroperte.ui.ViewModel.AppSettingsViewModel

/**
 * Dark mode toggle + language picker, meant to sit next to the navigation
 * icon (home/back) in every screen header.
 */
@Composable
fun AppSettingsActions(
    appSettingsViewModel: AppSettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by appSettingsViewModel.uiState.collectAsStateWithLifecycle()
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        IconButton(onClick = { appSettingsViewModel.toggleDarkMode() }) {
            Icon(
                imageVector = if (uiState.darkModeEnabled) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = stringResource(
                    if (uiState.darkModeEnabled) R.string.action_switch_to_light_mode
                    else R.string.action_switch_to_dark_mode
                ),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Box {
            IconButton(onClick = { languageMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = stringResource(R.string.action_choose_language),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            DropdownMenu(
                expanded = languageMenuExpanded,
                onDismissRequest = { languageMenuExpanded = false }
            ) {
                AppLanguage.allLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.displayName) },
                        onClick = {
                            appSettingsViewModel.setLanguage(language)
                            languageMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}
