package com.vlt.zeroperte.ui.Composable

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.vlt.zeroperte.R
import com.vlt.zeroperte.business.FoodStatusCalculator
import com.vlt.zeroperte.data.model.FoodCategory
import com.vlt.zeroperte.ui.FoodCreateUpdate
import com.vlt.zeroperte.ui.FoodList
import com.vlt.zeroperte.ui.ViewModel.AppSettingsViewModel
import com.vlt.zeroperte.ui.ViewModel.FoodDetailViewModel
import com.vlt.zeroperte.ui.theme.ColorFamily
import com.vlt.zeroperte.ui.theme.extendedDark
import com.vlt.zeroperte.ui.theme.extendedLight
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class FoodDetailItem(
    val name: String,
    val category: FoodCategory?,
    val brand: String?,
    val purchaseDate: LocalDate?,
    val expiryDate: LocalDate,
    val comment: String?,
    val remainingDays: Long,
    val photo: ImageVector? = null,
    val id: Long
)

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun FoodDetailScreen(
    modifier: Modifier = Modifier,
    foodId: Long?,
    viewModel: FoodDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    navController: NavHostController,
    appSettingsViewModel: AppSettingsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val detailState = viewModel.viewState.collectAsStateWithLifecycle()
    val appSettingsUiState by appSettingsViewModel.uiState.collectAsStateWithLifecycle()

    val extendedColors = if (appSettingsUiState.darkModeEnabled) extendedDark else extendedLight

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
                onBackClick = { navController.navigate(FoodList) },
                onDeleteClick = onDeleteClick,
                foodDetail = FoodDetailItem(
                    id = successState.foodDto.id,
                    name = successState.foodDto.name,
                    category = successState.foodDto.category,
                    brand = successState.foodDto.brand,
                    purchaseDate = successState.foodDto.datePurchased,
                    expiryDate = successState.foodDto.expiryDate,
                    comment = successState.foodDto.comment,
                    remainingDays = remainingDays,
                    photo = null
                ),
                statusColor = statusColor,
                navController = navController,
                appSettingsViewModel = appSettingsViewModel
            )
        }
        is FoodDetailViewModel.ViewState.Failure ->
            FoodDetailUI(
                modifier = modifier,
                onBackClick = { navController.navigate(FoodList)},
                onDeleteClick = {},
                foodDetail = null,
                statusColor = null,
                navController = navController,
                appSettingsViewModel = appSettingsViewModel
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
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel
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
                    contentDescription = stringResource(R.string.food_detail_content_desc_back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = stringResource(R.string.food_detail_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            AppSettingsActions(appSettingsViewModel = appSettingsViewModel)

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onBackground
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.food_detail_content_desc_delete),
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
                /*Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = foodDetail.photo ?: Icons.Filled.Photo,
                        contentDescription = stringResource(R.string.food_detail_content_desc_photo),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }*/

                Box(
                    modifier = Modifier
                        .aspectRatio(2.3f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (foodDetail.remainingDays <= 0) {
                            stringResource(R.string.common_expired)
                        } else {
                            stringResource(R.string.common_expires_in_days, foodDetail.remainingDays)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = statusColor.onColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // --- Champs en lecture seule ---
            ReadOnlyDetailField(
                label = stringResource(R.string.common_food_name_label),
                value = foodDetail.name
            )

            ReadOnlyDetailField(
                label = stringResource(R.string.food_detail_label_purchase_date),
                value = foodDetail.purchaseDate?.format(dateFormatter) ?: stringResource(R.string.food_detail_placeholder_date)
            )

            ReadOnlyDetailField(
                label = stringResource(R.string.food_detail_category),
                value = foodDetail.category?.let { stringResource(it.labelId) } ?: stringResource(R.string.food_detail_na)
            )

            ReadOnlyDetailField(
                label = stringResource(R.string.food_detail_label_brand),
                value = foodDetail.brand?.takeIf { it.isNotBlank() } ?: stringResource(R.string.food_detail_na)
            )

            ReadOnlyDetailField(
                label = stringResource(R.string.food_detail_label_comment),
                value = foodDetail.comment?.takeIf { it.isNotBlank() } ?: stringResource(R.string.food_detail_placeholder_comment)
            )

            Surface(
                onClick = {
                    navController.navigate(
                        FoodCreateUpdate(foodId = foodDetail.id)
                    )
                },
                shape = RoundedCornerShape(50.dp),
                color = statusColor.colorContainer,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(R.string.food_detail_edit_button),
                    style = MaterialTheme.typography.labelLarge.copy(
                    ),
                    color = statusColor.onColorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                )
            }



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
                    contentDescription = stringResource(R.string.common_content_desc_no_food),
                    tint = onError,
                    modifier = Modifier.size(64.dp)
                )

                BasicText(
                    text = stringResource(R.string.food_detail_error_title),
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