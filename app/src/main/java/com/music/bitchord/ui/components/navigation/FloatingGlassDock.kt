package com.music.bitchord.ui.components.navigation

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

/**
 * Floating glassmorphic dock hosting 5 navigation destinations.
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun FloatingGlassDock(
    currentDestination: DockDestination,
    onDestinationSelected: (DockDestination) -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier,
    ambientArtworkColor: Color? = null,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isOled: Boolean = false,
    isPlayingContinuousVideo: Boolean = false
) {
    val haptic = LocalHapticFeedback.current

    // Dynamically animated background tinting based on content passing underneath
    val targetTint = DockGlassmorphicTokens.resolveBackdropTint(
        isDarkTheme = isDarkTheme,
        ambientArtworkColor = ambientArtworkColor,
        isOled = isOled
    )
    val animatedBackdropTint by animateColorAsState(
        targetValue = targetTint,
        animationSpec = tween(durationMillis = 350),
        label = "animatedBackdropTint"
    )

    val shape = DockGlassmorphicTokens.DockShape
    val specularBrush = remember(isDarkTheme) {
        DockGlassmorphicTokens.specularBorderBrush(isDarkTheme)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(
                bottom = DockGlassmorphicTokens.BottomFloatingMargin,
                start = DockGlassmorphicTokens.HorizontalMargin,
                end = DockGlassmorphicTokens.HorizontalMargin
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Island Container
        Box(
            modifier = Modifier
                .widthIn(max = DockGlassmorphicTokens.MaxDockWidth)
                .fillMaxWidth()
                .height(DockGlassmorphicTokens.DockHeight)
                // GPU Offscreen isolation prevents video/canvas frame rebuild stalls
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    this.shape = shape
                    clip = true
                    shadowElevation = 18f
                }
                .then(
                    // Conditional rendering: Hardware Haze blur vs. API 26-30 / Video fallback
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isPlayingContinuousVideo && hazeState != null) {
                        Modifier
                            .hazeEffect(
                                state = hazeState,
                                style = HazeMaterials.ultraThin()
                            )
                            .background(animatedBackdropTint)
                    } else {
                        Modifier.background(
                            brush = DockGlassmorphicTokens.legacyFallbackGradient(isDarkTheme, isOled)
                        )
                    }
                )
                // Tactile 1px Specular Border Rim
                .border(
                    width = DockGlassmorphicTokens.SpecularStrokeWidth,
                    brush = specularBrush,
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockDestination.entries.forEach { destination ->
                    val isSelected = destination == currentDestination
                    DockItem(
                        destination = destination,
                        isSelected = isSelected,
                        ambientAccent = ambientArtworkColor,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDestinationSelected(destination)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DockItem(
    destination: DockDestination,
    isSelected: Boolean,
    ambientAccent: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth spring micro-interaction on down-press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) {
            DockGlassmorphicTokens.PRESSED_SCALE_RATIO
        } else {
            DockGlassmorphicTokens.RESTING_SCALE_RATIO
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "itemTouchScale"
    )

    // Blend halo glow between route color and playing media palette
    val activeGlowColor = remember(destination, ambientAccent) {
        if (ambientAccent != null && ambientAccent != Color.Unspecified) {
            ColorUtils.blendARGB(
                destination.haloGlowColor.toArgb(),
                ambientAccent.toArgb(),
                0.40f
            ).let { Color(it) }
        } else {
            destination.haloGlowColor
        }
    }

    val animatedIconColor by animateColorAsState(
        targetValue = if (isSelected) activeGlowColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        animationSpec = tween(durationMillis = 220),
        label = "animatedIconColor"
    )

    val animatedLabelAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(durationMillis = 200),
        label = "animatedLabelAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clip(DockGlassmorphicTokens.DockShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Suppress default material square ripple for custom halo bloom
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon wrapper with ambient radial halo bloom
        Box(
            modifier = Modifier
                .size(34.dp)
                .dockHaloGlow(isSelected = isSelected, glowColor = activeGlowColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                contentDescription = stringResource(id = destination.titleRes),
                tint = animatedIconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = stringResource(id = destination.titleRes),
            color = if (isSelected) animatedIconColor else MaterialTheme.colorScheme.onSurface.copy(alpha = animatedLabelAlpha),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
