package app.kwlee.authentica.domain.model

data class OtpAccount(
    val id: String,
    val issuer: String,
    val accountName: String,
    val label: String,
    val secret: String,
    val digits: Int = 6,
    val period: Int = 30,
    val algorithm: OtpAlgorithm = OtpAlgorithm.SHA1,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
