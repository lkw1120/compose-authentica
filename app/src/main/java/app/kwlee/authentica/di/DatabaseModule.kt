package app.kwlee.authentica.di

import android.content.Context
import androidx.room.Room
import app.kwlee.authentica.model.local.AppDatabase
import app.kwlee.authentica.model.local.OtpAccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "authentica.db"
        ).build()
    }

    @Provides
    fun provideOtpAccountDao(database: AppDatabase): OtpAccountDao = database.otpAccountDao()
}
