package com.vlt.zeroperte.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodListViewModelDto
import com.vlt.zeroperte.data.model.domain.FoodStatus
import com.vlt.zeroperte.utils.FoodMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject



@HiltViewModel
class FoodListViewModel @Inject constructor(private val repository: FoodRepository): ViewModel() {

    sealed interface FoodListUiState{
        data class Empty(val filter: FilterState) : FoodListUiState

        data class Content(val foods: List<FoodListViewModelDto>,
                           val filter: FilterState)
            : FoodListUiState

        data object Delete : FoodListUiState
        data object Loading : FoodListUiState
    }

    data class FilterState(val statuses : List<FoodStatus>,
                           val selectedStatus: FoodStatus?)


    private val _foodListFlow: Flow<List<FoodListViewModelDto>> =
        repository.getAllFoods()
            .map { foods -> foods.map { f -> FoodMapper.toFoodListViewModelDto(f) }.toList() }


    // Backing property to avoid state updates from other classes
    private val _filterUiState = MutableStateFlow(
        FilterState(
                    statuses = FoodStatus.allStatuses,
                    selectedStatus = null
                    )
                )
    val filterUiState =  _filterUiState.asStateFlow()

    val uiState : StateFlow<FoodListUiState> =
        combine(
            _foodListFlow,
            _filterUiState
        ){
            foods, filter ->
            Log.d(TAG, "combine triggered, foods.size=${foods.size}")
            if (foods.isEmpty()){
                FoodListUiState.Empty(filter)
            }
            else{
                if (filter.selectedStatus != null){
                    val filteringFoods = foods.filter { f -> f.status == filter.selectedStatus }.toList()
                    FoodListUiState.Content(filteringFoods, filter)

                }
                else{
                    FoodListUiState.Content(foods, filter)
                }
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FoodListUiState.Loading,
        )



    fun toggleStatus(status: FoodStatus){
        _filterUiState.update {
                val currentSelectedStatus = it.selectedStatus
                val newStatus =
                    // Filter selected twice is cancelled
                    if (currentSelectedStatus == status){
                    null
                }
                else {
                    status
                }
                return@update it.copy(
                    selectedStatus = newStatus
                )
        }
    }

    suspend fun delete(foodDto: FoodDto) {
        try {
            repository.delete(foodDto)
            Log.i(TAG, "Aliment supprimé avec succès : id=${foodDto}")
        } catch (e: Exception) {
            Log.e(TAG, "Échec de la suppression de l'aliment", e)
        }
    }
}