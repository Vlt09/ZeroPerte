package com.vlt.zeroperte.ui.Composable

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vlt.zeroperte.ui.FoodCreateUpdate
import com.vlt.zeroperte.ui.FoodList
import com.vlt.zeroperte.ui.Parameters
import com.vlt.zeroperte.ui.ViewModel.HomeViewModel

enum class HomeCardColorRole {
    Primary, Secondary, Tertiary, Error
}

data class HomeMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: Any,
    val colorRole: HomeCardColorRole = HomeCardColorRole.Primary,
    val badgeCount: Int? = null
)
val defaultHomeMenuItems = listOf(
    HomeMenuItem(
        title = "Aliments",
        icon = Icons.Filled.List,
        route = FoodList,
        colorRole = HomeCardColorRole.Primary
    ),
    HomeMenuItem(
        title = "Ajouter",
        icon = Icons.Filled.Add,
        route = FoodCreateUpdate(foodId = null),
        colorRole = HomeCardColorRole.Secondary
    ),
    /*    HomeMenuItem(
            title = "Statistiques",
            icon = Icons.Filled.BarChart,
            route = Stats,
            colorRole = HomeCardColorRole.Tertiary
        ),*/
    HomeMenuItem(
        title = "Paramètres",
        icon = Icons.Filled.Settings,
        route = Parameters,
        colorRole = HomeCardColorRole.Error
    )
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    menuItems: List<HomeMenuItem> = defaultHomeMenuItems,
    viewModel: HomeViewModel = hiltViewModel()
) {

    RuntimePermissionsDialog(
        Manifest.permission.POST_NOTIFICATIONS,
        onPermissionDenied = {},
        onPermissionGranted = {},
    )

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Accueil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
        )

        Button(onClick = {
            viewModel.runOneTimeWorkRequest()
        }) {
            Text("One Time Work Request")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(menuItems) { item ->
                HomeMenuCard(
                    item = item,
                    onClick = { navController.navigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun HomeMenuCard(
    item: HomeMenuItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (containerColor, onContainerColor) = resolveColors(item.colorRole)

    Box(
        modifier = modifier
            //.aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = onContainerColor,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                color = onContainerColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (item.badgeCount != null) {
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text(item.badgeCount.toString())
            }
        }
    }
}

@Composable
private fun resolveColors(role: HomeCardColorRole): Pair<Color, Color> {
    return when (role) {
        HomeCardColorRole.Primary ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        HomeCardColorRole.Secondary ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        HomeCardColorRole.Tertiary ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        HomeCardColorRole.Error ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
}

@Composable
fun RuntimePermissionsDialog(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        if (ContextCompat.checkSelfPermission(
                LocalContext.current,
                permission) != PackageManager.PERMISSION_GRANTED) {

            val requestLocationPermissionLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->

                    if (isGranted) {
                        onPermissionGranted()
                    } else {
                        onPermissionDenied()
                    }
                }

            SideEffect {
                requestLocationPermissionLauncher.launch(permission)
            }
        }
    }
}
