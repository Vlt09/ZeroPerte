package com.vlt.zeroperte.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodDto
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
        data class Modify(val resource : FoodDto) : ViewState
        data object Waiting : ViewState
        data object Failure : ViewState
    }

    var form = FoodForm()
    private val _viewState = MutableStateFlow<ViewState>(ViewState.Waiting)
    val viewState = _viewState.asStateFlow()


    fun validate() {
        form.validate(true)
        Log.d("FoodCreateUpdateViewModel", "Validate (form is valid: ${form.isValid})")
    }


    internal fun dateToLocalDate(date: Date): LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    suspend fun save(){
        form.validate(true)
        Log.d(TAG, "Form is valid : ${form.isValid}")
        if (form.isValid){
            Log.d(TAG, "Before getRawValues")
            val values = form.getRawValues()
            Log.d(TAG, "After getRawValues : $values")


            val foodDto = FoodDto(
                name = values["name"] as String,
                expiryDate = dateToLocalDate(values["expiryDate"] as Date),
                brand = if (values["brand"] == null) null else values["brand"] as String,
                category = if (values["category"] == null) null else values["category"] as String,
                datePurchased = if (values["datePurchased"] == null) null
                        else dateToLocalDate(values["datePurchased"] as Date),
                comment = if (values["comment"] == null) null else values["comment"] as String,
                amount = if (values["amount"] == null) null else values["amount"] as Int
            )
            Log.d(TAG, "Enregistrement de l'aliment : $foodDto")

            try {
                repository.create(foodDto)
                _viewState.update { ViewState.Modify(foodDto) }
                Log.i(TAG, "Aliment enregistré avec succès : id=${foodDto}")
            } catch (e: Exception) {
                Log.e(TAG, "Échec de l'enregistrement de l'aliment", e)
                _viewState.update { ViewState.Failure }
            }
        }
        else{
            _viewState.update { ViewState.Failure }
        }
    }
}