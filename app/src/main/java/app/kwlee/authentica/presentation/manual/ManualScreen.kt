package app.kwlee.authentica.presentation.manual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.kwlee.authentica.R
import app.kwlee.authentica.domain.model.OtpAlgorithm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(
    onBack: () -> Unit,
    onSave: (
        issuer: String,
        accountName: String,
        secret: String,
        digits: Int,
        period: Int,
        algorithm: OtpAlgorithm
    ) -> Unit
) {
    var issuer by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    var digitsText by remember { mutableStateOf("6") }
    var periodText by remember { mutableStateOf("30") }
    var algorithm by remember { mutableStateOf(OtpAlgorithm.SHA1) }

    val digitsValid = digitsText.toIntOrNull()?.let { it in 6..8 } ?: false
    val periodValid = periodText.toIntOrNull()?.let { it > 0 } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manual_title)) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.manual_section_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.manual_section_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.manual_section_account),
                style = MaterialTheme.typography.titleSmall
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = issuer,
                    onValueChange = { issuer = it },
                    label = { Text(stringResource(R.string.manual_field_issuer)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text(stringResource(R.string.manual_field_account_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text(stringResource(R.string.manual_field_secret)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = stringResource(R.string.manual_section_code_options),
                style = MaterialTheme.typography.titleSmall
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = digitsText,
                    onValueChange = { digitsText = it.filter(Char::isDigit) },
                    label = { Text(stringResource(R.string.manual_field_digits)) },
                    singleLine = true,
                    isError = digitsText.isNotEmpty() && !digitsValid,
                    supportingText = if (digitsText.isNotEmpty() && !digitsValid) {
                        { Text(stringResource(R.string.manual_digits_error)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = periodText,
                    onValueChange = { periodText = it.filter(Char::isDigit) },
                    label = { Text(stringResource(R.string.manual_field_period)) },
                    singleLine = true,
                    isError = periodText.isNotEmpty() && !periodValid,
                    supportingText = if (periodText.isNotEmpty() && !periodValid) {
                        { Text(stringResource(R.string.manual_period_error)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OtpAlgorithm.entries.forEach { item ->
                        FilterChip(
                            selected = item == algorithm,
                            onClick = { algorithm = item },
                            label = { Text(item.name) }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onSave(
                        issuer,
                        accountName,
                        secret,
                        digitsText.toIntOrNull() ?: 6,
                        periodText.toIntOrNull() ?: 30,
                        algorithm
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = issuer.isNotBlank() && accountName.isNotBlank() && secret.isNotBlank() && digitsValid && periodValid
            ) {
                Text(stringResource(R.string.manual_save_button))
            }
        }
    }
}
