package net.miyataroid.miyatamagrimoire

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object Home
object GrimoireEdit
object GrimoireView

fun NavGraphBuilder.appGraph (
    navController: NavController,
){
    composable<Home>() {
        HomeScreen(
            navigateToGrimoreEdit = {},
            navigateToGrimoreView = {},
        )
    }

    composable<GrimoireEdit>() {

    }

    composable<GrimoireView>() {

    }
}