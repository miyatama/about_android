package net.miyataroid.miyatamagrimoire.view

import android.app.Activity
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.widget.Toast
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
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import net.miyataroid.miyatamagrimoire.MainActivity
import com.google.ar.core.Session
import net.miyataroid.miyatamagrimoire.R
import net.miyataroid.miyatamagrimoire.core.helpers.CameraPermissionHelper
import net.miyataroid.miyatamagrimoire.ui.BaseScreen
import net.miyataroid.miyatamagrimoire.ui.LargeButton
import net.miyataroid.miyatamagrimoire.ui.SmallButton
import net.miyataroid.miyatamagrimoire.ui.SystemMessageOverlay
import org.koin.androidx.compose.koinViewModel

@Composable
fun GrimoireViewScreen(
    navigateToBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GrimoireViewViewModel = koinViewModel(),
) {
    val activity = LocalContext.current as Activity
    var cameraPermissionState by remember {mutableStateOf(CameraPermissionHelper.hasCameraPermission(activity))}
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                }

                Lifecycle.Event.ON_RESUME -> {
                    cameraPermissionState = CameraPermissionHelper.hasCameraPermission(activity)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        viewModel.setArCoreSessionLifecycleObserver(lifecycleOwner.lifecycle)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    viewModel.setArCoreSessionActivity(activity)
    if (!cameraPermissionState) {
        if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(activity)) {
            SystemMessageOverlay(
                message = "カメラを有効化しないと使えないぞ!",
                onClickOk = {
                    CameraPermissionHelper.launchPermissionSettings(activity)
                }
            )
        } else {
            SystemMessageOverlay(
                message = "この端末ではアプリを使えないぞ!",
                onClickOk = {
                    navigateToBack()
                }
            )
        }
    } else if (ArCoreApk.getInstance()
            .requestInstall(activity, false) == ArCoreApk.InstallStatus.INSTALL_REQUESTED
    ) {
        SystemMessageOverlay(
            message = "ARCoreをインストールしないと使えないぞ！",
            onClickOk = {
                navigateToBack()
            }
        )
    } else {
        val uiState by viewModel.uiState.collectAsState()

        val context = LocalContext.current
        BaseScreen(
            isLoading = uiState.isLoading,
        ) {
            GrimoireViewScreenContent(
                uiState = uiState,
                onLoadingRenderer = {
                    viewModel.generateGrimoireViewRenderer(context as Activity)
                },
                onClickBack = {
                    navigateToBack()
                },
                onSetUseDepthForOcclusion = {
                    viewModel.setUseDepthForOcclusion(it)
                },
                snackBarMessageCallback = {
                    // TODO show SnackBar
                },
                onTapSurface = {
                    viewModel.onTapSurface(it)
                },
                modifier = modifier,
            )
        }

    }
}

@Composable
private fun GrimoireViewScreenContent(
    uiState: GrimoireViewUiState,
    onLoadingRenderer: () -> Unit,
    onClickBack: () -> Unit,
    onSetUseDepthForOcclusion: (Boolean) -> Unit,
    snackBarMessageCallback: (String) -> Unit,
    onTapSurface: (MotionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.renderer == null) {
        // TODO ローディング待ち
        Box(modifier = modifier.fillMaxSize())
        onLoadingRenderer()
    } else {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    GrimoireArView(
                        context,
                        renderer = uiState.renderer,
                        snackBarMessageCallback,
                        onTapListener = {
                            onTapSurface(it)
                        }
                    )
                },
                update = { surfaceView ->

                },
                modifier = Modifier
                    .fillMaxSize()
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                LargeButton(
                    selected = true,
                    text = "< Back",
                    onClick = onClickBack,
                )
            }
            if(uiState.message.isNotEmpty()) {
                // TODO メッセージ表示

            }
        }
    }

    if (uiState.session != null &&
        uiState.session.isDepthModeSupported(Config.DepthMode.AUTOMATIC) &&
        uiState.shouldShowDepthEnableDialog
    ) {
        NeedDepthDialog(
            onClickPositive = {
                onSetUseDepthForOcclusion(true)
            },
            onClickNegative = {
                onSetUseDepthForOcclusion(false)
            },
        )
    }
}

@Composable
private fun NeedDepthDialog(
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(R.string.options_title_with_depth),
        message = stringResource(id = R.string.depth_use_explanation),
        positiveText = stringResource(id = R.string.button_text_enable_depth),
        negativeText = stringResource(id = R.string.button_text_disable_depth),
        onClickPositive = onClickPositive,
        onClickNegative = onClickNegative,
    )
}
