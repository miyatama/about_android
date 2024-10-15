package net.miyataroid.miyatamagrimoire

import androidx.compose.runtime.internal.composableLambda
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.miyataroid.miyatamagrimoire.edit.GrimoireEditScreen
import net.miyataroid.miyatamagrimoire.home.HomeScreen
import net.miyataroid.miyatamagrimoire.splash.SplashScreen
import net.miyataroid.miyatamagrimoire.view.GrimoireViewScreen

object Home
object GrimoireEdit
object GrimoireView

enum class Screens(name: String) {
    Splash("splash"),
    Home("home"),
    GrimoireEdit("gremoire_edit"),
    GrimoireView("grimoire_view"),
}

fun NavGraphBuilder.appGraph (
    navController: NavController,
){
    composable(
        route = Screens.Splash.name,
    ) {
        SplashScreen(
            navigateToHome = {
                navController.navigate(Screens.Home.name)
            }
        )
    }
    composable(
        route = Screens.Home.name,
    ) {
        HomeScreen(
            navigateToGrimoreEdit = {
                navController.navigate(Screens.GrimoireEdit.name)
            },
            navigateToGrimoreView = {
                navController.navigate(Screens.GrimoireView.name)
            },
        )
    }

    composable(
        route = Screens.GrimoireEdit.name,
    ) {
        GrimoireEditScreen()
    }

    composable(
        route = Screens.GrimoireView.name,
    ) {
        GrimoireViewScreen(
            navigateToBack = {
                navController.navigateUp()
            }
        )
    }
}