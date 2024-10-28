package net.miyataroid.miyatamagrimoire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme

@Composable
fun ToastMessage(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) = if (isError) {
        Pair(
            Color(0xFFd5d402),
            Color(0xFF000000),
        )
    } else {
        Pair(
            Color(0xA6000000),
            Color(0xfff2f2f2),
        )
    }
    Box(
        modifier = modifier
            .background(color = backgroundColor)
            .fillMaxWidth()
    ) {
       Text(
           text = message,
           color = textColor
       )
    }
}

@Preview
@Composable
private fun ToastMessagePreview() {
    MiyatamaGrimoireTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToastMessage(
                message = "Warning Text",
                isError = false,
            )
            ToastMessage(
                message = "Warning Text",
                isError = true,
            )
        }
    }
}