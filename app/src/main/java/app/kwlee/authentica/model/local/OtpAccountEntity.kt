package app.kwlee.authentica.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "otp_accounts")
data class OtpAccountEntity(
    @PrimaryKey val id: String,
    val issuer: String,
    val accountName: String,
    val label: String,
    val encryptedSecret: String,
    val encryptionIv: String,
    val digits: Int,
    val period: Int,
    val algorithm: String,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long
)
