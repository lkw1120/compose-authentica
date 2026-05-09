package app.kwlee.authentica.model.crypto

interface SecretCipher {
    fun encrypt(plainText: String): Pair<String, String>
    fun decrypt(cipherText: String, iv: String): String
}
