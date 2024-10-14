package net.miyataroid.miyatamagrimoire.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.miyataroid.miyatamagrimoire.ui.LargeButton
import net.miyataroid.miyatamagrimoire.R
import net.miyataroid.miyatamagrimoire.ui.SmallButton
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navigateToGrimoreEdit: () -> Unit,
    navigateToGrimoreView: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
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
    val backgroundColor = when(uiState.selectedMode) {
        HomeUiState.SelectedMode.VIEW -> Color(0xFF4b68bc)
        HomeUiState.SelectedMode.EDIT -> Color(0xFF4abf4c)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        val imageRes =when(uiState.selectedMode) {
            HomeUiState.SelectedMode.VIEW -> R.drawable.grimoire_view_image
            HomeUiState.SelectedMode.EDIT -> R.drawable.grimoire_edit_image
        }
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            LargeButton(
                selected = uiState.selectedMode == HomeUiState.SelectedMode.VIEW,
                text = "魔導書を見る",
                onClick = {
                    onSelectMode(HomeUiState.SelectedMode.VIEW)
                },
            )

            LargeButton(
                selected = uiState.selectedMode == HomeUiState.SelectedMode.EDIT,
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

            SmallButton(
                selected = true,
                text = "Next",
                onClick = onClickNext,
                modifier = Modifier
                    .width(130.dp)
            )
        }
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
