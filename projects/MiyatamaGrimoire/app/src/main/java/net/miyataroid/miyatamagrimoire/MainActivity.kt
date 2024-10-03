package net.miyataroid.miyatamagrimoire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiyatamaGrimoireTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
        ) {

        }
    }
}