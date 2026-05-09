package app.kwlee.authentica.common

object Base32Decoder {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    fun decode(input: String): ByteArray {
        val cleaned = input.uppercase().replace("=", "").replace(" ", "")
        val out = ArrayList<Byte>()

        var buffer = 0
        var bitsLeft = 0

        for (char in cleaned) {
            val value = ALPHABET.indexOf(char)
            if (value == -1) continue

            buffer = (buffer shl 5) or value
            bitsLeft += 5

            if (bitsLeft >= 8) {
                out.add(((buffer shr (bitsLeft - 8)) and 0xFF).toByte())
                bitsLeft -= 8
            }
        }

        return out.toByteArray()
    }
}
