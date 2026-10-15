package com.music.bitchord.ui.components.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.music.bitchord.R

/**
 * Encapsulates the 5 primary destinations displayed within the floating dock.
 * Each destination holds distinct resting/selected vector icons and a localized
 * ambient halo glow color that blooms during active selection.
 */
enum class DockDestination(
    val route: String,
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val haloGlowColor: Color
) {
    HOME(
        route = "home",
        titleRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        haloGlowColor = Color(0xFFFFB300) // Radiant Amber bloom
    ),
    VIDEOS(
        route = "videos",
        titleRes = R.string.nav_videos,
        selectedIcon = Icons.Filled.PlayCircle,
        unselectedIcon = Icons.Outlined.PlayCircleOutline,
        haloGlowColor = Color(0xFF00E5FF) // Electric Cyan bloom
    ),
    EXPLORE(
        route = "explore",
        titleRes = R.string.nav_explore,
        selectedIcon = Icons.Filled.CompassCalibration,
        unselectedIcon = Icons.Outlined.CompassCalibration,
        haloGlowColor = Color(0xFF00E676) // Vivid Emerald bloom
    ),
    BOOKS(
        route = "books",
        titleRes = R.string.nav_books,
        selectedIcon = Icons.Filled.AutoStories,
        unselectedIcon = Icons.Outlined.AutoStories,
        haloGlowColor = Color(0xFFB388FF) // Lavender Violet bloom
    ),
    ME(
        route = "me",
        titleRes = R.string.nav_me,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        haloGlowColor = Color(0xFFFF7043) // Warm Coral bloom
    );

    companion object {
        fun fromRoute(route: String?): DockDestination =
            entries.find { it.route.equals(route, ignoreCase = true) } ?: HOME
    }
}
