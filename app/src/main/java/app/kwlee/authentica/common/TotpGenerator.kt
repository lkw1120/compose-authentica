package app.kwlee.authentica.common

import app.kwlee.authentica.domain.model.OtpAlgorithm
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

object TotpGenerator {

    fun generate(
        base32Secret: String,
        timestampSeconds: Long,
        period: Int,
        digits: Int,
        algorithm: OtpAlgorithm
    ): String {
        val counter = timestampSeconds / period
        val counterBytes = ByteBuffer.allocate(8).putLong(counter).array()

        val key = Base32Decoder.decode(base32Secret)
        val mac = Mac.getInstance(algorithm.toMacName())
        mac.init(SecretKeySpec(key, algorithm.toMacName()))

        val hash = mac.doFinal(counterBytes)
        val offset = hash.last().toInt() and 0x0F

        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
            ((hash[offset + 1].toInt() and 0xFF) shl 16) or
            ((hash[offset + 2].toInt() and 0xFF) shl 8) or
            (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % (10.0.pow(digits.toDouble()).toInt())
        return otp.toString().padStart(digits, '0')
    }

    private fun OtpAlgorithm.toMacName(): String = when (this) {
        OtpAlgorithm.SHA1 -> "HmacSHA1"
        OtpAlgorithm.SHA256 -> "HmacSHA256"
        OtpAlgorithm.SHA512 -> "HmacSHA512"
    }
}
