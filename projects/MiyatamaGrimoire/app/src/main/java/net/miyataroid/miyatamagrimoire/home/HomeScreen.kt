package net.miyataroid.miyatamagrimoire.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme

@Composable
fun HomeScreen(
    navigateToGrimoreEdit: () -> Unit,
    navigateToGrimoreView: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onSelectMode =  {
            viewModel.select(it)
        },
        onClickNext =  {
            when(uiState.selectedMode) {
                HomeUiState.SelectedMode.VIEW -> {
                    navigateToGrimoreView()
                }
                HomeUiState.SelectedMode.EDIT -> {
                    navigateToGrimoreEdit()
                }
            }
        }
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onSelectMode: (HomeUiState.SelectedMode) -> Unit,
    onClickNext: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            ModeSelectButton(
                selected = false,
                text = "魔導書を見る",
                onClick = {
                    onSelectMode(HomeUiState.SelectedMode.VIEW)
                },
            )

            ModeSelectButton(
                selected = false,
                text = "魔導書を作る",
                onClick = {
                    onSelectMode(HomeUiState.SelectedMode.EDIT)
                },
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Button(
                onClick = onClickNext,
            ) {
                Text(
                    text = "次へ"
                )
            }
        }
    }
}

@Composable
private fun ModeSelectButton(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = text
        )
    }
}

@Preview
@Composable
private fun PreviewHomeScreenContent() {
    MiyatamaGrimoireTheme {
            HomeScreenContent(
                uiState = HomeUiState(),
                onSelectMode = {},
                onClickNext = {  }
            )
    }
}