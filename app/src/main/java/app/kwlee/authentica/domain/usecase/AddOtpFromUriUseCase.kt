package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.common.OtpAuthUriParser
import javax.inject.Inject

class AddOtpFromUriUseCase @Inject constructor(
    private val addOtpAccountUseCase: AddOtpAccountUseCase
) {
    suspend operator fun invoke(uri: String) {
        val parsed = OtpAuthUriParser.parse(uri)
        addOtpAccountUseCase(
            issuer = parsed.issuer,
            accountName = parsed.accountName,
            secret = parsed.secret,
            digits = parsed.digits,
            period = parsed.period,
            algorithm = parsed.algorithm
        )
    }
}
