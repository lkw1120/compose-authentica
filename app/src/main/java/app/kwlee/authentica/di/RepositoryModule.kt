package app.kwlee.authentica.di

import app.kwlee.authentica.domain.repository.BackupRepository
import app.kwlee.authentica.domain.repository.OtpRepository
import app.kwlee.authentica.domain.repository.SettingsRepository
import app.kwlee.authentica.model.repository.BackupRepositoryImpl
import app.kwlee.authentica.model.repository.OtpRepositoryImpl
import app.kwlee.authentica.model.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindOtpRepository(impl: OtpRepositoryImpl): OtpRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
