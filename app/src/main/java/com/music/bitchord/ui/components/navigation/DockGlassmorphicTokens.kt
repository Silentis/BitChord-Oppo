package com.music.bitchord.ui.components.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

/**
 * Design tokens and mathematical blend engines for BitChord's dynamic glassmorphism.
 */
object DockGlassmorphicTokens {
    // Dock Dimensions & Spacing
    val DockHeight = 64.dp
    val MaxDockWidth = 440.dp
    val CapsuleCornerRadius = 36.dp
    val DockShape = RoundedCornerShape(CapsuleCornerRadius)
    val BottomFloatingMargin = 12.dp
    val HorizontalMargin = 16.dp
    val SpecularStrokeWidth = 1.dp
    val BlurRadius = 26.dp

    // Micro-interaction springs
    const val PRESSED_SCALE_RATIO = 0.94f
    const val RESTING_SCALE_RATIO = 1.0f

    /**
     * Resolves the tactile 1px specular highlight rim.
     * The top of the pill receives a brighter bounce highlight; the bottom fades
     * into a soft alpha baseline to establish tangible light elevation.
     */
    fun specularBorderBrush(isDarkTheme: Boolean): Brush {
        val topAlpha = if (isDarkTheme) 0.22f else 0.45f
        val bottomAlpha = if (isDarkTheme) 0.04f else 0.08f
        return Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = topAlpha),
                Color.White.copy(alpha = bottomAlpha)
            )
        )
    }

    /**
     * Adapts dock tint to content behind it:
     * - Dark / Pure Black mode: Smoked deep charcoal preserving under-scrolling clarity.
     * - Warm / Colorful Media mode: Ingests artwork chrominance and blends a 25% tint bloom.
     * - Light / Vibrant content mode: Milky translucent glass with low opacity.
     */
    fun resolveBackdropTint(
        isDarkTheme: Boolean,
        ambientArtworkColor: Color?,
        isOled: Boolean = false
    ): Color {
        if (ambientArtworkColor != null && ambientArtworkColor != Color.Unspecified) {
            val baseBackground = if (isDarkTheme) 0xFF121216.toInt() else 0xFFFFFFFF.toInt()
            val blendedArgb = ColorUtils.blendARGB(
                baseBackground,
                ambientArtworkColor.toArgb(),
                if (isDarkTheme) 0.28f else 0.16f
            )
            val alphaTarget = if (isDarkTheme) 0.74f else 0.60f
            return Color(blendedArgb).copy(alpha = alphaTarget)
        }

        return when {
            isOled -> Color(0xFF070709).copy(alpha = 0.88f)
            isDarkTheme -> Color(0xFF131317).copy(alpha = 0.72f)
            else -> Color(0xFFF9F9FB).copy(alpha = 0.58f)
        }
    }

    /**
     * Fallback scrim brush for Android 8.0–11 (API 26–30) where hardware RenderEffect
     * blur pipelines are unavailable.
     */
    fun legacyFallbackGradient(isDarkTheme: Boolean, isOled: Boolean): Brush {
        val topColor = when {
            isOled -> Color(0xEB070709)
            isDarkTheme -> Color(0xE01A1A22)
            else -> Color(0xEBFFFFFF)
        }
        val bottomColor = when {
            isOled -> Color(0xF5040406)
            isDarkTheme -> Color(0xF0101014)
            else -> Color(0xF5F0F0F5)
        }
        return Brush.verticalGradient(listOf(topColor, bottomColor))
    }
}
