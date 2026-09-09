package com.vlt.zeroperte.ui.Composable

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vlt.zeroperte.business.FoodStatusCalculator
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.domain.FoodStatus
import com.vlt.zeroperte.ui.FoodCreateUpdate
import com.vlt.zeroperte.ui.FoodDetail
import com.vlt.zeroperte.ui.Home
import com.vlt.zeroperte.ui.ViewModel.FoodListViewModel
import com.vlt.zeroperte.ui.theme.ColorFamily
import com.vlt.zeroperte.ui.theme.extendedDark
import com.vlt.zeroperte.ui.theme.extendedLight
import com.vlt.zeroperte.utils.FoodMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit


data class FoodCardItem(
    val name: String = "Name",
    val remainingDay: Long = 0,
    val expiryDate: LocalDate,
    val photo: ImageVector?,
    val dtoRef: FoodDto
)

enum class SearchCriteria(val label: String) {

    Name("Nom"),
    Brand("Marque"),
    Category("Catégorie"),
    NoSelected("Aucun critère de recherche");

    companion object {
        val allCriteria = listOf<SearchCriteria>(Name, Brand, Category)
    }

}

@Composable
fun FoodListScreen(
    modifier: Modifier,
    viewModel: FoodListViewModel = hiltViewModel(),
    navController: NavHostController
){
    val foodListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterUiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope() // Use when User trigger Delete button

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null // This gets rid of the ripple effect
            ){
                keyboardController?.hide()
                focusManager.clearFocus(true)
            },
        floatingActionButton = {
            AddFoodFab(navController = navController)
        }
    ) { innerPadding ->

        when(foodListUiState) {
        is FoodListViewModel.FoodListUiState.Content -> FoodListLazyColumn(
            modifier,
            viewModel,
            foodListUiState as FoodListViewModel.FoodListUiState.Content,
            coroutineScope,
            filterState,
            navController
        )
        is FoodListViewModel.FoodListUiState.Empty -> EmptyFoodItem(modifier)
        FoodListViewModel.FoodListUiState.Loading -> Text("Loading foods")
        else -> {
                Log.i(TAG, "else statement foodListUiState : $foodListUiState")
            }
        }

    }

}

@Composable
private fun FoodListLazyColumn(
    modifier: Modifier, viewModel: FoodListViewModel,
    content: FoodListViewModel.FoodListUiState.Content,
    coroutineScope: CoroutineScope,
    filterState: FoodListViewModel.FilterState,
    navController: NavHostController
) {
    Log.i(TAG, "FoodListViewModel.FoodListUiState.Content")

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

    val textFieldState = rememberTextFieldState()
    var selectedCriteria by remember { mutableStateOf(SearchCriteria.NoSelected) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                FoodHeader(
                    navController = navController,
                    modifier = Modifier.align(Alignment.End)
                )


                FoodSearchBar(
                    textFieldState = textFieldState,
                    selectedCriteria = selectedCriteria,
                    onCriteriaSelected = {
                        selectedCriteria = it
                        viewModel.toggleCriteria(selectedCriteria)
                                         },
                    onSearch = { result -> viewModel.triggerSearch(result) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FoodStatusFilterRow(filterState = filterState,
                onStatusClick = {foodStatus -> viewModel.toggleStatus(foodStatus)})

        }

        items(content.foods){foodVmDto ->
            val remainingDay = ChronoUnit.DAYS.between(LocalDate.now(),
                foodVmDto.expiryDate)
            var visible by remember { mutableStateOf(true) }

            val foodCardItem = FoodCardItem(
                name = foodVmDto.name,
                remainingDay = remainingDay,
                expiryDate = foodVmDto.expiryDate,
                photo = null,
                dtoRef = FoodMapper.toFoodDto(foodVmDto)
            )

            AnimatedVisibility(
                visible = visible,
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(animationSpec = tween(300)),
                modifier = Modifier.animateItem()
            ) {
                FoodCard(modifier = modifier,
                    foodCardItem = foodCardItem,
                    navController = navController,
                    onDeleteClick = {
                        visible = false

                        coroutineScope.launch {
                            delay(600) // animation duration
                            viewModel.delete(foodCardItem.dtoRef)
                        }
                    }
                )
            }

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
            navController.navigate(FoodCreateUpdate(null))
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Ajouter un aliment")
    }
}

@Composable
internal fun FoodHeader(modifier: Modifier = Modifier, navController: NavHostController) {
        IconButton(
            onClick = { navController.navigate(Home) },
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Retour à l'accueil",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
}


@Composable
internal fun FoodCard(
    modifier: Modifier = Modifier,
    foodCardItem: FoodCardItem,
    onDeleteClick: () -> Unit,
    navController: NavHostController
) {
    val extendedColors = if (isSystemInDarkTheme()) extendedDark else extendedLight

    val cardColor = when {
        foodCardItem.remainingDay <= 0 -> extendedColors.expiredCard
        foodCardItem.remainingDay <= FoodStatusCalculator.soonExpiredDays -> extendedColors.expiredSoonCard
        else -> extendedColors.validCard
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor.color,
        ),
        modifier = modifier
            .padding(8.dp)
            .clickable {
                navController.navigate(FoodDetail(foodId = foodCardItem.dtoRef.id))
            },
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
                    text = foodCardItem.name,
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
                IconButton(onClick = onDeleteClick, modifier = Modifier.fillMaxWidth()){
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete button",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                            .size(30.dp)
                    )
                }
            }


        }
    }
}

