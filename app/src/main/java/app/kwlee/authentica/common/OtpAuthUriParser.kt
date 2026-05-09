package app.kwlee.authentica.common

import app.kwlee.authentica.domain.model.OtpAlgorithm
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class ParsedOtpAuth(
    val issuer: String,
    val accountName: String,
    val secret: String,
    val digits: Int = 6,
    val period: Int = 30,
    val algorithm: OtpAlgorithm = OtpAlgorithm.SHA1
)

object OtpAuthUriParser {

    fun parse(uri: String): ParsedOtpAuth {
        val input = uri.trim()
        require(input.startsWith("otpauth://totp/")) { "Only otpauth://totp URI is supported" }

        val noScheme = input.removePrefix("otpauth://totp/")
        val parts = noScheme.split("?", limit = 2)
        val labelPart = decode(parts[0])
        val queryPart = parts.getOrNull(1).orEmpty()

        val queryMap = queryPart
            .split("&")
            .mapNotNull { entry ->
                val kv = entry.split("=", limit = 2)
                if (kv.size == 2) decode(kv[0]) to decode(kv[1]) else null
            }
            .toMap()

        val secret = queryMap["secret"].orEmpty()
        require(secret.isNotBlank()) { "secret is required" }

        val issuerInLabel = labelPart.substringBefore(':', missingDelimiterValue = "").trim()
        val accountName = labelPart.substringAfter(':', missingDelimiterValue = labelPart).trim()
        val issuer = queryMap["issuer"]?.trim().orEmpty().ifBlank { issuerInLabel }

        val digits = queryMap["digits"]?.toIntOrNull()?.takeIf { it in 6..8 } ?: 6
        val period = queryMap["period"]?.toIntOrNull()?.takeIf { it > 0 } ?: 30
        val algorithm = when (queryMap["algorithm"]?.uppercase()) {
            "SHA256" -> OtpAlgorithm.SHA256
            "SHA512" -> OtpAlgorithm.SHA512
            else -> OtpAlgorithm.SHA1
        }

        require(issuer.isNotBlank()) { "issuer is required" }
        require(accountName.isNotBlank()) { "account name is required" }

        return ParsedOtpAuth(
            issuer = issuer,
            accountName = accountName,
            secret = secret,
            digits = digits,
            period = period,
            algorithm = algorithm
        )
    }

    private fun decode(value: String): String {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
    }
}
