package com.music.bitchord.ui.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Injects a smooth radial bloom directly behind active icon/label bounds.
 *
 * @param isSelected Whether the destination tab is currently selected.
 * @param glowColor The target chrominance seed defined by the destination or extracted artwork.
 */
fun Modifier.dockHaloGlow(
    isSelected: Boolean,
    glowColor: Color
): Modifier = this.then(
    Modifier.drawBehind {
        if (isSelected) {
            val bloomRadius = size.maxDimension * 0.85f
            val centerOffset = Offset(size.width / 2f, size.height / 2f)

            // Outer diffused ambient bloom
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.38f),
                        glowColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = bloomRadius
                ),
                radius = bloomRadius,
                center = centerOffset
            )

            // Inner intense core halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.55f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = bloomRadius * 0.42f
                ),
                radius = bloomRadius * 0.42f,
                center = centerOffset
            )
        }
    }
)
