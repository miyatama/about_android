package net.miyataroid.miyatamagrimoire.home

data class HomeUiState(
    val loading: Boolean = false,
    val selectedMode: SelectedMode = SelectedMode.VIEW,
) {
    enum class SelectedMode {
        VIEW,
        EDIT,
    }
}