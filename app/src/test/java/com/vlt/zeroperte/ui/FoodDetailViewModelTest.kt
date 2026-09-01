package com.vlt.zeroperte.ui

import com.vlt.zeroperte.data.FakeFoodRepository
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.ui.ViewModel.FoodDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDetailViewModelTest {

    private lateinit var repository: FakeFoodRepository
    private lateinit var viewModel: FoodDetailViewModel

    @Before
    fun setup() {
        repository = FakeFoodRepository()
        viewModel = FoodDetailViewModel(repository)
    }

    private fun sampleFood(
        id: Long = 1L,
        name: String = "Yaourt",
        brand: String? = "Danone",
        category: String = "frais",
        amount: Int = 4,
        datePurchased: LocalDate = LocalDate.now().minusDays(2),
        expiryDate: LocalDate = LocalDate.now().plusDays(30),
        comment: String? = null
    ) = Food(
        id = id,
        name = name,
        brand = brand,
        category = category,
        amount = amount,
        datePurchased = datePurchased,
        expiryDate = expiryDate,
        comment = comment
    )

    @Test
    fun `initial state is Waiting`() {
        assertTrue(viewModel.viewState.value is FoodDetailViewModel.ViewState.Waiting)
    }

    @Test
    fun `fetchFood with an existing id emits Success with the matching food`() = runTest {
        val food = sampleFood(id = 1L, name = "Lait")
        repository.setFoods(listOf(food))

        viewModel.fetchFood(1L)

        val state = viewModel.viewState.value
        assertTrue(state is FoodDetailViewModel.ViewState.Success)
        val success = state as FoodDetailViewModel.ViewState.Success
        assertEquals("Lait", success.foodDto.name)
        assertEquals(1L, success.foodDto.id)
    }

    @Test
    fun `fetchFood with an unknown id emits Failure`() = runTest {
        repository.setFoods(listOf(sampleFood(id = 1L)))

        viewModel.fetchFood(999L)

        assertTrue(viewModel.viewState.value is FoodDetailViewModel.ViewState.Failure)
    }
}
