package net.miyataroid.miyatamagrimoire

import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings
import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettingsImpl
import net.miyataroid.miyatamagrimoire.edit.GrimoireEditViewModel
import net.miyataroid.miyatamagrimoire.home.HomeViewModel
import net.miyataroid.miyatamagrimoire.view.GrimoireViewViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val modules = module {
    single<DepthSettings> {
        DepthSettingsImpl(get())
    }
    viewModel {
        HomeViewModel()
    }
    viewModel {
        GrimoireViewViewModel(get())
    }
    viewModel {
        GrimoireEditViewModel()
    }
}