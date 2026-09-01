package com.vlt.zeroperte.ui

import com.vlt.zeroperte.data.FakeFoodRepository
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.ui.ViewModel.FoodCreateUpdateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.GregorianCalendar

@OptIn(ExperimentalCoroutinesApi::class)
class FoodCreateUpdateViewModelTest {

    private lateinit var repository: FakeFoodRepository
    private lateinit var viewModel: FoodCreateUpdateViewModel

    @Before
    fun setup() {
        repository = FakeFoodRepository()
        viewModel = FoodCreateUpdateViewModel(repository)
    }

    private fun sampleFood(
        id: Long = 1L,
        name: String = "Yaourt",
        expiryDate: LocalDate = LocalDate.now().plusDays(30)
    ) = Food(
        id = id,
        name = name,
        brand = null,
        category = null,
        amount = 1,
        datePurchased = LocalDate.now().minusDays(2),
        expiryDate = expiryDate,
        comment = null
    )

    @Test
    fun `initial state is Waiting`() {
        assertTrue(viewModel.viewState.value is FoodCreateUpdateViewModel.ViewState.Waiting)
    }

    @Test
    fun `dateToLocalDate converts a Date to the matching LocalDate`() {
        val date = GregorianCalendar(2026, 8, 1).time // month is 0-based: 8 = September

        val localDate = viewModel.dateToLocalDate(date)

        assertEquals(LocalDate.of(2026, 9, 1), localDate)
    }

    @Test
    fun `dateToLocalDate is the inverse of converting a LocalDate to a Date at the start of day`() {
        val localDate = LocalDate.of(2025, 12, 31)
        val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        assertEquals(localDate, viewModel.dateToLocalDate(date))
    }

    @Test
    fun `fetchFood with an existing id emits Update with the matching food`() = runTest {
        repository.setFoods(listOf(sampleFood(id = 1L, name = "Lait")))

        viewModel.fetchFood(1L)

        val state = viewModel.viewState.value
        assertTrue(state is FoodCreateUpdateViewModel.ViewState.Update)
        val update = state as FoodCreateUpdateViewModel.ViewState.Update
        assertEquals("Lait", update.resource.name)
        assertEquals(1L, update.resource.id)
    }

    @Test
    fun `fetchFood with an unknown id emits Failure`() = runTest {
        repository.setFoods(listOf(sampleFood(id = 1L)))

        viewModel.fetchFood(999L)

        assertTrue(viewModel.viewState.value is FoodCreateUpdateViewModel.ViewState.Failure)
    }
}
