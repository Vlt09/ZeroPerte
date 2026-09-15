package com.vlt.zeroperte.ui.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodCategory
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.data.model.FoodListViewModelDto
import com.vlt.zeroperte.data.model.domain.FoodStatus
import com.vlt.zeroperte.ui.Composable.SearchCriteria
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
import java.util.function.Predicate
import javax.inject.Inject



@HiltViewModel
class FoodListViewModel @Inject constructor(private val repository: FoodRepository): ViewModel() {

    typealias Predicate3<T1, T2, T3> = (T1, T2, T3) -> Boolean

    sealed interface FoodListUiState{
        data class Empty(val filter: FilterState) : FoodListUiState

        data class Content(val foods: List<FoodListViewModelDto>,
                           val filter: FilterState)
            : FoodListUiState

        data object Loading : FoodListUiState
    }

    data class FilterState(val statuses : List<FoodStatus>,
                           val selectedStatus: FoodStatus?,
                           val applyFilter: Predicate<FoodListViewModelDto>)

    data class SearchCriteriaState(
        val criteriaList: List<SearchCriteria>,
        val applyCriteria: Predicate3<FoodListViewModelDto, String?, FoodCategory?>,
        val searchInput: String?,
        val foodCategory: FoodCategory?
    )


    private val _foodListFlow: Flow<List<FoodListViewModelDto>> =
        repository.getAllFoods()
            .map { foods -> foods.map { f -> FoodMapper.toFoodListViewModelDto(f) }.toList() }


    // Backing property to avoid state updates from other classes
    private val _filterUiState = MutableStateFlow(
        FilterState(
                    statuses = FoodStatus.allStatuses,
                    selectedStatus = null,
                    applyFilter = {f -> true}
                    )
                )

    val filterUiState =  _filterUiState.asStateFlow()

    private val _searchBarUiState = MutableStateFlow(
        SearchCriteriaState(
            criteriaList = SearchCriteria.allCriteria,
            applyCriteria = {f, s, c -> true},
            searchInput = null,
            foodCategory = null
        )
    )

    private val searchBarUiState = _searchBarUiState.asStateFlow()

    val uiState : StateFlow<FoodListUiState> =
        combine(
            _foodListFlow,
            _filterUiState,
            _searchBarUiState
        ){
            foods, filter, searchBar ->
            Log.d(TAG, "combine triggered, foods.size=${foods.size} food ${foods[0]}")

            if (foods.isEmpty()){
                FoodListUiState.Empty(filter)
            }
            else{
                    val filteringFoods = foods.filter { f -> filter.applyFilter.test(f) &&
                                                             searchBar.applyCriteria.invoke(
                                                                 f,
                                                                 searchBar.searchInput,
                                                                 searchBar.foodCategory
                                                             )
                                                        }
                        .toList()

                FoodListUiState.Content(filteringFoods, filter)

            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FoodListUiState.Loading,
        )



    fun toggleStatus(status: FoodStatus){
        _filterUiState.update {
                var newStatus = it.selectedStatus
                var newFilter: Predicate<FoodListViewModelDto>

                // Filter selected twice is cancelled
                if (newStatus == status){
                    newStatus = null
                    newFilter = Predicate<FoodListViewModelDto>{ f -> true}
                }
                else {
                    newStatus = status
                    newFilter = Predicate<FoodListViewModelDto>{ f -> f.status == newStatus}
                }

                return@update it.copy(
                    selectedStatus = newStatus,
                    applyFilter = newFilter
                )
        }
    }


    fun toggleCriteria(searchCriteria: SearchCriteria){
        Log.i("FoodListVM", "toggle criteria $searchCriteria")
        _searchBarUiState.update {
            val newCriteria: Predicate3<FoodListViewModelDto, String?, FoodCategory?> =
                {dto, searchInput, foodCategory ->
                    when(searchCriteria){
                        SearchCriteria.Name -> dto.name == searchInput
                        SearchCriteria.Brand -> dto.brand == searchInput
                        SearchCriteria.Category -> dto.category == foodCategory
                        SearchCriteria.NoSelected -> true
                    }
                }

            return@update it.copy(
                applyCriteria = newCriteria
            )
        }
    }


    fun triggerSearch(result: String, foodCategory: FoodCategory?){
        Log.i("FoodLisViewModel", "inputSearch = $result category $foodCategory")
        _searchBarUiState.update {
            return@update it.copy(
                searchInput = result,
                foodCategory = foodCategory
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