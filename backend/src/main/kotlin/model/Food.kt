package com.zeroperte.model

import kotlinx.datetime.LocalDateTime
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

data class Food(val id: Long, val name: String, val brand: String?, val category: String,
                val datePurchased: LocalDateTime, val expiryDate: LocalDateTime, val comment: String?, val amount: Int) {
    companion object {
        fun getMemberPropertiesString() : List<String> {
            return Food::class.declaredMemberProperties.map { p -> p.name }.toList()
        }
    }
};



fun Food.foodAsRow() = """
    <tr>
        <td>$name</td><td>$brand</td><td>$category</td>
        <td>$datePurchased</td>
    </tr>
""".trimIndent()


fun List<Food>.foodAsTable() = this.joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
    transform = Food::foodAsRow
)

