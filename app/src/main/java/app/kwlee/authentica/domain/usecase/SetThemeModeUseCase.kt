package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.model.AppThemeMode
import app.kwlee.authentica.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(mode: AppThemeMode) = repository.setThemeMode(mode)
}
