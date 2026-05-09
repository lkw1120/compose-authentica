package app.kwlee.authentica.model.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [OtpAccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun otpAccountDao(): OtpAccountDao
}
