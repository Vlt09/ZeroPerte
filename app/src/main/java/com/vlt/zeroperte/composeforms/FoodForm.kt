package com.vlt.zeroperte.composeforms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.FormField
import ch.benlu.composeform.validators.NotEmptyValidator
import ch.benlu.composeform.Validator
import ch.benlu.composeform.fields.PickerValue
import com.vlt.zeroperte.data.model.FoodCategory
import java.util.Date


data class FoodGroup(
    val category: FoodCategory,
    val name: String
): PickerValue() {
    override fun searchFilter(query: String): Boolean {
        return this.name.startsWith(query)
    }
}
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
        state = mutableStateOf<FoodGroup?>(null),
        options = mutableListOf(
            FoodGroup(FoodCategory.FRUIT_VEGETABLE, "Fruits et légumes"),
            FoodGroup(FoodCategory.STARCHY, "Féculents"),
            FoodGroup(FoodCategory.LEGUME, "Légumineuses"),
            FoodGroup(FoodCategory.DAIRY, "Produits laitiers"),
            FoodGroup(FoodCategory.FAT_OIL, "Matière grasse"),
            FoodGroup(FoodCategory.MEAT, "Viande"),
            FoodGroup(FoodCategory.FISH_SEAFOOD, "Poisson et fruits de mer"),
            FoodGroup(FoodCategory.SUGARY, "Sucreries"),
            FoodGroup(FoodCategory.DRINK, "Boisson"),
            FoodGroup(FoodCategory.DRINK, "Eau")
        ),
        optionItemFormatter = { "${it?.name}" }
    )
}