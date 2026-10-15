// --- MODIFICATION SNIPPET: com/music/bitchord/ui/player/MiniPlayer.kt ---

package com.music.bitchord.ui.player

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.music.bitchord.ui.components.navigation.DockGlassmorphicTokens

/**
 * Calculates the exact bottom padding required for the MiniPlayer so it floats
 * seamlessly above the FloatingGlassDock without overlapping.
 *
 * Offset = NavigationBar Inset + Dock Height (64dp) + Dock Bottom Margin (12dp) + Visual Clearance Gap (8dp)
 */
@Composable
fun Modifier.floatingAboveDockPadding(isDockVisible: Boolean): Modifier {
    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalDockOffset = if (isDockVisible) {
        navBarsBottom + DockGlassmorphicTokens.DockHeight + DockGlassmorphicTokens.BottomFloatingMargin + 8.dp
    } else {
        navBarsBottom + 8.dp
    }

    return this.then(
        Modifier.padding(bottom = totalDockOffset)
    )
}
