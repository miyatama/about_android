package net.miyataroid.miyatamagrimoire.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.miyataroid.miyatamagrimoire.R
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFf0729b)
            ),
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar_translate),
            contentScale = ContentScale.Fit,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    MiyatamaGrimoireTheme {
            LoadingScreen()
    }
}
