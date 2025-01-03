package net.miyataroid.miyatamagrimoire.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import net.miyataroid.miyatamagrimoire.ui.BaseScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun GrimoireEditScreen(
    modifier: Modifier = Modifier,
    viewModel: GrimoireEditViewModel= koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    BaseScreen(isLoading = uiState.isLoading) {
        GrimoireEditContent()
    }
}

@Composable
fun GrimoireEditContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "魔導書編集画面")
    }
}
