package net.miyataroid.miyatamagrimoire.splash

data class SplashUiState(
    val isLoading: Boolean = false,
    val splashProcessState: SplashProcessState = SplashProcessState.START,
){
    enum class SplashProcessState(message: String) {
        START("環境構築中"),
        MOVE_EFFECT("エフェクトファイルの移動中"),
        FINISH("環境構築完了"),
    }
}