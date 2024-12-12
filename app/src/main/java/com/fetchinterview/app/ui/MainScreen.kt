package com.fetchinterview.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fetchinterview.app.data.Candidate
import com.fetchinterview.app.ui.theme.FetchInterviewTheme
import com.fetchinterview.app.ui.theme.FetchOrange
import com.fetchinterview.app.ui.theme.FetchPurple

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ) {
            uiState.candidates.forEach { candidateGroup ->
                candidateGroup.firstOrNull()?.let {
                    // If the group is not empty, show a title and the list of candidates in the group
                    item {
                        Text(
                            text = "List ${it.listId}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp
                                ),
                            style = TextStyle(
                                color = FetchPurple,
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        HorizontalDivider(modifier = Modifier.height(1.dp))
                    }
                    items(
                        items = candidateGroup,
                        key = { candidate ->
                            candidate.id
                        }
                    ) { candidate ->
                        CandidateProfile(
                            candidate = candidate
                        )
                        HorizontalDivider(modifier = Modifier.height(1.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
        if (uiState.pageState == PageState.LOADING) {
            CircularProgressIndicator(
                color = FetchPurple
            )
        }
        if (uiState.pageState == PageState.FAILED) {
            // Show an error message and retry button
            ErrorState(
                onRetryClick = viewModel::retry
            )
        }
    }
}

@Composable
private fun CandidateProfile(
    candidate: Candidate,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = FetchOrange,
                    shape = CircleShape
                )
        ) {
            // A candidate profile picture can be added here instead of just using an orange circle
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = "Candidate Name: ${candidate.name}"
            )
            Text(
                text = "Candidate id: ${candidate.id}",
                modifier = Modifier
                    .padding(top = 4.dp)
            )
            Text(
                text = "listId: ${candidate.listId}",
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ErrorState(
    onRetryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Error loading results",
            style = TextStyle(
                fontSize = 30.sp
            )
        )
        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = FetchOrange
            ),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = "Retry",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    FetchInterviewTheme {
        MainScreen()
    }
}
