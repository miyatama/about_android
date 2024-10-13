package net.miyataroid.miyatamagrimoire

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object Home
object GrimoireEdit
object GrimoireView

enum class Screens(name: String) {
    Home("home"),
    GrimoireEdit("gremoire_edit"),
    GrimoireView("grimoire_view"),
}

fun NavGraphBuilder.appGraph (
    navController: NavController,
){
    composable(
        route = Screens.Home.name,
    ) {
        HomeScreen(
            navigateToGrimoreEdit = {},
            navigateToGrimoreView = {},
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
        GrimoireViewScreen()
    }
}