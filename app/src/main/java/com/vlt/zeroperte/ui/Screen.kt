package com.vlt.zeroperte.ui

sealed class Screen(val route: String) {
    data object FoodList: Screen(route = "foodList")
    data object FoodDetail: Screen(route = "foodDetail/{foodId}")
    data object FoodCreateUpdate: Screen(route = "foodCreateUpdate/{foodId}")
    data object Parameters : Screen(route = "parameters")
}