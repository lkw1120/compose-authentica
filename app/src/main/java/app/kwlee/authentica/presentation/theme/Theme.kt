package app.kwlee.authentica.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = VaultOnPrimary,
    primaryContainer = VaultPrimaryContainer,
    onPrimaryContainer = VaultOnPrimaryContainer,
    secondary = VaultSecondary,
    onSecondary = VaultOnSecondary,
    secondaryContainer = VaultSecondaryContainer,
    onSecondaryContainer = VaultOnSecondaryContainer,
    tertiary = VaultTertiary,
    onTertiary = VaultOnTertiary,
    tertiaryContainer = VaultTertiaryContainer,
    onTertiaryContainer = VaultOnTertiaryContainer,
    error = VaultError,
    onError = VaultOnError,
    errorContainer = VaultErrorContainer,
    onErrorContainer = VaultOnErrorContainer,
    background = VaultBackground,
    onBackground = VaultOnBackground,
    surface = VaultSurface,
    onSurface = VaultOnSurface,
    surfaceVariant = VaultSurfaceVariant,
    onSurfaceVariant = VaultOnSurfaceVariant,
    outline = VaultOutline,
    outlineVariant = VaultOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB94A74),              // Sakura
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E5),
    onPrimaryContainer = Color(0xFF3F001F),
    secondary = Color(0xFF7A5A68),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E5),
    onSecondaryContainer = Color(0xFF2F1622),
    tertiary = Color(0xFF815426),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDDBE),
    onTertiaryContainer = Color(0xFF2A1700),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F9),
    onBackground = Color(0xFF21191D),
    surface = Color(0xFFFFF8F9),
    onSurface = Color(0xFF21191D),
    surfaceVariant = Color(0xFFF3DDE4),
    onSurfaceVariant = Color(0xFF524349),
    outline = Color(0xFF847278),
    outlineVariant = Color(0xFFD6C1C8)
)

@Composable
fun AuthenticaTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
