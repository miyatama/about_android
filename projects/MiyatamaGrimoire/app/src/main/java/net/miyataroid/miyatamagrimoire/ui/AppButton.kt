package net.miyataroid.miyatamagrimoire.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.miyataroid.miyatamagrimoire.R
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme

@Composable
fun ButtonBase(
    selected: Boolean,
    text: String,
    fontSize: TextUnit,
    height: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier
        .height(height)
        .padding(2.dp)
        .clickable { onClick() }
    ) {
        val imageRes = if(selected) {
            R.drawable.button_background
        } else {
            R.drawable.button_background_disabled
        }
        Image(
            painter = painterResource(id = imageRes ),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize(),
        )

        val textColor = if(selected) {
            Color(0xFFFFFFFF)
        } else {
            Color(0xFFA3A3A3)
        }
        Text(
            fontSize = fontSize,
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }

}

@Composable
fun LargeButton(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 30.sp,
) {
    ButtonBase(
        selected = selected,
        text = text,
        fontSize = fontSize,
        height = 80.dp,
        onClick = onClick,
        modifier = modifier,
    )
}
@Composable
fun SmallButton(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 8.sp,
) {
    ButtonBase(
        selected = selected,
        text = text,
        height = 30.dp,
        fontSize = fontSize,
        onClick = onClick,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewModeSelectButton() {
    MiyatamaGrimoireTheme {
        Column() {
            LargeButton(
                selected = true,
                text = "魔導書を見る",
                onClick = {},
            )
            LargeButton(
                selected = false,
                text = "魔導書を見る",
                onClick = {},
            )
            SmallButton(
                selected = true,
                text = "魔導書を見る",
                onClick = {},
            )
            SmallButton(
                selected = false,
                text = "魔導書を見る",
                onClick = {},
            )
        }
    }
}
