package com.vlt.zeroperte.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.vlt.zeroperte.data.FoodRepository
import com.vlt.zeroperte.data.model.FoodDto
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FoodCreateUpdateViewModel @Inject constructor(private val repository: FoodRepository) : ViewModel() {
    var form = FoodForm()

    fun validate() {
        form.validate(true)
        Log.d("FoodCreateUpdateViewModel", "Validate (form is valid: ${form.isValid})")
    }

    fun dateToLocalDate(date: Date): LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    /*Faire un état qui indique l'état de l'enregistrement*/

    suspend fun save(){
        form.validate(markAsChanged = true)
        if (form.isValid){
            val values = form.getRawValues()

            val foodDto = FoodDto(
                name = values["name"] as String,
                expiryDate = dateToLocalDate(values["expiryDate"] as Date),
                brand = values["brand"] as String,
                category = values["category"] as String,
                datePurchased = dateToLocalDate(values["datePurchased"] as Date),
                comment = values["comment"] as String,
                amount = values["amount"] as Int
            )

            repository.create(foodDto)
        }
    }
}