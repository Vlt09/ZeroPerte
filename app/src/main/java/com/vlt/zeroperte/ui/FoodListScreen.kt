package com.vlt.zeroperte.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlt.zeroperte.ui.theme.extendedDark
import com.vlt.zeroperte.ui.theme.extendedLight
import com.vlt.zeroperte.ui.theme.onBackgroundLight
import com.vlt.zeroperte.ui.theme.onSurfaceVariantLight
import java.util.Date


data class FoodCardItem(val foodName : String = "Name",
                        val remainingDay : Int = 0,
                        val expiryDate : Date,
                        val foodPhoto : ImageVector?
                        )

val sampleFoodCardItems = listOf(
    FoodCardItem(
        foodName = "Yaourt nature",
        remainingDay = 3,
        expiryDate = Date(),
        foodPhoto = Icons.Filled.Photo
    ),
    FoodCardItem(
        foodName = "Compote de pomme",
        remainingDay = 10,
        expiryDate = Date(),
        foodPhoto = null
    ),
    FoodCardItem(
        foodName = "Lait demi-écrémé",
        remainingDay = 0,
        expiryDate = Date(),
        foodPhoto = Icons.Filled.Photo
    ),
    FoodCardItem(
        foodName = "Riz basmati",
        remainingDay = 45,
        expiryDate = Date(),
        foodPhoto = null
    )
)

@Composable
fun foodListScreen(
    modifier: Modifier,
    viewModel: FoodListViewModel = hiltViewModel()
){
    val foodListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterUiState.collectAsStateWithLifecycle()


    AddFoodFab()

        LazyColumn (
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ){

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FoodHeader(modifier)
                    }
                }

                items(sampleFoodCardItems){ item ->
                    FoodCard(modifier, item)
                }
            }

    }

    /*when(foodListUiState) {
        is FoodListViewModel.FoodListUiState.Content -> LazyColumn(modifier) {
            val foodsList = (foodListUiState as FoodListViewModel.FoodListUiState.Content).foods
            items(foodsList){ food ->
                Text(food.name)
            }
        }

        is FoodListViewModel.FoodListUiState.Empty -> EmptyFoodItem(modifier)
        FoodListViewModel.FoodListUiState.Loading -> Text("Loading foods")
    }*/

@Composable
internal fun EmptyFoodItem(modifier: Modifier = Modifier) {
    val onBackground = MaterialTheme.colorScheme.onBackground
    val onBackgroundVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Filled.NoFood,
            contentDescription = "Aucun aliment",
            tint = onBackgroundVariant,
            modifier = Modifier.size(64.dp)
        )

        BasicText(
            text = "Aucun aliment enregistré",
            autoSize = TextAutoSize.StepBased(maxFontSize = 24.sp),
            style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            color = { onBackground },
        )

        BasicText(
            text = "Ajoutez votre premier aliment pour ne plus jamais rater une date de péremption",
            autoSize = TextAutoSize.StepBased(maxFontSize = 16.sp),
            style = TextStyle(textAlign = TextAlign.Center),
            color = { onBackgroundVariant },
        )
    }
}

@Composable
internal fun AddFoodFab() {
    Scaffold(
    floatingActionButton = {
        FloatingActionButton(
        onClick = { /**/ },
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
    }
    ){ padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {}
     }
}

@Composable
internal fun FoodHeader(modifier: Modifier = Modifier) {
    Text(
        text = "Aliments",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
internal fun FoodCard(modifier: Modifier = Modifier, foodCardItem : FoodCardItem) {
    val extendedColors = if (isSystemInDarkTheme()) extendedDark else extendedLight

    val cardColor = when {
        foodCardItem.remainingDay <= 0 -> extendedColors.expiredContainer.color
        foodCardItem.remainingDay <= 5 -> extendedColors.expiredSoonContainer.color
        else -> extendedColors.customColor1.color
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
        ),
        modifier = modifier
            .padding(8.dp)
            .clickable { },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Icon(
                imageVector = Icons.Filled.Photo,
                contentDescription = "Photo Aliment",
                tint = extendedColors.expiredContainer.onColor,
                modifier = Modifier.size(90.dp)
            )

            Column() {
                val textColor = MaterialTheme.colorScheme.onSurfaceVariant

                BasicText(
                    text = foodCardItem.foodName,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 20.sp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = {textColor},
                    modifier = Modifier
                                    .padding(top = 2.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                                    .padding(start = 37.dp)

                ) {
                    BasicText(
                        text = "Expire dans ${foodCardItem.remainingDay} jours",
                        autoSize = TextAutoSize.StepBased(maxFontSize = 20.sp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = {textColor},
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        BasicText(
                            text = "Date de péremption : ${foodCardItem.expiryDate}",
                            autoSize = TextAutoSize.StepBased(maxFontSize = 12.sp),
                            style = MaterialTheme.typography.headlineSmall,
                            color = {textColor},
                        )
                    }
                }

            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
                ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Photo Aliment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(30.dp)
                )
            }


        }
    }
}