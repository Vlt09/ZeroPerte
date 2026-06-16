package com.zeroperte.Repository

import io.ktor.util.logging.*
import com.zeroperte.model.Food
import com.zeroperte.model.FoodDto
import com.zeroperte.model.FoodPostDto
import kotlinx.coroutines.yield
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.datetime.*
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.reflect.full.memberProperties

internal val LOGGER = KtorSimpleLogger("com.zeroperte.FoodRepositoryLogger")



internal object FoodTable : LongIdTable() {
    val name: Column<String> = varchar("food", 100)
    val brand: Column<String?> = varchar("brand", 30).nullable()
    val category: Column<String?> = varchar("category", 30).nullable()
    val datePurchased: Column<LocalDate?> = date("date_purchased").nullable()
    val expiryDate: Column<LocalDate> = date("expiry_date")
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

    fun findByName(name: String): List<Food> {
        return transaction {
            FoodTable.selectAll().where{ FoodTable.name eq name }
                .map { FoodTable.toDomain(it) }
            .toList()
        }
    }

    fun findByCategory(category: String): List<Food> {
        return transaction {
            FoodTable.selectAll().where{
                FoodTable.category eq category}
                .map { FoodTable.toDomain(it) }
                .toList()
        }
    }

    fun findByBrand(brand: String): List<Food> {
        return transaction {
            FoodTable.selectAll().where{
                FoodTable.brand eq brand}
                .map { FoodTable.toDomain(it) }
                .toList()
        }
    }

    fun allFoods(): List<Food> {
        return transaction {
            FoodTable.selectAll().map { FoodTable.toDomain(it) }.toList()
        }
    }

    fun create(foodPostDto: FoodPostDto): Long {
        return transaction {
            FoodTable.insertAndGetId { row ->
                row[name] = foodPostDto.name
                row[brand] = foodPostDto.brand
                row[category] = foodPostDto.category
                row[datePurchased] = foodPostDto.datePurchased
                row[expiryDate] = foodPostDto.expiryDate
                row[comment] = foodPostDto.comment
                row[amount] = foodPostDto.amount
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


