package net.miyataroid.miyatamagrimoire.view

import android.app.Activity
import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.ar.core.Config
import net.miyataroid.miyatamagrimoire.MainActivity
import com.google.ar.core.Session
import net.miyataroid.miyatamagrimoire.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun GrimoireViewScreen(
    modifier: Modifier = Modifier,
    viewModel: GrimoireViewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

   DisposableEffect(lifecycleOwner) {
       val observer = LifecycleEventObserver {_, event ->
           when(event) {
               Lifecycle.Event.ON_START -> {
               }
               Lifecycle.Event.ON_RESUME -> {}
               else -> {}
           }
       }

       lifecycleOwner.lifecycle.addObserver(observer)
       viewModel.setArCoreSessionLifecycleObserver(lifecycleOwner.lifecycle)

       onDispose {
           lifecycleOwner.lifecycle.removeObserver(observer)
       }
   }

    GrimoireViewScreenContent(
        uiState = uiState ,
        modifier = modifier,
    )

    if (uiState.session != null &&
        uiState.session!!.isDepthModeSupported(Config.DepthMode.AUTOMATIC)  &&
        uiState.shouldShowDepthEnableDialog
    ) {
        NeedDepthDialog(
            onClickPositive = {
                viewModel.setUseDepthForOcclusion(true)
            },
            onClickNegative = {
                viewModel.setUseDepthForOcclusion(false)
            },
        )
    }
}

@Composable
private fun GrimoireViewScreenContent(
    uiState: GrimoireViewUiState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                GLSurfaceView(context)
            },
            update = { surfaceView->

            },
            modifier = Modifier
                .fillMaxSize()
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "戻る")
            }
        }
    }
}

@Composable
private fun NeedDepthDialog(
    onClickPositive: () -> Unit,
    onClickNegative : () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(R.string.options_title_with_depth),
        message = stringResource(id = R.string.depth_use_explanation),
        positiveText = stringResource(id = R.string.button_text_enable_depth),
        negativeText = stringResource(id = R.string.button_text_disable_depth),
        onClickPositive = onClickPositive ,
        onClickNegative = onClickNegative ,
    )
}
