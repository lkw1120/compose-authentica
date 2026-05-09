package app.kwlee.authentica.presentation.home

import app.kwlee.authentica.domain.model.AppThemeMode

data class HomeUiState(
    val items: List<OtpAccountUiModel> = emptyList(),
    val requireLaunchAuth: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val error: HomeError? = null,
    val editingItem: OtpAccountUiModel? = null
)

data class OtpAccountUiModel(
    val id: String,
    val issuer: String,
    val accountName: String,
    val code: String,
    val remainingSeconds: Int,
    val period: Int,
    val progressFraction: Float
)
