package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.model.AppThemeMode
import app.kwlee.authentica.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppThemeMode> = repository.observeThemeMode()
}
