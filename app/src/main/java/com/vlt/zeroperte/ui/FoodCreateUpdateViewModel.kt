package com.vlt.zeroperte.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class FoodCreateUpdateViewModel @Inject constructor() : ViewModel() {

    var form = FoodForm()

    fun validate() {
        form.validate(true)
        Log.d("FoodCreateUpdateViewModel", "Validate (form is valid: ${form.isValid})")
    }
}