package com.vlt.zeroperte.data.model.domain

sealed class FoodStatus(val displayName : String) {
    object Expired: FoodStatus("Expired")
    object ExpiringSoon: FoodStatus("Expiring Soon")
    object Edible: FoodStatus("Edible")

    companion object {
        val allStatuses = listOf<FoodStatus>(Edible, ExpiringSoon, Expired)
    }
}
