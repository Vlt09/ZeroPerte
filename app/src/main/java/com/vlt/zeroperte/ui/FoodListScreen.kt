package com.vlt.zeroperte.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlt.zeroperte.ui.theme.onBackgroundLight

@Composable
fun foodListScreen(
    modifier: Modifier,
    viewModel: FoodListViewModel = hiltViewModel()
){
    val foodListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterUiState.collectAsStateWithLifecycle()


    AddFoodFab()
    when(foodListUiState) {
        is FoodListViewModel.FoodListUiState.Content -> LazyColumn(modifier) {
            val foodsList = (foodListUiState as FoodListViewModel.FoodListUiState.Content).foods
            items(foodsList){ food ->
                Text(food.name)
            }
        }

        is FoodListViewModel.FoodListUiState.Empty -> EmptyFoodItem(modifier)
        FoodListViewModel.FoodListUiState.Loading -> Text("Loading foods")
    }
}

@Composable
internal fun EmptyFoodItem(modifier: Modifier = Modifier) {
    val onBackground = MaterialTheme.colorScheme.onBackground
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "There is no food",
            autoSize = TextAutoSize.StepBased(
                maxFontSize = 24.sp
            ),
            style = TextStyle(fontWeight = FontWeight.Bold),
            color = { onBackground },
        )
    }
}

@Composable
fun AddFoodFab() {
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