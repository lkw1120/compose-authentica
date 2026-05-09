package app.kwlee.authentica.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kwlee.authentica.domain.model.AppThemeMode
import app.kwlee.authentica.domain.model.OtpAlgorithm
import app.kwlee.authentica.domain.usecase.AddOtpAccountUseCase
import app.kwlee.authentica.domain.usecase.AddOtpFromUriUseCase
import app.kwlee.authentica.domain.usecase.DeleteOtpAccountUseCase
import app.kwlee.authentica.domain.usecase.ExportOtpAccountsBackupUseCase
import app.kwlee.authentica.domain.usecase.ImportOtpAccountsBackupUseCase
import app.kwlee.authentica.domain.usecase.ObserveRequireLaunchAuthUseCase
import app.kwlee.authentica.domain.usecase.ObserveThemeModeUseCase
import app.kwlee.authentica.domain.usecase.ReorderOtpAccountsUseCase
import app.kwlee.authentica.domain.usecase.SetRequireLaunchAuthUseCase
import app.kwlee.authentica.domain.usecase.SetThemeModeUseCase
import app.kwlee.authentica.domain.usecase.UpdateOtpAccountLabelUseCase
import app.kwlee.authentica.model.otp.OtpCodeManager
import app.kwlee.authentica.model.otp.OtpCodeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    otpCodeManager: OtpCodeManager,
    observeRequireLaunchAuthUseCase: ObserveRequireLaunchAuthUseCase,
    observeThemeModeUseCase: ObserveThemeModeUseCase,
    private val reorderOtpAccountsUseCase: ReorderOtpAccountsUseCase,
    private val setRequireLaunchAuthUseCase: SetRequireLaunchAuthUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val addOtpAccountUseCase: AddOtpAccountUseCase,
    private val addOtpFromUriUseCase: AddOtpFromUriUseCase,
    private val exportOtpAccountsBackupUseCase: ExportOtpAccountsBackupUseCase,
    private val importOtpAccountsBackupUseCase: ImportOtpAccountsBackupUseCase,
    private val deleteOtpAccountUseCase: DeleteOtpAccountUseCase,
    private val updateOtpAccountLabelUseCase: UpdateOtpAccountLabelUseCase
) : ViewModel() {

    private val _addAccountSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val addAccountSuccess: SharedFlow<Unit> = _addAccountSuccess.asSharedFlow()

    private val error = MutableStateFlow<HomeError?>(null)
    private val editingItem = MutableStateFlow<OtpAccountUiModel?>(null)

    val uiState = combine(
        otpCodeManager.stateFlow,
        observeRequireLaunchAuthUseCase(),
        observeThemeModeUseCase(),
        error,
        editingItem
    ) { codes, requireLaunchAuth, themeMode, error, editing ->
        HomeUiState(
            items = codes.map { it.toUiModel() },
            requireLaunchAuth = requireLaunchAuth,
            themeMode = themeMode,
            error = error,
            editingItem = editing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun setRequireLaunchAuth(enabled: Boolean) {
        viewModelScope.launch { setRequireLaunchAuthUseCase(enabled) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { setThemeModeUseCase(mode) }
    }

    fun clearError() {
        error.value = null
    }

    fun startEditing(item: OtpAccountUiModel) {
        editingItem.value = item
    }

    fun cancelEditing() {
        editingItem.value = null
    }

    fun saveEdit(issuer: String, accountName: String) {
        val item = editingItem.value ?: return
        editingItem.value = null

        viewModelScope.launch {
            runCatching {
                updateOtpAccountLabelUseCase(item.id, issuer, accountName)
            }.onFailure {
                error.value = HomeError.UpdateAccountFailed
            }
        }
    }

    fun deleteAccount(id: String) {
        viewModelScope.launch {
            runCatching {
                deleteOtpAccountUseCase(id)
            }.onFailure {
                error.value = HomeError.DeleteAccountFailed
            }
        }
    }

    fun reorderAccounts(idsInOrder: List<String>) {
        viewModelScope.launch {
            runCatching {
                reorderOtpAccountsUseCase(idsInOrder)
            }.onFailure {
                error.value = HomeError.ReorderAccountsFailed
            }
        }
    }

    fun addAccount(
        issuer: String,
        accountName: String,
        secret: String,
        digits: Int,
        period: Int,
        algorithm: OtpAlgorithm
    ) {
        if (issuer.isBlank() || accountName.isBlank() || secret.isBlank()) {
            error.value = HomeError.FillRequiredFields
            return
        }

        viewModelScope.launch {
            runCatching {
                addOtpAccountUseCase(
                    issuer = issuer,
                    accountName = accountName,
                    secret = secret,
                    digits = digits,
                    period = period,
                    algorithm = algorithm
                )
            }.onSuccess {
                _addAccountSuccess.tryEmit(Unit)
            }.onFailure {
                error.value = HomeError.AddAccountFailed
            }
        }
    }

    fun addAccountFromUri(uri: String) {
        if (uri.isBlank()) {
            error.value = HomeError.UriRequired
            return
        }

        viewModelScope.launch {
            runCatching { addOtpFromUriUseCase(uri) }
                .onFailure {
                    error.value = HomeError.InvalidUri
                }
        }
    }

    suspend fun exportAccountsJson(password: CharArray): String {
        return exportOtpAccountsBackupUseCase(password)
    }

    suspend fun importAccountsJson(json: String, password: CharArray): Int {
        return importOtpAccountsBackupUseCase(json, password)
    }

    private fun OtpCodeState.toUiModel() = OtpAccountUiModel(
        id = id,
        issuer = issuer,
        accountName = accountName,
        code = code,
        remainingSeconds = remainingSeconds,
        period = period,
        progressFraction = progressFraction
    )
}
