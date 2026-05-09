package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.SettingsRepository
import javax.inject.Inject

class SetRequireLaunchAuthUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setRequireLaunchAuth(enabled)
    }
}
