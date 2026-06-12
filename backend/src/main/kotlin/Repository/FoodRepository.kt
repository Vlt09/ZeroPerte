package com.zeroperte.Repository

import io.ktor.util.logging.*
import com.zeroperte.model.Food
import com.zeroperte.model.FoodDto
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.*
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

internal val LOGGER = KtorSimpleLogger("com.zeroperte.FoodRepositoryLogger")



internal object FoodTable : LongIdTable() {
    val name: Column<String> = varchar("food", 100)
    val brand: Column<String?> = varchar("brand", 30).nullable()
    val category: Column<String> = varchar("category", 30)
    val datePurchased: Column<LocalDateTime> = datetime("date_purchased")
    val expiryDate: Column<LocalDateTime> = datetime("expiry_date")
    val comment: Column<String?> = text("comment").nullable()
    val amount: Column<Int> = integer("amount")

    fun toDomain(row: ResultRow): Food {
        return Food(
            id = row[id].value,
            name = row[name],
            brand = row[brand],
            category = row[category],
            datePurchased = row[datePurchased],
            expiryDate = row[expiryDate],
            comment = row[comment],
            amount = row[amount]
        )
    }}

class FoodRepository {
    private fun sampleFood(name: String = "Yaourt") = Food(
        id = 0,
        name = name,
        brand = "Danone",
        category = "Produit laitier",
        datePurchased = LocalDateTime(2026, 6, 1, 10, 0),
        expiryDate = LocalDateTime(2026, 6, 15, 0, 0),
        comment = "A consommer rapidement",
        amount = 4
    )

    val foodsList = mutableListOf<Food>(
        sampleFood("Test1"),
        sampleFood("Test2"),
        sampleFood("Test3"),
        sampleFood("Test4"),
        sampleFood("Test5")
    )

    init {
        LOGGER.info("init FoodRepository");
        transaction {
            SchemaUtils.create(FoodTable)
        }
    }

    fun findById(id: Long): Food? {
        return transaction {
            FoodTable.selectAll().where { FoodTable.id eq id }
                .map { FoodTable.toDomain(it) }
                .singleOrNull()
        }
    }

    fun allFoods(): List<Food> {
        return transaction {
            FoodTable.selectAll().map { FoodTable.toDomain(it) }.toList()
        }
    }

    fun create(food: Food): Long {
        return transaction {
            FoodTable.insertAndGetId { row ->
                row[name] = food.name
                row[brand] = food.brand
                row[category] = food.category
                row[datePurchased] = food.datePurchased
                row[expiryDate] = food.expiryDate
                row[comment] = food.comment
                row[amount] = food.amount
            }.value
        }
    }

    fun update(id: Long, foodDto: FoodDto): Food? {
        val updateRow = transaction {
                            FoodTable.update ({ FoodTable.id eq id}) { row ->
                                if (foodDto.name != null) row[name] = foodDto.name
                                if (foodDto.brand != null) row[brand] = foodDto.brand
                                if (foodDto.category != null) row[category] = foodDto.category
                                if (foodDto.comment != null) row[comment] = foodDto.comment
                                if (foodDto.amount != null) row[amount] = foodDto.amount
                            }
                        }
        LOGGER.info("row updated $updateRow")
        return findById(id);
    }

    fun delete(id: Long): Int {
        val deletedRow = transaction {
            FoodTable.deleteWhere { FoodTable.id eq id }
        }
        LOGGER.info("Id $id removed and $deletedRow row has been deleted")
        return deletedRow
    }

}


