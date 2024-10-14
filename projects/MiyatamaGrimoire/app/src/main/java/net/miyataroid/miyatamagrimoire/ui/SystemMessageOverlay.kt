package net.miyataroid.miyatamagrimoire.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme

@Composable
fun SystemMessageOverlay(
    modifier: Modifier = Modifier,
) {
    // P5風エフェクト -> システムメッセージ -> タップイベントでアプリ再起動
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

    }
}

@Preview
@Composable
fun PreviewSystemMessageOverlay(){
    MiyatamaGrimoireTheme {
        SystemMessageOverlay()
    }
}