package net.miyataroid.miyatamagrimoire.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * ローディングの処理など共通処理
 */
@Composable
fun BaseScreen(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content:@Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        content()
    }
}