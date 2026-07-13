package com.vlt.zeroperte.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vlt.zeroperte.business.FoodStatusCalculator
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodListViewModelDto
import com.vlt.zeroperte.utils.FoodMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


sealed interface FoodListUiState{
    data object Loading : FoodListUiState
    data object Empty : FoodListUiState

    // ça prend une liste d'un type Food contenant les info supplémentaire à savoir l'indication de couleur sur si un aliment arrive bientot à expiration ou non
    data class Success(val foods: List<FoodListViewModelDto>) : FoodListUiState
    data class Error(val message : String) : FoodListUiState
}

@HiltViewModel
class FoodListViewModel @Inject constructor(private val repository: FoodRepository): ViewModel() {

    val foodState : StateFlow<FoodListUiState> =
        repository.getAllFoods()
            .map { foods ->
                if (foods.isEmpty()) {
                    FoodListUiState.Empty
                } else {
                    FoodListUiState.Success(foods.map { f -> FoodMapper.toFoodListViewModelDto(f) }.toList())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FoodListUiState.Loading
            )

}