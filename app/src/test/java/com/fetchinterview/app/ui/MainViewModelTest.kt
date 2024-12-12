package com.fetchinterview.app.ui

import com.fetchinterview.app.data.Candidate
import com.fetchinterview.app.data.HiringService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class MainViewModelTest {

    private val hiringService = mockk<HiringService>()

    private val response = mockk<Response<List<Candidate>>>()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        every { response.isSuccessful } returns false
        coEvery { hiringService.fetchHiringCandidatesFromServer() } returns response

        viewModel = MainViewModel(hiringService)
    }

    @Test
    fun testEmptyList() {
        // GIVEN
        val input = listOf<Candidate>()
        val expected = listOf<List<Candidate>>()

        // WHEN
        val actual = viewModel.filterAndSortResponse(input)

        // THEN
        assertEquals(expected, actual)
    }

    @Test
    fun testShouldGroupByListId() {
        // GIVEN
        val input = listOf(
            generateExampleCandidate(num = 3),
            generateExampleCandidate(num = 1),
            generateExampleCandidate(num = 3),
            generateExampleCandidate(num = 2)
        )
        val expected = listOf(
            listOf(generateExampleCandidate(num = 1)),
            listOf(generateExampleCandidate(num = 2)),
            listOf(
                generateExampleCandidate(num = 3),
                generateExampleCandidate(num = 3)
            )
        )

        // WHEN
        val actual = viewModel.filterAndSortResponse(input)

        // THEN
        assertEquals(expected, actual)
    }

    @Test
    fun testShouldFilterOutEmptyAndNullNames() {
        // GIVEN
        val input = listOf(
            generateExampleCandidate(num = 3).copy(name = null),
            generateExampleCandidate(num = 1).copy(name = ""),
            generateExampleCandidate(num = 2)
        )
        val expected = listOf(
            listOf(generateExampleCandidate(num = 2))
        )

        // WHEN
        val actual = viewModel.filterAndSortResponse(input)

        // THEN
        assertEquals(expected, actual)
    }

    @Test
    fun testShouldOrderByNameInListIdGroup() {
        // GIVEN
        val input = listOf(
            generateExampleCandidate(num = 3).copy(name = "Item 300"),
            generateExampleCandidate(num = 1),
            generateExampleCandidate(num = 3),
            generateExampleCandidate(num = 2)
        )
        val expected = listOf(
            listOf(generateExampleCandidate(num = 1)),
            listOf(generateExampleCandidate(num = 2)),
            listOf(
                generateExampleCandidate(num = 3), // name = Item 3 should be first
                generateExampleCandidate(num = 3).copy(name = "Item 300")
            )
        )

        // WHEN
        val actual = viewModel.filterAndSortResponse(input)

        // THEN
        assertEquals(expected, actual)
    }

    private fun generateExampleCandidate(num: Int) = Candidate(
        id = num,
        listId = num,
        name = "Item $num"
    )
}