package com.ars.arsync.ui.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ars.arsync.ui.screens.components.TrackListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.search(it)
                    },
                    onSearch = { viewModel.search(it) },
                    active = true,
                    onActiveChange = { if (!it) showSearch = false },
                    placeholder = { Text("Search tracks...") }
                ) {
                    LazyColumn {
                        items(uiState.filteredTracks, key = { it.id }) { track ->
                            TrackListItem(
                                track = track,
                                isPlaying = false,
                                onClick = { onNavigateToPlayer(track.id) },
                                onFavorite = { viewModel.toggleFavorite(track) }
                            )
                        }
                    }
                }
            } else {
                TopAppBar(
                    title = { Text("Library") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = viewModel::cycleSortOrder) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(uiState.tracks, key = { it.id }) { track ->
                TrackListItem(
                    track = track,
                    isPlaying = uiState.currentTrackId == track.id && uiState.isPlaying,
                    onClick = {
                        viewModel.playTrack(track, uiState.tracks)
                        onNavigateToPlayer(track.id)
                    },
                    onFavorite = { viewModel.toggleFavorite(track) }
                )
            }
        }
    }
}
