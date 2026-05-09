package app.kwlee.authentica

import app.kwlee.authentica.common.TotpGenerator
import app.kwlee.authentica.domain.model.OtpAlgorithm
import org.junit.Assert.assertEquals
import org.junit.Test

class TotpGeneratorTest {

    @Test
    fun rfc6238_vectors_sha1_sha256_sha512_match_expected() {
        val secretSha1 = toBase32("12345678901234567890")
        val secretSha256 = toBase32("12345678901234567890123456789012")
        val secretSha512 = toBase32(
            "1234567890123456789012345678901234567890123456789012345678901234"
        )

        val cases = listOf(
            59L to Triple("94287082", "46119246", "90693936"),
            1111111109L to Triple("07081804", "68084774", "25091201"),
            1111111111L to Triple("14050471", "67062674", "99943326"),
            1234567890L to Triple("89005924", "91819424", "93441116"),
            2000000000L to Triple("69279037", "90698825", "38618901"),
            20000000000L to Triple("65353130", "77737706", "47863826")
        )

        cases.forEach { (timestamp, expected) ->
            assertEquals(
                expected.first,
                TotpGenerator.generate(secretSha1, timestamp, 30, 8, OtpAlgorithm.SHA1)
            )
            assertEquals(
                expected.second,
                TotpGenerator.generate(secretSha256, timestamp, 30, 8, OtpAlgorithm.SHA256)
            )
            assertEquals(
                expected.third,
                TotpGenerator.generate(secretSha512, timestamp, 30, 8, OtpAlgorithm.SHA512)
            )
        }
    }

    private fun toBase32(input: String): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val bytes = input.encodeToByteArray()

        val output = StringBuilder()
        var buffer = 0
        var bitsLeft = 0

        for (byte in bytes) {
            buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
            bitsLeft += 8

            while (bitsLeft >= 5) {
                val index = (buffer shr (bitsLeft - 5)) and 0x1F
                output.append(alphabet[index])
                bitsLeft -= 5
            }
        }

        if (bitsLeft > 0) {
            val index = (buffer shl (5 - bitsLeft)) and 0x1F
            output.append(alphabet[index])
        }

        return output.toString()
    }
}
