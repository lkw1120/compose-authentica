package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRequireLaunchAuthUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeRequireLaunchAuth()
}
