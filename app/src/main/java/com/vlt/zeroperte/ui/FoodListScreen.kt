package com.vlt.zeroperte.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vlt.zeroperte.ui.theme.extendedDark
import com.vlt.zeroperte.ui.theme.extendedLight
import java.time.LocalDate
import java.time.temporal.ChronoUnit


data class FoodCardItem(
    val foodName: String = "Name",
    val remainingDay: Long = 0,
    val expiryDate: LocalDate,
    val foodPhoto: ImageVector?
                        )

val sampleFoodCardItems = listOf(
    FoodCardItem(
        foodName = "Yaourt nature",
        remainingDay = 3,
        expiryDate = LocalDate.now(),
        foodPhoto = Icons.Filled.Photo
    ),
    FoodCardItem(
        foodName = "Compote de pomme",
        remainingDay = 10,
        expiryDate = LocalDate.now(),
        foodPhoto = null
    ),
    FoodCardItem(
        foodName = "Lait demi-écrémé",
        remainingDay = 0,
        expiryDate = LocalDate.now(),
        foodPhoto = Icons.Filled.Photo
    ),
    FoodCardItem(
        foodName = "Riz basmati",
        remainingDay = 45,
        expiryDate = LocalDate.now(),
        foodPhoto = null
    ),
    FoodCardItem(
        foodName = "Test",
        remainingDay = 45,
        expiryDate = LocalDate.now(),
        foodPhoto = null
    ),
    FoodCardItem(
        foodName = "Test",
        remainingDay = 45,
        expiryDate = LocalDate.now(),
        foodPhoto = null
    ),
    FoodCardItem(
        foodName = "Test",
        remainingDay = 45,
        expiryDate = LocalDate.now(),
        foodPhoto = null
    )


)

@Composable
fun foodListScreen(
    modifier: Modifier,
    viewModel: FoodListViewModel = hiltViewModel(),
    navController: NavHostController
){
    val foodListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            AddFoodFab(navController = navController)
        }
    ) { innerPadding ->

        when(foodListUiState) {
        is FoodListViewModel.FoodListUiState.Content -> FoodListLazyColumn(modifier,
            foodListUiState as FoodListViewModel.FoodListUiState.Content
        )
        is FoodListViewModel.FoodListUiState.Empty -> EmptyFoodItem(modifier)
        FoodListViewModel.FoodListUiState.Loading -> Text("Loading foods")
        }

        //FoodListLazyColumn(modifier, foodListUiState)
    }

}

@Composable
private fun FoodListLazyColumn(modifier: Modifier, content: FoodListViewModel.FoodListUiState.Content) {
    val listState = rememberLazyListState()
    var trackHeightPx by remember { mutableIntStateOf(0) }

    val scrollProgress by remember {
        derivedStateOf {
            val firstVisibleItem = listState.firstVisibleItemIndex
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems > 1)
                (firstVisibleItem.toFloat() / (totalItems - 1)).coerceIn(0f, 1f)
            else 0f
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {

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

        items(content.foods){food ->
            val remainingDay = ChronoUnit.DAYS.between(LocalDate.now(),
                food.expiryDate)

            val foodCardItem = FoodCardItem(
                foodName = food.name,
                remainingDay = remainingDay,
                expiryDate = food.expiryDate,
                foodPhoto = null
            )

            FoodCard(modifier, foodCardItem,)
        }

    }

    // Custom scrollbar
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(6.dp)
            .padding(vertical = 12.dp)
            .onGloballyPositioned { layoutCoordinates ->
                trackHeightPx = layoutCoordinates.size.height
            }
    ) {
        // Scroll thumb
        val thumbHeightPx = with(LocalDensity.current) { 40.dp.toPx() }
        val offsetY = ((trackHeightPx - thumbHeightPx) * scrollProgress).toInt()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .offset { IntOffset(x = 0, y = offsetY) }
                .background(Color.Gray, RoundedCornerShape(3.dp))
        )
    }
}


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
internal fun AddFoodFab(onClick: () -> Unit = {}, navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            navController.navigate(Screen.FoodCreateUpdate.route)
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Ajouter un aliment")
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
internal fun FoodCard(modifier: Modifier = Modifier, foodCardItem: FoodCardItem, viewModel: FoodListViewModel) {
    val extendedColors = if (isSystemInDarkTheme()) extendedDark else extendedLight

    val cardColor = when {
        foodCardItem.remainingDay <= 0 -> extendedColors.expiredCard
        foodCardItem.remainingDay <= 5 -> extendedColors.expiredSoonCard
        else -> extendedColors.validCard
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor.color,
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
                tint = extendedColors.expiredCard.onColor,
                modifier = Modifier.size(90.dp)
            )

            Column() {

                BasicText(
                    text = foodCardItem.foodName,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 20.sp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = {cardColor.expiredSoonCardTypoDark1},
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
                        color = {cardColor.expiredSoonCardTypoDark1},
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        BasicText(
                            text = "Date de péremption : ${foodCardItem.expiryDate}",
                            autoSize = TextAutoSize.StepBased(maxFontSize = 12.sp),
                            style = MaterialTheme.typography.headlineSmall,
                            color = {cardColor.expiredSoonCardTypoDark1},
                        )
                    }
                }

            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    viewModel.delete(foodCardItem)
                }, modifier = Modifier.fillMaxWidth()){
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
}