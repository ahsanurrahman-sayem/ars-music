package com.ars.arsync.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ars.arsync.ui.navigation.ArSyncNavHost
import com.ars.arsync.ui.navigation.NavEvent
import com.ars.arsync.ui.theme.ArSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            val navEvent = viewModel.navEvent.collectAsStateWithLifecycle(initialValue = null)

            ArSyncTheme(dynamicColor = uiState.value.dynamicColorEnabled) {
                ArSyncNavHost(
                    navEvent = navEvent.value,
                    onNavEventConsumed = viewModel::clearNavEvent
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                uri?.let { viewModel.handleImportUri(it) }
            }
            Intent.ACTION_VIEW -> {
                intent.data?.let { viewModel.handleImportUri(it) }
            }
        }
    }
}
