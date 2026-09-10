package com.vlt.zeroperte.ui.Composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vlt.zeroperte.R
import com.vlt.zeroperte.ui.Home
import com.vlt.zeroperte.ui.ViewModel.AppSettingsViewModel
import com.vlt.zeroperte.ui.ViewModel.ParametersViewModel
import kotlinx.coroutines.launch

@Composable
fun ParametersScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ParametersViewModel = hiltViewModel(),
    appSettingsViewModel: AppSettingsViewModel
    ) {

    val parameterUiState = viewModel.uiState.collectAsStateWithLifecycle()
    val notifEnable = (parameterUiState.value as ParametersViewModel.ParametersUiState.Content).notificationsEnabled
    val notifDelay = (parameterUiState.value as ParametersViewModel.ParametersUiState.Content).notifDelay

    Column(modifier = modifier.fillMaxWidth()) {
        ParametersHeader(navController = navController, appSettingsViewModel = appSettingsViewModel)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Enable or disabled notifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.parameters_notifications_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.parameters_notifications_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = notifEnable,
                    onCheckedChange = { viewModel.updateNotifActivation() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Warning period
            val delayTextColor = if (notifEnable) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Text(
                text = stringResource(R.string.parameters_delay_title),
                style = MaterialTheme.typography.titleMedium,
                color = delayTextColor
            )

            Text(
                text = stringResource(R.string.parameters_delay_subtitle, notifDelay),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            Slider(
                value = notifDelay.toFloat(),
                onValueChange = { viewModel.updateNotifDelay(it.toInt()) },
                valueRange = 1f..14f,
                steps = 12,
                enabled = notifEnable,
                modifier = Modifier.fillMaxWidth()
            )
        }

        DeleteDataSection(viewModel)

    }

}

@Composable
private fun ParametersHeader(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appSettingsViewModel: AppSettingsViewModel
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.parameters_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            AppSettingsActions(appSettingsViewModel = appSettingsViewModel)

            IconButton(
                onClick = { navController.navigate(Home) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = stringResource(R.string.common_content_desc_back_to_home),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun DeleteDataSection(
    viewModel: ParametersViewModel,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope() // Use when User trigger Delete Data button

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.parameters_danger_zone_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = stringResource(R.string.parameters_danger_zone_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Button(
            onClick = { showDeleteConfirmation = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.parameters_delete_all_button))
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.parameters_delete_confirm_title)) },
            text = { Text(stringResource(R.string.parameters_delete_confirm_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch { viewModel.deleteAllData() }
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(stringResource(R.string.common_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}