package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.error.AppError
import com.example.model.Language
import com.example.ui.components.AllModulesSheet
import com.example.ui.components.ErrorDetailsDialog
import com.example.ui.components.FoodoraBottomNav
import com.example.ui.components.FoodoraTopBar
import com.example.ui.components.GlobalErrorBanner
import com.example.ui.screens.*
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.FoodoraManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val foodoraViewModel: FoodoraViewModel = viewModel()
            val currentLang by foodoraViewModel.currentLanguage.collectAsState()
            val isLoggedIn by foodoraViewModel.isLoggedIn.collectAsState()
            val layoutDirection = if (currentLang == Language.AR) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                FoodoraManagerTheme {
                    if (!isLoggedIn) {
                        LoginScreen(viewModel = foodoraViewModel)
                    } else {
                        FoodoraApp(viewModel = foodoraViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodoraApp(viewModel: FoodoraViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentError by viewModel.currentError.collectAsState()
    var showModulesSheet by remember { mutableStateOf(false) }
    var selectedErrorForDetails by remember { mutableStateOf<AppError?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FoodoraTopBar(
                viewModel = viewModel,
                onOpenNavMenu = { showModulesSheet = true }
            )
        },
        bottomBar = {
            FoodoraBottomNav(
                currentScreen = currentScreen,
                onSelectScreen = { screen ->
                    viewModel.navigateTo(screen)
                },
                onOpenMoreMenu = { showModulesSheet = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Global Exception & Sync Failure Feedback Banner
            GlobalErrorBanner(
                error = currentError,
                onDismiss = { viewModel.dismissError() },
                onRetry = { viewModel.retryLastFailedOperation() },
                onShowDetails = { error -> selectedErrorForDetails = error }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentScreen) {
                    Screen.DASHBOARD -> DashboardScreen(viewModel)
                    Screen.POS -> PosScreen(viewModel)
                    Screen.KDS -> KdsScreen(viewModel)
                    Screen.TABLES -> TablesScreen(viewModel)
                    Screen.MENU -> MenuScreen(viewModel)
                    Screen.INVENTORY -> InventoryScreen(viewModel)
                    Screen.DELIVERY -> DeliveryScreen(viewModel)
                    Screen.CRM -> CrmScreen(viewModel)
                    Screen.REPORTS -> ReportsScreen(viewModel)
                    Screen.ANALYTICS -> DailyAnalyticsScreen(viewModel)
                    Screen.STAFF -> StaffScreen(viewModel)
                    Screen.QR_MENU -> QrMenuScreen(viewModel)
                    Screen.SETTINGS -> SettingsScreen(viewModel)
                }
            }
        }
    }

    // Technical Error Diagnostics Dialog
    selectedErrorForDetails?.let { error ->
        ErrorDetailsDialog(
            error = error,
            onDismiss = { selectedErrorForDetails = null }
        )
    }

    // All Modules Sheet
    if (showModulesSheet) {
        AllModulesSheet(
            viewModel = viewModel,
            onDismiss = { showModulesSheet = false }
        )
    }
}
