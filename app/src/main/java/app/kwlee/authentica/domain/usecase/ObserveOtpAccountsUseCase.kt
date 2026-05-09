package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.model.OtpAccount
import app.kwlee.authentica.domain.repository.OtpRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOtpAccountsUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    operator fun invoke(): Flow<List<OtpAccount>> = repository.observeAccounts()
}
