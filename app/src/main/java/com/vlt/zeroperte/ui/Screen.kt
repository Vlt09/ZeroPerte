package com.vlt.zeroperte.ui

import kotlinx.serialization.Serializable

@Serializable
object FoodList

@Serializable
object Parameters

@Serializable
data class FoodDetail(val foodId: Long? = null)

@Serializable
data class FoodCreateUpdate(val foodId: Long? = null)