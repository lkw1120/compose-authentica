package app.kwlee.authentica.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kwlee.authentica.R
import app.kwlee.authentica.domain.model.AppThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    requireLaunchAuth: Boolean,
    onToggleRequireLaunchAuth: (Boolean) -> Unit,
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    onThemeModeChange: (AppThemeMode) -> Unit = {},
    isBackupLoading: Boolean = false,
    onExportAccounts: () -> Unit = {},
    onImportAccounts: () -> Unit = {},
    onOpenSourceLicenses: () -> Unit = {}
) {
    var showThemeSheet by rememberSaveable { mutableStateOf(false) }
    var pendingThemeMode by rememberSaveable { mutableStateOf(themeMode) }
    val context = LocalContext.current
    val appVersion = remember(context) {
        runCatching {
            val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "v${pkgInfo.versionName ?: "?"}"
        }.getOrDefault("-")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = stringResource(R.string.content_desc_go_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 8.dp)
        ) {
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(),
                headlineContent = { Text(stringResource(R.string.settings_app_name)) },
                supportingContent = { Text(stringResource(R.string.settings_app_tagline)) },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )

            ListItem(
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(),
                headlineContent = { Text(stringResource(R.string.settings_app_version)) },
                supportingContent = { Text(appVersion) },
                leadingContent = { Icon(Icons.Default.Tag, contentDescription = null) }
            )

            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        pendingThemeMode = themeMode
                        showThemeSheet = true
                    },
                colors = ListItemDefaults.colors(),
                headlineContent = { Text(stringResource(R.string.settings_app_theme)) },
                supportingContent = { Text(themeMode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) }
            )

            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(value = requireLaunchAuth, onValueChange = onToggleRequireLaunchAuth),
                colors = ListItemDefaults.colors(),
                headlineContent = { Text(stringResource(R.string.settings_require_unlock)) },
                supportingContent = { Text(stringResource(R.string.settings_require_unlock_subtitle)) },
                leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = requireLaunchAuth,
                        onCheckedChange = null
                    )
                }
            )

            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isBackupLoading, onClick = onExportAccounts),
                colors = ListItemDefaults.colors(
                    headlineColor = if (isBackupLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface,
                    supportingColor = if (isBackupLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                headlineContent = { Text(stringResource(R.string.settings_export_accounts)) },
                supportingContent = { Text(stringResource(R.string.settings_export_accounts_subtitle)) },
                leadingContent = { Icon(Icons.Default.FileDownload, contentDescription = null) }
            )

            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isBackupLoading, onClick = onImportAccounts),
                colors = ListItemDefaults.colors(
                    headlineColor = if (isBackupLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface,
                    supportingColor = if (isBackupLoading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                headlineContent = { Text(stringResource(R.string.settings_import_accounts)) },
                supportingContent = { Text(stringResource(R.string.settings_import_accounts_subtitle)) },
                leadingContent = { Icon(Icons.Default.FileUpload, contentDescription = null) }
            )

            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSourceLicenses),
                colors = ListItemDefaults.colors(),
                headlineContent = { Text(stringResource(R.string.settings_open_source_licenses)) },
                leadingContent = { Icon(Icons.AutoMirrored.Default.HelpOutline, contentDescription = null) }
            )
        }
    }

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_choose_theme_title),
                    style = MaterialTheme.typography.titleLarge
                )
                AppThemeMode.entries.forEach { mode ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                pendingThemeMode = mode
                            },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        trailingContent = {
                            if (pendingThemeMode == mode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showThemeSheet = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    Button(
                        onClick = {
                            onThemeModeChange(pendingThemeMode)
                            showThemeSheet = false
                        }
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}
