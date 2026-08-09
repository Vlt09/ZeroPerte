package com.vlt.zeroperte.ui

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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FoodCreateUpdateViewModel @Inject constructor(private val repository: FoodRepository) : ViewModel() {

    sealed interface ViewState{
        data class Create(val resource : FoodDto) : ViewState

        data class Update(val resource : FoodDto) : ViewState

        data object Waiting : ViewState

        data object Updated: ViewState

        data object Failure : ViewState
    }

    var form = FoodForm()
    private val _viewState = MutableStateFlow<ViewState>(ViewState.Waiting)
    val viewState = _viewState.asStateFlow()


    fun validate() {
        form.validate(true)
        Log.d("FoodCreateUpdateViewModel", "Validate (form is valid: ${form.isValid})")
    }


    fun dateToLocalDate(date: Date): LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    suspend fun save(){
        form.validate(true)
        if (form.isValid){
            val values = form.getRawValues()

            // Room treat 0 as not-set while inserting the item
            var id: Long = 0
            if (_viewState.value is ViewState.Update){
                id = (_viewState.value as ViewState.Update).resource.id
            }

            val foodDto = FoodDto(
                name = values["name"] as String,
                expiryDate = dateToLocalDate(values["expiryDate"] as Date),
                brand = values["brand"] as? String,
                category = values["category"] as? String,
                datePurchased = (values["datePurchased"] as? Date)?.let { dateToLocalDate(it) },
                comment = values["comment"] as? String,
                amount = (values["amount"] as? Int).let { 1 },
                id = id
            )

            Log.d(TAG, "Enregistrement de l'aliment : $foodDto")

            try {

                when(_viewState.value) {
                    is ViewState.Create -> {
                        repository.create(foodDto)
                        _viewState.update { ViewState.Create(foodDto) }
                        Log.i(TAG, "Aliment enregistré avec succès : id=${foodDto}")
                    }
                    is ViewState.Update -> {
                        repository.update(foodDto)
                        _viewState.update { ViewState.Updated }
                        Log.i(TAG, "Aliment update avec succès : id=${foodDto}")
                    }
                    else -> Log.i(TAG, "Erreur embranchement else impossible")

                }
            } catch (e: Exception) {
                Log.e(TAG, "Échec de l'enregistrement de l'aliment", e)
                _viewState.update { ViewState.Failure }
            }
        }
        else{
            _viewState.update { ViewState.Failure }
        }
    }

    suspend fun fetchFood(foodId: Long) {
        try {
            val food = repository.getById(foodId)
            Log.i(TAG,"fetchFood funcion CreateUpdateVM get food from db : $food")

            val dto = FoodMapper.toFoodDto(food!!)
            _viewState.update { ViewState.Update(dto) }

        }catch (e: Exception){
            _viewState.update { ViewState.Failure }
            Log.i(TAG, "Failed to fetch food with id $foodId")
        }

    }
}