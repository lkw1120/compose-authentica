package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.OtpRepository
import javax.inject.Inject

class ReorderOtpAccountsUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    suspend operator fun invoke(idsInOrder: List<String>) {
        repository.reorderAccounts(idsInOrder)
    }
}
