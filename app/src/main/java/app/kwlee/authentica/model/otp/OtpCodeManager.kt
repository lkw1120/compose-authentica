package app.kwlee.authentica.model.otp

import app.kwlee.authentica.common.TotpGenerator
import app.kwlee.authentica.domain.repository.OtpRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpCodeManager @Inject constructor(
    private val repository: OtpRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Accessed only within the combine transform (sequential), so regular MutableMap is safe.
    private val codeCache = mutableMapOf<String, Pair<Long, String>>() // id -> (periodBucket, code)

    private val tickFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(100)
        }
    }

    val stateFlow: StateFlow<List<OtpCodeState>> = combine(
        repository.observeAccounts(),
        tickFlow
    ) { accounts, nowMillis ->
        val currentIds = accounts.map { it.id }.toSet()
        codeCache.keys.retainAll(currentIds)

        accounts.map { account ->
            val periodMillis = account.period * 1000L
            val periodBucket = nowMillis / periodMillis

            val code = codeCache[account.id]
                ?.takeIf { it.first == periodBucket }
                ?.second
                ?: TotpGenerator.generate(
                    base32Secret = account.secret,
                    timestampSeconds = nowMillis / 1000L,
                    period = account.period,
                    digits = account.digits,
                    algorithm = account.algorithm
                ).also { codeCache[account.id] = periodBucket to it }

            val elapsedMillis = nowMillis % periodMillis
            val progressFraction = (elapsedMillis.toFloat() / periodMillis).coerceIn(0f, 1f)
            val remainingMillis = periodMillis - elapsedMillis
            val remainingSeconds = ((remainingMillis + 999L) / 1000L).toInt()

            OtpCodeState(
                id = account.id,
                issuer = account.issuer,
                accountName = account.accountName,
                code = code,
                remainingSeconds = remainingSeconds,
                period = account.period,
                progressFraction = progressFraction
            )
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
