package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = PrPrimaryLight,
  onPrimary = Color.White,
  primaryContainer = PrSurfaceDark,
  onPrimaryContainer = PrTextPrimary,
  secondary = InstaPink,
  onSecondary = Color.White,
  secondaryContainer = PrCardDark,
  onSecondaryContainer = PrTextPrimary,
  tertiary = InstaOrange,
  background = PrNavyDark,
  onBackground = PrTextPrimary,
  surface = PrSurfaceDark,
  onSurface = PrTextPrimary,
  surfaceVariant = PrCardDark,
  onSurfaceVariant = PrTextSecondary,
  outline = PrBorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = PrPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFF3E8FF),
  onPrimaryContainer = Color(0xFF3B0764),
  secondary = InstaPink,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFE4E6),
  onSecondaryContainer = Color(0xFF881337),
  tertiary = InstaOrange,
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color.White,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to dark for high-tech auto machine feel
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
