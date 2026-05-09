package app.kwlee.authentica.di

import app.kwlee.authentica.model.crypto.SecretCipher
import app.kwlee.authentica.model.crypto.KeystoreSecretCipher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoModule {

    @Binds
    @Singleton
    abstract fun bindSecretCipher(impl: KeystoreSecretCipher): SecretCipher
}
