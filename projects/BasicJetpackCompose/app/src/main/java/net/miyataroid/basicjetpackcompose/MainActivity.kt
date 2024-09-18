package net.miyataroid.basicjetpackcompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import net.miyataroid.basicjetpackcompose.ui.theme.BasicJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicJetpackComposeTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        // ダークモード切替などで状態を破棄する
        // var shouldShowOnBoarding by remember { mutableStateOf(true)}
        // ダークモード切替などでも状態を保持する
        var shouldShowOnBoarding by rememberSaveable { mutableStateOf(true) }
        if (shouldShowOnBoarding) {
            OnBoardingScreen(
                onClickContinue = {
                    shouldShowOnBoarding = false
                }
            )
        } else{
            Greetings()
        }
    }
}

@Composable
fun Greetings(
    texts: List<String> = List(1000) { "$it" }
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp),
    ) {
        items(items = texts) { text ->
            Greeting(name = text)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // val expand = remember { mutableStateOf(false)}
    val expand = rememberSaveable { mutableStateOf(false)}
    // val padding = if (expand.value) 48.dp else 0.dp
    val extraPadding by animateDpAsState(
        targetValue = if (expand.value) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        )
    )
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp)),
            ) {
                Text(
                    text = "Hello",
                    modifier = Modifier
                        .padding(2.dp),
                )
                Text(
                    text = "$name!",
                    style = MaterialTheme.typography.headlineLarge
                        .copy(
                            color = MaterialTheme.colorScheme.error,
                        )
                    ,
                    modifier =Modifier
                        .padding(2.dp),
                )
            }

            ElevatedButton(
                onClick = {
                    expand.value = !expand.value
                          },
            ) {
                Text(
                    text = if (expand.value) "show less" else "show more",
                )
            }
        }
    }
}

@Composable
fun OnBoardingScreen(
    onClickContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text("welcome")
        Button(
            onClick = {
                onClickContinue()
            },
            modifier = Modifier.padding(vertical = 24.dp),
        ) {
            Text("continue")
        }

    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Text Preview Night", widthDp = 320)
@Preview(showBackground = true, name = "Text Preview", widthDp = 320)
@Composable
fun GreetingPreview() {
    BasicJetpackComposeTheme {
        Greetings()
    }
}

@Preview
@Composable
fun OnBoardingPreview() {
    BasicJetpackComposeTheme {
        OnBoardingScreen(onClickContinue = {})
    }
}
