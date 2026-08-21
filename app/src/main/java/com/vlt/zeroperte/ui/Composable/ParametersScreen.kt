package com.vlt.zeroperte.ui.Composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vlt.zeroperte.ui.Home
import kotlin.math.roundToInt

@Composable
fun ParametersScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var alertDelayDays by remember { mutableFloatStateOf(3f) }

    Column(modifier = modifier.fillMaxWidth()) {
        ParametersHeader(navController = navController)

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
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Recevoir une alerte avant la péremption d'un aliment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
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
            val delayTextColor = if (notificationsEnabled) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Text(
                text = "Délai avant péremption",
                style = MaterialTheme.typography.titleMedium,
                color = delayTextColor
            )

            Text(
                text = "Être alerté ${alertDelayDays.roundToInt()} jour(s) avant la date de péremption",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            Slider(
                value = alertDelayDays,
                onValueChange = { alertDelayDays = it },
                valueRange = 1f..14f,
                steps = 12,
                enabled = notificationsEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ParametersHeader(modifier: Modifier = Modifier, navController: NavHostController) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        IconButton(
            onClick = { navController.navigate(Home) },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Retour à l'accueil",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}