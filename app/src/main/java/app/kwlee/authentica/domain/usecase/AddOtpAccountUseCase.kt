package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.model.OtpAccount
import app.kwlee.authentica.domain.model.OtpAlgorithm
import app.kwlee.authentica.domain.repository.OtpRepository
import java.util.UUID
import javax.inject.Inject

class AddOtpAccountUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    suspend operator fun invoke(
        issuer: String,
        accountName: String,
        secret: String,
        digits: Int = 6,
        period: Int = 30,
        algorithm: OtpAlgorithm = OtpAlgorithm.SHA1
    ) {
        val now = System.currentTimeMillis()
        val trimmedIssuer = issuer.trim()
        val trimmedName = accountName.trim()
        val trimmedSecret = secret.trim()

        repository.addAccount(
            OtpAccount(
                id = UUID.randomUUID().toString(),
                issuer = trimmedIssuer,
                accountName = trimmedName,
                label = "$trimmedIssuer:$trimmedName",
                secret = trimmedSecret,
                digits = digits,
                period = period,
                algorithm = algorithm,
                sortOrder = repository.getNextSortOrder(),
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
