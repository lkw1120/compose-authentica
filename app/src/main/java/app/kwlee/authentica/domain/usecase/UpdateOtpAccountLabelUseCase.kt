package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.OtpRepository
import javax.inject.Inject

class UpdateOtpAccountLabelUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    suspend operator fun invoke(id: String, issuer: String, accountName: String) {
        require(issuer.isNotBlank()) { "Issuer is required" }
        require(accountName.isNotBlank()) { "Account name is required" }
        repository.updateAccountLabel(id, issuer.trim(), accountName.trim())
    }
}
