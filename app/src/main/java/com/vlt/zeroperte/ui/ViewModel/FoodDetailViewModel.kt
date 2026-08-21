package com.vlt.zeroperte.ui.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodDto
import com.vlt.zeroperte.utils.FoodMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(private val repository: FoodRepository): ViewModel() {

    sealed interface ViewState{
        data class Success(val foodDto : FoodDto) : ViewState

        data object Waiting : ViewState

        data object Failure : ViewState
    }

    private val _viewState = MutableStateFlow<ViewState>(
        ViewState.Waiting)

    val viewState = _viewState.asStateFlow()

    suspend fun fetchFood(foodId: Long){

        try {
            val food = repository.getById(foodId)
            Log.i(TAG,"Get food from db : $food")

            val dto = FoodMapper.toFoodDto(food!!)
            _viewState.update { ViewState.Success(dto) }

        }catch (e: Exception){
            _viewState.update { ViewState.Failure }
            Log.i(TAG, "Failed to fetch food with id $foodId")
        }

    }

}
