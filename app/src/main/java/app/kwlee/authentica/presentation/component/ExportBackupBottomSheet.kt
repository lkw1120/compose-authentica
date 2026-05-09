package app.kwlee.authentica.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.kwlee.authentica.R
import app.kwlee.authentica.presentation.backup.BackupUiState
import app.kwlee.authentica.presentation.backup.PasswordStrengthLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBackupBottomSheet(
    state: BackupUiState,
    onDismiss: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onExport: () -> Unit
) {
    val strength = state.exportPasswordStrength
    val strengthColor = when (strength.label) {
        PasswordStrengthLabel.WEAK -> MaterialTheme.colorScheme.error
        PasswordStrengthLabel.MEDIUM -> MaterialTheme.colorScheme.tertiary
        PasswordStrengthLabel.STRONG -> MaterialTheme.colorScheme.primary
    }
    val strengthLabelRes = when (strength.label) {
        PasswordStrengthLabel.WEAK -> R.string.password_strength_weak
        PasswordStrengthLabel.MEDIUM -> R.string.password_strength_medium
        PasswordStrengthLabel.STRONG -> R.string.password_strength_strong
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.backup_export_title), style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = state.exportPassword,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.backup_export_password_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.exportPasswordConfirm,
                onValueChange = onConfirmPasswordChange,
                label = { Text(stringResource(R.string.backup_export_confirm_password_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            LinearProgressIndicator(
                progress = { strength.progress },
                modifier = Modifier.fillMaxWidth(),
                color = strengthColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
            Text(stringResource(R.string.backup_export_password_strength, stringResource(strengthLabelRes)))
            Text(stringResource(R.string.backup_export_password_hint))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = onExport,
                    enabled = state.canExport
                ) {
                    Text(stringResource(R.string.backup_export_button))
                }
            }
        }
    }
}
