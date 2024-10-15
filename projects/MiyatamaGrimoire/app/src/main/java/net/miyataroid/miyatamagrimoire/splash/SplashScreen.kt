package net.miyataroid.miyatamagrimoire.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.splashProcessState == SplashUiState.SplashProcessState.FINISH) {
        navigateToHome()
    } else {
        SplashScreenContent(
            uiState = uiState,
            modifier = modifier,
        )
    }
}

@Composable
fun SplashScreenContent(
    uiState: SplashUiState,
    modifier: Modifier =Modifier,
){
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

    }
}