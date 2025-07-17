package io.bossdogs.ui

data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorMsg: String? = null
) {
    companion object {
        fun <T> loading() = UiState<T>(isLoading = true)
        fun <T> success(data: T) = UiState(isLoading = false, data = data)
        fun <T> error(msg: String) = UiState<T>(isLoading = false, errorMsg = msg)
    }
}