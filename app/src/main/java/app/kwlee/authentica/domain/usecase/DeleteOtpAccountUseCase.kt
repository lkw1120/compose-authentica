package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.OtpRepository
import javax.inject.Inject

class DeleteOtpAccountUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteAccount(id)
}
