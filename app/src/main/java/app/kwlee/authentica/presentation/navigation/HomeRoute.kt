package app.kwlee.authentica.presentation.navigation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.kwlee.authentica.R
import app.kwlee.authentica.presentation.backup.BackupViewModel
import app.kwlee.authentica.presentation.component.ExportBackupBottomSheet
import app.kwlee.authentica.presentation.component.ImportBackupBottomSheet
import app.kwlee.authentica.presentation.home.HomeError
import app.kwlee.authentica.presentation.home.HomeScreen
import app.kwlee.authentica.presentation.home.HomeViewModel
import app.kwlee.authentica.presentation.manual.ManualScreen
import app.kwlee.authentica.presentation.settings.OpenSourceLicensesScreen
import app.kwlee.authentica.presentation.settings.SettingsScreen
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val ROUTE_HOME = "home"
private const val ROUTE_MANUAL = "manual"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_LICENSES = "licenses"

@Composable
fun HomeRoute(
    onScanQrCode: ((String) -> Unit) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    backupViewModel: BackupViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val state by viewModel.uiState.collectAsState()
    val backupState by backupViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val exportSuccessMessage = stringResource(R.string.backup_export_success)
    val exportFailureMessage = stringResource(R.string.backup_export_failure)
    val importSuccessFormat = stringResource(R.string.backup_import_success)
    val importFailureMessage = stringResource(R.string.backup_import_failure)
    val fillRequiredFieldsMessage = stringResource(R.string.error_fill_required_fields)
    val uriRequiredMessage = stringResource(R.string.error_uri_required)
    val updateAccountFailedMessage = stringResource(R.string.error_update_account)
    val deleteAccountFailedMessage = stringResource(R.string.error_delete_account)
    val reorderAccountsFailedMessage = stringResource(R.string.error_reorder_accounts)
    val addAccountFailedMessage = stringResource(R.string.error_add_account)
    val invalidUriMessage = stringResource(R.string.error_invalid_uri)

    fun writeText(uri: Uri, text: String) {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            output.write(text.toByteArray())
        }
    }

    fun readText(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { input ->
            input.bufferedReader().readText()
        } ?: ""
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) {
            backupViewModel.clearStagedExportPassword()
            return@rememberLauncherForActivityResult
        }
        val password = backupViewModel.consumeStagedExportPassword() ?: return@rememberLauncherForActivityResult
        scope.launch {
            backupViewModel.setLoading(true)
            try {
                runCatching {
                    val backupJson = viewModel.exportAccountsJson(password)
                    writeText(uri, backupJson)
                }.onSuccess {
                    Toast.makeText(context, exportSuccessMessage, Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(context, exportFailureMessage, Toast.LENGTH_SHORT).show()
                }
            } finally {
                backupViewModel.setLoading(false)
                password.fill('\u0000')
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        pendingImportUri = uri
        backupViewModel.openImportSheet()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            val msg = when (it) {
                HomeError.FillRequiredFields -> fillRequiredFieldsMessage
                HomeError.UriRequired -> uriRequiredMessage
                HomeError.UpdateAccountFailed -> updateAccountFailedMessage
                HomeError.DeleteAccountFailed -> deleteAccountFailedMessage
                HomeError.ReorderAccountsFailed -> reorderAccountsFailedMessage
                HomeError.AddAccountFailed -> addAccountFailedMessage
                HomeError.InvalidUri -> invalidUriMessage
            }
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.addAccountSuccess.collect {
            navController.popBackStack(ROUTE_MANUAL, inclusive = true)
        }
    }

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                state = state,
                snackbarHostState = snackbarHostState,
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
                onScanQrCode = { onScanQrCode(viewModel::addAccountFromUri) },
                onOpenManualInput = { navController.navigate(ROUTE_MANUAL) },
                onDeleteAccount = viewModel::deleteAccount,
                onReorderAccounts = viewModel::reorderAccounts,
                onStartEditing = viewModel::startEditing,
                onCancelEditing = viewModel::cancelEditing,
                onSaveEdit = viewModel::saveEdit
            )
        }
        composable(ROUTE_MANUAL) {
            ManualScreen(
                onBack = { navController.popBackStack() },
                onSave = viewModel::addAccount
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                requireLaunchAuth = state.requireLaunchAuth,
                onToggleRequireLaunchAuth = viewModel::setRequireLaunchAuth,
                themeMode = state.themeMode,
                onThemeModeChange = viewModel::setThemeMode,
                isBackupLoading = backupState.isBackupLoading,
                onExportAccounts = backupViewModel::openExportSheet,
                onImportAccounts = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
                onOpenSourceLicenses = { navController.navigate(ROUTE_LICENSES) }
            )
        }
        composable(ROUTE_LICENSES) {
            OpenSourceLicensesScreen(onBack = { navController.popBackStack() })
        }
    }

    if (backupState.showExportPasswordSheet) {
        ExportBackupBottomSheet(
            state = backupState,
            onDismiss = backupViewModel::dismissExportSheet,
            onPasswordChange = backupViewModel::updateExportPassword,
            onConfirmPasswordChange = backupViewModel::updateExportPasswordConfirm,
            onExport = {
                if (backupViewModel.stageExportPassword()) {
                    val filename = "authentica-backup-${LocalDate.now()}.json"
                    exportLauncher.launch(filename)
                }
            }
        )
    }

    if (backupState.showImportPasswordSheet) {
        ImportBackupBottomSheet(
            state = backupState,
            onDismiss = {
                backupViewModel.dismissImportSheet()
                pendingImportUri = null
            },
            onPasswordChange = backupViewModel::updateImportPassword,
            onImport = {
                val uri = pendingImportUri ?: return@ImportBackupBottomSheet
                val password = backupViewModel.consumeImportPassword() ?: return@ImportBackupBottomSheet
                scope.launch {
                    backupViewModel.setLoading(true)
                    try {
                        runCatching {
                            val content = readText(uri)
                            viewModel.importAccountsJson(content, password)
                        }.onSuccess { imported ->
                            Toast.makeText(context, importSuccessFormat.format(imported), Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(context, importFailureMessage, Toast.LENGTH_SHORT).show()
                        }
                    } finally {
                        backupViewModel.setLoading(false)
                        password.fill('\u0000')
                        pendingImportUri = null
                    }
                }
            }
        )
    }
}
