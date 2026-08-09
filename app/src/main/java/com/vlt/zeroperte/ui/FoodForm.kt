package com.vlt.zeroperte.ui

import androidx.compose.runtime.mutableStateOf
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.FormField
import ch.benlu.composeform.validators.NotEmptyValidator
import ch.benlu.composeform.Validator
import java.util.Date

class FoodForm : Form() {


    class PositiveValueValidator(errorText: String? = null) : Validator<String?>(
        validate = { value ->
            value == null || value.matches(Regex("[0-9]{1,15}"))
        },
        errorText = errorText ?: "Seulement nombre positif."
    )

    @FormField
    val name = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(NotEmptyValidator())
    )

    @FormField
    val expiryDate = FieldState(
        state = mutableStateOf<Date?>(null), // Use Date instead of LocalDate bc DateField method
        validators = mutableListOf(NotEmptyValidator())
    )

    @FormField
    val datePurchased = FieldState(
        state = mutableStateOf<Date?>(null)
    )

    @FormField
    val amount = FieldState(
        state = mutableStateOf<String?>("1"),
        validators = mutableListOf(PositiveValueValidator())
    )

    @FormField
    val brand = FieldState(
        state = mutableStateOf<String?>(null)
    )

    @FormField
    val comment = FieldState(
        state = mutableStateOf<String?>(null)
    )

    @FormField
    val category = FieldState(
        state = mutableStateOf<String?>(null)
    )

}