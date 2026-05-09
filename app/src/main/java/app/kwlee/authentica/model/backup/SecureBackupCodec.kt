package app.kwlee.authentica.model.backup

import app.kwlee.authentica.domain.model.OtpAlgorithm
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SecureBackupCodec {
    private const val SCHEMA_VERSION = 2
    private const val KDF_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_SIZE_BITS = 256
    private const val PBKDF2_ITERATIONS = 210_000
    private const val SALT_BYTES = 16
    private const val NONCE_BYTES = 12
    private const val TAG_BITS = 128

    data class PlainAccount(
        val issuer: String,
        val accountName: String,
        val secret: String,
        val digits: Int,
        val period: Int,
        val algorithm: OtpAlgorithm
    )

    fun encrypt(accounts: List<PlainAccount>, password: CharArray): String {
        require(password.size >= 8) { "Password must be at least 8 characters" }

        val payload = JSONObject().apply {
            put("accounts", JSONArray().apply {
                accounts.forEach { account ->
                    put(
                        JSONObject()
                            .put("issuer", account.issuer)
                            .put("accountName", account.accountName)
                            .put("secret", account.secret)
                            .put("digits", account.digits)
                            .put("period", account.period)
                            .put("algorithm", account.algorithm.name)
                    )
                }
            })
        }

        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val nonce = ByteArray(NONCE_BYTES).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, nonce))
        val encrypted = cipher.doFinal(payload.toString().toByteArray(StandardCharsets.UTF_8))

        return JSONObject()
            .put("schemaVersion", SCHEMA_VERSION)
            .put("kdf", KDF_ALGORITHM)
            .put("iterations", PBKDF2_ITERATIONS)
            .put("salt", salt.encodeBase64())
            .put("nonce", nonce.encodeBase64())
            .put("ciphertext", encrypted.encodeBase64())
            .toString(2)
    }

    fun decrypt(json: String, password: CharArray): List<PlainAccount> {
        val root = JSONObject(json)
        val version = root.optInt("schemaVersion", -1)
        require(version == SCHEMA_VERSION) { "Unsupported backup version" }

        val iterations = root.optInt("iterations", -1)
        require(iterations > 0) { "Invalid backup KDF settings" }

        val salt = root.getString("salt").decodeBase64()
        val nonce = root.getString("nonce").decodeBase64()
        val ciphertext = root.getString("ciphertext").decodeBase64()

        val key = deriveKey(password, salt, iterations)
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, nonce))
        val decrypted = cipher.doFinal(ciphertext)
        val payload = JSONObject(String(decrypted, StandardCharsets.UTF_8))

        val items = payload.optJSONArray("accounts") ?: JSONArray()
        return buildList {
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                add(
                    PlainAccount(
                        issuer = item.getString("issuer"),
                        accountName = item.getString("accountName"),
                        secret = item.getString("secret"),
                        digits = item.optInt("digits", 6),
                        period = item.optInt("period", 30),
                        algorithm = runCatching {
                            OtpAlgorithm.valueOf(item.optString("algorithm", OtpAlgorithm.SHA1.name))
                        }.getOrDefault(OtpAlgorithm.SHA1)
                    )
                )
            }
        }
    }

    private fun deriveKey(password: CharArray, salt: ByteArray, iterations: Int = PBKDF2_ITERATIONS): SecretKeySpec {
        val spec = PBEKeySpec(password, salt, iterations, KEY_SIZE_BITS)
        val factory = SecretKeyFactory.getInstance(KDF_ALGORITHM)
        val bytes = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return SecretKeySpec(bytes, "AES")
    }

    private fun ByteArray.encodeBase64(): String = java.util.Base64.getEncoder().encodeToString(this)

    private fun String.decodeBase64(): ByteArray = java.util.Base64.getDecoder().decode(this)
}
