package net.miyataroid.miyatamagrimoire

import net.miyataroid.miyatamagrimoire.core.helpers.ARCoreSessionLifecycleHelper
import net.miyataroid.miyatamagrimoire.core.helpers.ARCoreSessionLifecycleHelperImpl
import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettings
import net.miyataroid.miyatamagrimoire.core.helpers.DepthSettingsImpl
import net.miyataroid.miyatamagrimoire.core.helpers.DisplayRotationHelper
import net.miyataroid.miyatamagrimoire.core.helpers.DisplayRotationHelperImpl
import net.miyataroid.miyatamagrimoire.core.helpers.InstantPlacementSettings
import net.miyataroid.miyatamagrimoire.core.helpers.InstantPlacementSettingsImpl
import net.miyataroid.miyatamagrimoire.edit.GrimoireEditViewModel
import net.miyataroid.miyatamagrimoire.home.HomeViewModel
import net.miyataroid.miyatamagrimoire.splash.SplashViewModel
import net.miyataroid.miyatamagrimoire.view.GrimoireViewViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    single<DepthSettings> {
        DepthSettingsImpl(get())
    }
    single<InstantPlacementSettings> {
        InstantPlacementSettingsImpl(get())
    }
    single<ARCoreSessionLifecycleHelper> {
        ARCoreSessionLifecycleHelperImpl()
    }
    single<DisplayRotationHelper>{
        DisplayRotationHelperImpl(get())
    }
    viewModel {
        SplashViewModel()
    }
    viewModel {
        HomeViewModel()
    }
    viewModel {
        GrimoireViewViewModel(get(), get(), get(), get())
    }
    viewModel {
        GrimoireEditViewModel()
    }
}