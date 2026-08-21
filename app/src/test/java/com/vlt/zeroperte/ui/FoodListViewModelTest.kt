package com.vlt.zeroperte.ui

import com.vlt.zeroperte.data.FakeFoodRepository
import com.vlt.zeroperte.data.model.Food
import com.vlt.zeroperte.data.model.domain.FoodStatus
import com.vlt.zeroperte.ui.ViewModel.FoodListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class FoodListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeFoodRepository
    private lateinit var viewModel: FoodListViewModel

    @Before
    fun setup() {
        repository = FakeFoodRepository()
        viewModel = FoodListViewModel(repository)
    }

    private fun sampleFood(
        name: String = "Yaourt",
        brand: String? = "Danone",
        category: String = "frais",
        amount: Int = 4,
        datePurchased: LocalDate = LocalDate.now().minusDays(2),
        expiryDate: LocalDate = LocalDate.now().plusDays(30),
        comment: String? = null
    ) = Food(
        name = name,
        brand = brand,
        category = category,
        amount = amount,
        datePurchased = datePurchased,
        expiryDate = expiryDate,
        comment = comment
    )

    @Test
    fun `empty repository eventually emits Empty state`() = runTest {
        val states = mutableListOf<FoodListViewModel.FoodListUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        // Premier item collecté : la valeur initiale du stateIn
        assertEquals(FoodListViewModel.FoodListUiState.Loading, states[0])
        // Deuxième item : le vrai résultat calculé par combine()
        assertTrue(states[1] is FoodListViewModel.FoodListUiState.Empty)

        job.cancel()
    }

    @Test
    fun `foods without filter emits Content with all foods`() = runTest {
        repository.setFoods(listOf(sampleFood(name = "Lait"), sampleFood(name = "Riz")))

        val states = mutableListOf<FoodListViewModel.FoodListUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        val state = states.last()
        assertTrue(state is FoodListViewModel.FoodListUiState.Content)
        val content = state as FoodListViewModel.FoodListUiState.Content
        assertEquals(2, content.foods.size)
        assertNull(content.filter.selectedStatus)

        job.cancel()
    }

    @Test
    fun `filter excluding all foods emits Empty with filter preserved`() = runTest {
        // Tous les aliments sont OK (expiryDate loin dans le futur)
        repository.setFoods(listOf(sampleFood(expiryDate = LocalDate.now().plusDays(30))))

        viewModel.toggleStatus(FoodStatus.Expired)

        val states = mutableListOf<FoodListViewModel.FoodListUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        val state = states.last()
        assertTrue(state is FoodListViewModel.FoodListUiState.Empty)
        val empty = state as FoodListViewModel.FoodListUiState.Empty
        assertEquals(FoodStatus.Expired, empty.filter.selectedStatus)

        job.cancel()
    }

    @Test
    fun `filter matching some foods emits Content with only matching foods`() = runTest {
        repository.setFoods(
            listOf(
                sampleFood(name = "Perime", expiryDate = LocalDate.now().minusDays(3)),
                sampleFood(name = "Valide", expiryDate = LocalDate.now().plusDays(30))
            )
        )

        viewModel.toggleStatus(FoodStatus.Expired)

        val states = mutableListOf<FoodListViewModel.FoodListUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        val state = states.last()
        assertTrue(state is FoodListViewModel.FoodListUiState.Content)
        val content = state as FoodListViewModel.FoodListUiState.Content
        assertEquals(1, content.foods.size)

        job.cancel()
    }

    @Test
    fun `toggleStatus twice with same status resets selection to null`() = runTest {
        val filterStates = mutableListOf<FoodListViewModel.FilterState>()
        val job = launch { viewModel.filterUiState.collect { filterStates.add(it) } }

        advanceUntilIdle()
        assertNull(filterStates[0].selectedStatus)

        viewModel.toggleStatus(FoodStatus.Expired)
        advanceUntilIdle()
        assertEquals(FoodStatus.Expired, filterStates.last().selectedStatus)

        viewModel.toggleStatus(FoodStatus.Expired)
        advanceUntilIdle()
        assertNull(filterStates.last().selectedStatus)

        job.cancel()
    }

    @Test
    fun `toggleStatus with different statuses replaces selection`() = runTest {
        val filterStates = mutableListOf<FoodListViewModel.FilterState>()
        val job = launch { viewModel.filterUiState.collect { filterStates.add(it) } }

        advanceUntilIdle()

        viewModel.toggleStatus(FoodStatus.Expired)
        advanceUntilIdle()
        assertEquals(FoodStatus.Expired, filterStates.last().selectedStatus)

        viewModel.toggleStatus(FoodStatus.ExpiringSoon)
        advanceUntilIdle()
        assertEquals(FoodStatus.ExpiringSoon, filterStates.last().selectedStatus)

        job.cancel()
    }
}