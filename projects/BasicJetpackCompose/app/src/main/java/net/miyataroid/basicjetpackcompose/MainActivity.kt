package net.miyataroid.basicjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            val texts = listOf("Android", "iOS")
            texts.forEach { text ->
                Greeting(name = text)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val expand = remember { mutableStateOf(false)}
    val padding = if (expand.value) 48.dp else 0.dp
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
                    .padding(bottom = padding),
            ) {
                Text(
                    text = "Hello",
                    modifier = Modifier
                        .padding(2.dp),
                )
                Text(
                    text = "$name!",
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
    modifier: Modifier = Modifier,
) {
    var shouldShowOnBoarding by remember { mutableStateOf(true)}
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text("welcome")
        Button(
            onClick = {
                shouldShowOnBoarding = false
            },
            modifier = Modifier.padding(vertical = 24.dp),
        ) {
            Text("continue")
        }

    }

}

@Preview(showBackground = true, name = "Text Preview", widthDp = 320)
@Composable
fun GreetingPreview() {
    BasicJetpackComposeTheme {
        MyApp()
    }
}

@Preview
@Composable
fun OnBoardingPreview() {
    BasicJetpackComposeTheme {
        OnBoardingScreen()
    }
}
