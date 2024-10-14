package net.miyataroid.miyatamagrimoire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings
import net.miyataroid.miyatamagrimoire.ui.theme.MiyatamaGrimoireTheme
import net.miyataroid.miyatamagrimoire.core.helpers.ARCoreSessionLifecycleHelper

class MainActivity : ComponentActivity() {
    lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
    // lateinit var renderer: HelloArRenderer

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
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            appGraph(
                navController,
            )
        }
    }
}