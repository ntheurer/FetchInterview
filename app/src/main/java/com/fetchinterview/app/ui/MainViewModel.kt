package com.fetchinterview.app.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fetchinterview.app.data.Candidate
import com.fetchinterview.app.data.HiringService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val hiringService: HiringService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCandidates()
    }

    private fun loadCandidates() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = hiringService.fetchHiringCandidatesFromServer()
            when {
                response.isSuccessful && response.body() != null -> {
                    val candidates = filterAndSortResponse(response.body())
                    _uiState.update { state ->
                        state.copy(
                            pageState = PageState.SUCCESS,
                            candidates = candidates
                        )
                    }
                }
                else -> {
                    _uiState.update { state ->
                        state.copy(
                            pageState = PageState.FAILED
                        )
                    }
                }
            }
        }
    }

    @VisibleForTesting
    fun filterAndSortResponse(candidates: List<Candidate>?): List<List<Candidate>> {
        val sortedCandidates = mapCandidatesByListId(candidates = candidates)
        val listIds = sortedCandidates.keys.sortedBy { it }
        return listIds.map { listId ->
            sortedCandidates[listId].orEmpty().sortedWith(
                // Inside the group of common listIds, sort by candidate name
                compareBy<Candidate> {
                    // Sorting by length will ensure that "Item 28" comes before
                    // "Item 276" but assumes that a contract will be followed
                    // that all names are "Item {number}". If this is not the
                    // contract then a more robust solution can be implemented
                    // using regex or other checks
                    // Note: If "Item 28" can come after "Item 276" (rules of
                    // just sorting by name alphabetically) then this compare
                    // can be removed
                    it.name?.length
                }.thenBy {
                    it.name
                }
            )
        }
    }

    private fun mapCandidatesByListId(candidates: List<Candidate>?): Map<Int, List<Candidate>> {
        return candidates?.let {
            // First filter out any items where "name" is blank or null
            val filteredList = candidates.filter { !it.name.isNullOrBlank() }.toMutableList()
            val mapByListId = mutableMapOf<Int, List<Candidate>>()
            filteredList.forEach { candidate ->
                val candidatesForListId = mapByListId[candidate.listId]?.toMutableList() ?: mutableListOf()
                candidatesForListId.add(candidate)
                mapByListId[candidate.listId] = candidatesForListId
            }
            mapByListId
        } ?: emptyMap()
    }

    fun retry() {
        loadCandidates()
    }
}
