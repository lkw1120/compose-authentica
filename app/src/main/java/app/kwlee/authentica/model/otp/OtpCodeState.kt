package app.kwlee.authentica.model.otp

data class OtpCodeState(
    val id: String,
    val issuer: String,
    val accountName: String,
    val code: String,
    val remainingSeconds: Int,
    val period: Int,
    val progressFraction: Float
)
