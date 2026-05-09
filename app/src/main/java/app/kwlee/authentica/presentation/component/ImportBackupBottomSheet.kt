package app.kwlee.authentica.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.kwlee.authentica.R
import app.kwlee.authentica.presentation.backup.BackupUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBackupBottomSheet(
    state: BackupUiState,
    onDismiss: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onImport: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.backup_import_title), style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = state.importPassword,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.backup_import_password_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Text(stringResource(R.string.backup_import_password_hint))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = onImport,
                    enabled = state.canImport
                ) {
                    Text(stringResource(R.string.backup_import_button))
                }
            }
        }
    }
}
