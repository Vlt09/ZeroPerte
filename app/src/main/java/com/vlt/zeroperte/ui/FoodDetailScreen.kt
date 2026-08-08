package com.vlt.zeroperte.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vlt.zeroperte.business.FoodStatusCalculator
import com.vlt.zeroperte.ui.theme.ColorFamily
import com.vlt.zeroperte.ui.theme.extendedDark
import com.vlt.zeroperte.ui.theme.extendedLight
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class FoodDetailItem(
    val name: String,
    val brand: String?,
    val purchaseDate: LocalDate?,
    val expiryDate: LocalDate,
    val comment: String?,
    val remainingDays: Long,
    val photo: ImageVector? = null
)

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun FoodDetailScreen(
    modifier: Modifier = Modifier,
    foodId: Long?,
    viewModel: FoodDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    val detailState = viewModel.viewState.collectAsStateWithLifecycle()

    val extendedColors = if (isSystemInDarkTheme()) extendedDark else extendedLight


    /*val foodDetail = FoodDetailItem(
        name = "Test",
        brand = "Test",
        purchaseDate = null,
        expiryDate = LocalDate.of(2026, 8, 12),
        comment = null,
        remainingDays = 5,
        photo = null
    )*/


    LaunchedEffect(Unit) {
        if (foodId != null){
            viewModel.fetchFood(foodId)
        }
    }

    when(detailState.value){
        is FoodDetailViewModel.ViewState.Success -> {
            val successState = detailState.value as FoodDetailViewModel.ViewState.Success
            val remainingDays = ChronoUnit.DAYS.between(LocalDate.now(),
                successState.foodDto.expiryDate)

            val statusColor = when {
                remainingDays <= 0 -> extendedColors.expiredCard
                remainingDays <= FoodStatusCalculator.soonExpiredDays -> extendedColors.expiredSoonCard
                else -> extendedColors.validCard
            }


            FoodDetailUI(
                modifier = modifier,
                onBackClick = { navController.navigate(Screen.FoodList.route) },
                onDeleteClick = onDeleteClick,
                foodDetail = FoodDetailItem(
                    name = successState.foodDto.name,
                    brand = successState.foodDto.brand,
                    purchaseDate = successState.foodDto.datePurchased,
                    expiryDate = successState.foodDto.expiryDate,
                    comment = successState.foodDto.comment,
                    remainingDays = remainingDays,
                    photo = null
                ),
                statusColor = statusColor,
            )
        }
        is FoodDetailViewModel.ViewState.Failure ->
            FoodDetailUI(
                modifier = modifier,
                onBackClick = { navController.navigate(Screen.FoodList.route)},
                onDeleteClick = {},
                foodDetail = null,
                statusColor = null
            )
        else -> {}
    }

}

@Composable
private fun FoodDetailUI(
    modifier: Modifier,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    foodDetail: FoodDetailItem?,
    statusColor: ColorFamily?,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // --- Barre du haut : retour + titre + suppression ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Détails aliment",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onBackground
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Supprimer l'aliment",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }

        if (foodDetail != null && statusColor != null){
            // --- Photo + statut de péremption ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = foodDetail.photo ?: Icons.Filled.Photo,
                        contentDescription = "Photo de l'aliment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (foodDetail.remainingDays <= 0) {
                            "Expiré"
                        } else {
                            "Expire dans ${foodDetail.remainingDays} jours"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = statusColor.onColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // --- Champs en lecture seule ---
            ReadOnlyDetailField(
                label = "Nom de l'aliment",
                value = foodDetail.name
            )

            ReadOnlyDetailField(
                label = "Date d'achat",
                value = foodDetail.purchaseDate?.format(dateFormatter) ?: "mm/dd/yyyy"
            )

            ReadOnlyDetailField(
                label = "Marque",
                value = foodDetail.brand?.takeIf { it.isNotBlank() } ?: "N/A"
            )

            ReadOnlyDetailField(
                label = "Commentaire",
                value = foodDetail.comment?.takeIf { it.isNotBlank() } ?: "Saisir"
            )
        } else{
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                val onBackground = MaterialTheme.colorScheme.onBackground
                val onError = MaterialTheme.colorScheme.onError

                Icon(
                    imageVector = Icons.Filled.NoFood,
                    contentDescription = "Aucun aliment",
                    tint = onError,
                    modifier = Modifier.size(64.dp)
                )

                BasicText(
                    text = "Problème pendant la récupération des données",
                    autoSize = TextAutoSize.StepBased(maxFontSize = 24.sp),
                    style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    color = { onBackground },
                )
            }

        }
    }
}

@Composable
private fun ReadOnlyDetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}