@Composable
internal fun FoodStatusFilterRow(
    filterState: FoodListViewModel.FilterState,
    onStatusClick: (FoodStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = if (isSystemInDarkTheme()) extendedDark else extendedLight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterStatusButton(
            text = "Valide",
            colorFamily = extendedColors.validCard,
            selected = filterState.selectedStatus == FoodStatus.Edible,
            onClick = { onStatusClick(FoodStatus.Edible) },
            modifier = Modifier.weight(1f)
        )

        FilterStatusButton(
            text = "Expire bientôt",
            colorFamily = extendedColors.expiredSoonCard,
            selected = filterState.selectedStatus == FoodStatus.ExpiringSoon,
            onClick = { onStatusClick(FoodStatus.ExpiringSoon) },
            modifier = Modifier.weight(1f)
        )

        FilterStatusButton(
            text = "Expiré",
            colorFamily = extendedColors.expiredCard,
            selected = filterState.selectedStatus == FoodStatus.Expired,
            onClick = { onStatusClick(FoodStatus.Expired) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FilterStatusButton(
    text: String,
    colorFamily: ColorFamily,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = colorFamily.colorContainer,
        border = if (selected) {
            BorderStroke(2.dp, colorFamily.color)
        } else {
            null
        },
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = colorFamily.onColorContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchBar(
    textFieldState: TextFieldState,
    selectedCriteria: SearchCriteria,
    onCriteriaSelected: (SearchCriteria) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        onSearch(textFieldState.text.toString())
                                    },
                    onSearch = {
                        expanded = false
                    },
                    expanded = false,
                    onExpandedChange = { expanded = false },
                    placeholder = {
                        if (selectedCriteria == SearchCriteria.NoSelected){
                            Text(selectedCriteria.label.lowercase())
                        }
                        else{
                            Text("Rechercher par ${selectedCriteria.label.lowercase()}")
                        }
                      },
                    trailingIcon = {
                        Box() {
                            IconButton(
                                modifier = Modifier.padding(end = 30.dp),
                                onClick = {
                                    textFieldState.edit { replace(0, length, "")}
                                    onCriteriaSelected(SearchCriteria.NoSelected)
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Choisir le critère de recherche"
                                )
                            }

                            IconButton(
                                modifier = Modifier.padding(start = 30.dp),
                                onClick = { menuExpanded = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FilterList,
                                    contentDescription = "Choisir le critère de recherche"
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                SearchCriteria.entries.filter { c -> c != SearchCriteria.NoSelected }
                                    .forEach { criteria ->
                                        DropdownMenuItem(
                                            text = { Text(criteria.label) },
                                            onClick = {
                                                onCriteriaSelected(criteria)
                                                menuExpanded = false
                                            }
                                        )
                                }
                            }
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = { expanded = false },
        ) {}
    }
}