package com.vlt.zeroperte.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodListViewModelDto
import com.vlt.zeroperte.data.model.domain.FoodStatus
import com.vlt.zeroperte.utils.FoodMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Optional
import javax.inject.Inject


sealed interface FoodListUiState{
    data object Loading : FoodListUiState
    data object Empty : FoodListUiState

    data class Error(val message : String) : FoodListUiState

    data class Content(val foods: List<FoodListViewModelDto>)
        : FoodListUiState
}

sealed interface FoodFilterUiState{
    data class FilterState(val statuses : List<FoodStatus>,
                           val selectedStatus: Optional<FoodStatus>)
}

@HiltViewModel
class FoodListViewModel @Inject constructor(private val repository: FoodRepository): ViewModel() {


    private val _uiState = MutableStateFlow<FoodListUiState>(FoodListUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private fun fetchAllFoods() = viewModelScope.launch {
        _uiState.update { FoodListUiState.Loading }
        repository.getAllFoods()
            .collect { foods ->
                if (foods.isEmpty()) {
                    _uiState.update {  FoodListUiState.Empty }
                } else {
                    _uiState.update {
                        FoodListUiState.Content(
                            foods.map { f ->
                                FoodMapper.toFoodListViewModelDto(f)
                            }.toList()
                        )
                    }
                }
            }
    }

    // Backing property to avoid state updates from other classes
    private val _filterUiState = MutableStateFlow(
        FoodFilterUiState.FilterState(
                    statuses = FoodStatus.allStatuses,
                    selectedStatus = Optional.empty<FoodStatus>()
                    )
                )

    val filterUiState =  _filterUiState.asStateFlow()

    fun toggleStatus(status: FoodStatus){
        _filterUiState.update {
                val currentSelectedStatus = it.selectedStatus
                val newStatus =
                    // Filter selected twice is cancelled
                    if (currentSelectedStatus.isPresent && currentSelectedStatus.get() == status
                        || currentSelectedStatus.isEmpty){
                    Optional.empty<FoodStatus>()
                }
                else {
                    Optional.of<FoodStatus>(status)
                }
                return@update it.copy(
                    selectedStatus = newStatus
                )
        }
    }

}