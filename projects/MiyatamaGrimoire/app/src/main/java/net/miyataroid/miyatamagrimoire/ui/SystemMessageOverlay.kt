@file:Suppress("UNUSED_EXPRESSION")

package net.miyataroid.miyatamagrimoire.ui

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.load
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import coil3.video.VideoFrameDecoder
import kotlinx.coroutines.delay
import net.miyataroid.miyatamagrimoire.R
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme
import net.miyataroid.miyatamagrimoire.ui.theme.TextEnable

@Composable
fun SystemMessageOverlay(
    message: String,
    onClickOk: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var progress by remember { mutableStateOf(SystemMessageProgress.START_EFFECT) }
    // P5風エフェクト -> システムメッセージ -> タップイベントでアプリ再起動
    SystemMessageOverlayContent(
        message = message,
        state = progress,
        onClickOk = onClickOk,
        onFinishState = {
            if (progress == SystemMessageProgress.START_EFFECT) {
                progress = SystemMessageProgress.SHOW_MESSAGE
            }
        }
    )
}

@Composable
private fun SystemMessageOverlayContent(
    message: String,
    state: SystemMessageProgress,
    onClickOk: () -> Unit,
    onFinishState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color(0x3A000000)
            )
    ) {
        when (state) {
            SystemMessageProgress.START_EFFECT -> {
                // P5風エフェクト
                SystemCutInEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
                LaunchedEffect(Unit) {
                    delay(1000)
                    onFinishState()
                }
            }

            SystemMessageProgress.SHOW_MESSAGE -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.system_message_background_base),
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Image(
                        painter = painterResource(id = R.drawable.system_message_avatar),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(
                                Alignment.BottomStart
                            )
                            .padding(start = 12.dp, bottom = 8.dp),
                    )
                    Text(
                        text = message,
                        color = TextEnable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(top = 50.dp, start = 80.dp, end = 80.dp)
                            .align(Alignment.Center)
                    )

                    SmallButton(
                        selected = true,
                        text = "OK",
                        onClick = {
                            onClickOk()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .width(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemCutInEffect(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val cutInUrl= Uri.parse("android.resource://" + context.packageName + "/" + R.raw.system_cutin)
   AndroidView(
       modifier = modifier
           .background(color = Color(0x00000000)),
       factory = {
           val videoView = VideoView(it)
           // videoView.setVideoPath(effectFilePath)
           videoView.setVideoURI(cutInUrl)
           videoView.setBackgroundColor(context.getColor(R.color.effect_background))
           videoView.setOnPreparedListener { mediaPlayer ->
               val videoWidth = mediaPlayer.videoWidth
               val videoHeight = mediaPlayer.videoHeight
                   if (videoWidth > 0) {
                       videoHeight.toFloat() / videoWidth.toFloat()
                   } else 0f
           }
           videoView.start()
           videoView
       },
       update = {
       }
   )
}

enum class SystemMessageProgress {
    START_EFFECT,
    SHOW_MESSAGE,
}

@Preview
@Composable
fun PreviewSystemMessageOverlay() {
    MiyatamaGrimoireTheme {
        SystemMessageOverlayContent(
            message = "ABCDEFGHIJKLMNOPQRSTUVWXYZこれはシステムメッセージや！",
            state = SystemMessageProgress.SHOW_MESSAGE,
            onClickOk = {},
            onFinishState = {},
        )
    }
}