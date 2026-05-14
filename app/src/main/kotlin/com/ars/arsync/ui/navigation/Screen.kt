package com.ars.arsync.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Player : Screen("player/{trackId}") {
        fun createRoute(trackId: Long) = "player/$trackId"
    }
    data object Library : Screen("library")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Import : Screen("import")
    data object Favorites : Screen("favorites")
    data object RecentlyPlayed : Screen("recently_played")
    data object Playlists : Screen("playlists")
    data object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
}

sealed class NavEvent {
    data class NavigateToPlayer(val trackId: Long) : NavEvent()
    data object NavigateToImport : NavEvent()
    data class NavigateToPlaylist(val playlistId: Long) : NavEvent()
}
