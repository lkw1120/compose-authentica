package app.kwlee.authentica

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import app.kwlee.authentica.presentation.OtpQrScanner
import app.kwlee.authentica.domain.model.AppThemeMode
import app.kwlee.authentica.domain.usecase.ObserveRequireLaunchAuthUseCase
import app.kwlee.authentica.domain.usecase.ObserveThemeModeUseCase
import app.kwlee.authentica.presentation.navigation.HomeRoute
import app.kwlee.authentica.presentation.theme.AuthenticaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var observeRequireLaunchAuthUseCase: ObserveRequireLaunchAuthUseCase
    @Inject
    lateinit var observeThemeModeUseCase: ObserveThemeModeUseCase

    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL

    private var isUnlocked by mutableStateOf(false)
    private var isAuthDecisionReady by mutableStateOf(false)
    private var authUnavailable by mutableStateOf(false)
    private lateinit var qrScanner: OtpQrScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        qrScanner = OtpQrScanner(this)

        lifecycleScope.launch {
            val requireLaunchAuth = observeRequireLaunchAuthUseCase().first()
            if (requireLaunchAuth) {
                authenticateIfAvailable()
            } else {
                isUnlocked = true
            }
            isAuthDecisionReady = true
        }

        setContent {
            val themeMode by observeThemeModeUseCase().collectAsState(initial = AppThemeMode.SYSTEM)
            val darkTheme = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }
            AuthenticaTheme(darkTheme = darkTheme) {
                val systemBarColor = MaterialTheme.colorScheme.background.toArgb()
                SideEffect {
                    window.statusBarColor = systemBarColor
                    window.navigationBarColor = systemBarColor
                    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                    insetsController.isAppearanceLightStatusBars = !darkTheme
                    insetsController.isAppearanceLightNavigationBars = !darkTheme
                }

                when {
                    !isAuthDecisionReady -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.auth_loading), style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    isUnlocked -> {
                        HomeRoute(
                            onScanQrCode = { onScanned ->
                                qrScanner.scan()
                                    .addOnSuccessListener { barcode ->
                                        barcode.rawValue?.let(onScanned)
                                    }
                            }
                        )
                    }

                    authUnavailable -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.auth_screen_lock_required_title),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.auth_screen_lock_required_body),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            Button(onClick = { openSecuritySettings() }) {
                                Text(stringResource(R.string.auth_open_settings))
                            }
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.auth_unlock_required_title),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { showBiometricPrompt() }) {
                                Text(stringResource(R.string.auth_unlock_button))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun authenticateIfAvailable() {
        when (BiometricManager.from(this).canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> showBiometricPrompt()
            else -> authUnavailable = true
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.auth_biometric_title))
            .setSubtitle(getString(R.string.auth_biometric_subtitle))
            .setAllowedAuthenticators(authenticators)
            .build()

        BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    isUnlocked = true
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        finish()
                    }
                }
            }
        ).authenticate(promptInfo)
    }

    private fun openSecuritySettings() {
        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
    }
}
