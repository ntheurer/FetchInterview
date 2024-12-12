package com.fetchinterview.app.ui

import com.fetchinterview.app.data.Candidate

data class MainUiState(
    val pageState: PageState = PageState.LOADING,
    val candidates: List<List<Candidate>> = emptyList(),
    val errorMessage: String = ""
)

enum class PageState {
    LOADING,
    FAILED,
    SUCCESS
}
