package net.miyataroid.miyatamagrimoire.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    navigateToGrimoreEdit: () -> Unit,
    navigateToGrimoreView: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Button(
                onClick = {
                    viewModel.select(HomeUiState.SelectedMode.VIEW)
                }
            ) {
                Text(
                    text = "魔導書を見る"
                )
            }

            Button(
                onClick = {
                    viewModel.select(HomeUiState.SelectedMode.EDIT)
                }
            ) {
                Text(
                    text = "魔導書を作る"
                )
            }
        }

        Button(
            onClick = {
                when(uiState.selectedMode) {
                    HomeUiState.SelectedMode.VIEW -> {
                        navigateToGrimoreView()
                    }
                    HomeUiState.SelectedMode.EDIT -> {
                        navigateToGrimoreEdit()
                    }
                }
            }
        ) {
            Text(
                text = "次へ"
            )
        }
    }
}