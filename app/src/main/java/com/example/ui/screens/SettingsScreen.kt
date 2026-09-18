package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.data.error.ErrorCategory
import com.example.data.error.SyncStatus
import com.example.model.BusinessType
import com.example.model.Language
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(viewModel: FoodoraViewModel) {
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncTime by viewModel.lastSyncTimestamp.collectAsState()
    val errorHistory by viewModel.errorHistory.collectAsState()

    var businessName by remember { mutableStateOf(profile.name) }
    var currencySymbol by remember { mutableStateOf(profile.currencySymbol) }
    var taxRate by remember { mutableStateOf(profile.taxRatePercent.toString()) }
    var showSavedMessage by remember { mutableStateOf(false) }
    var showHistoryDetails by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SaaS Subscription Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FoodoraOrange),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(FoodoraOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White)
                            }

                            Column {
                                Text(
                                    text = "Foodora Cloud SaaS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Plan: ${profile.plan.displayName} • Active",
                                    fontSize = 12.sp,
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = StatusGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Includes multi-terminal POS, live KDS station dispatch, table QR digital menu, inventory sync, and real-time P&L analytics.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Language & Localization Selection
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = FoodoraOrange)
                        Text(
                            text = StringsLocalization.get("language_select", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(Language.EN, "English", "LTR"),
                            Triple(Language.AR, "العربية", "RTL"),
                            Triple(Language.FR, "Français", "LTR")
                        ).forEach { (lang, label, dir) ->
                            val isSelected = currentLang == lang
                            Surface(
                                onClick = { viewModel.setLanguage(lang) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dir,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Business Profile Form
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Restaurant Configuration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = { Text("Currency Symbol") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = taxRate,
                            onValueChange = { taxRate = it },
                            label = { Text("Tax %") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Currency Quick Presets
                    Column {
                        Text(
                            text = "Currency Presets:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("د.ج", "DA", "DZD", "$", "€").forEach { curr ->
                                FilterChip(
                                    selected = currencySymbol == curr,
                                    onClick = { currencySymbol = curr },
                                    label = { Text(if (curr == "د.ج") "د.ج (DZD)" else curr, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Business Type Selector
                    Column {
                        Text(
                            text = "Business Concept:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                BusinessType.RESTAURANT,
                                BusinessType.CAFE,
                                BusinessType.BAKERY,
                                BusinessType.FAST_FOOD
                            ).forEach { bType ->
                                val isSelected = profile.type == bType
                                Surface(
                                    onClick = {
                                        viewModel.updateBusinessProfile(profile.copy(type = bType))
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) FoodoraOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Text(
                                        text = bType.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val newTax = taxRate.toDoubleOrNull() ?: 10.0
                            viewModel.updateBusinessProfile(
                                profile.copy(
                                    name = businessName,
                                    currencySymbol = currencySymbol,
                                    taxRatePercent = newTax
                                )
                            )
                            showSavedMessage = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_settings_button")
                    ) {
                        Text("Save Configuration", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Cloud Synchronization & Exception Diagnostics Card
        item {
            val formattedSyncTime = remember(lastSyncTime) {
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                sdf.format(Date(lastSyncTime))
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = FoodoraOrange)
                            Text(
                                text = "Cloud & Exception Diagnostics",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (syncStatus) {
                                SyncStatus.SYNCED -> StatusGreen.copy(alpha = 0.2f)
                                SyncStatus.SYNCING -> Color(0xFF0288D1).copy(alpha = 0.2f)
                                SyncStatus.FAILED -> Color(0xFFD32F2F).copy(alpha = 0.2f)
                                SyncStatus.OFFLINE -> Color(0xFFF57C00).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = syncStatus.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (syncStatus) {
                                    SyncStatus.SYNCED -> StatusGreen
                                    SyncStatus.SYNCING -> Color(0xFF0288D1)
                                    SyncStatus.FAILED -> Color(0xFFD32F2F)
                                    SyncStatus.OFFLINE -> Color(0xFFF57C00)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Global exception handling actively monitors Firebase Firestore sync, authentication state, and REST API network calls with automatic recovery.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Last Successful Sync: $formattedSyncTime",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { viewModel.triggerCloudSync() },
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("trigger_cloud_sync_button")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Syncing...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Now", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Simulate & Test Exception Handler:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Simulation Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.simulateException(ErrorCategory.NETWORK_OFFLINE) },
                                modifier = Modifier.weight(1f).height(34.dp).testTag("simulate_offline_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Offline", fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateException(ErrorCategory.NETWORK_TIMEOUT) },
                                modifier = Modifier.weight(1f).height(34.dp).testTag("simulate_timeout_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Timeout", fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateException(ErrorCategory.FIREBASE_AUTH) },
                                modifier = Modifier.weight(1f).height(34.dp).testTag("simulate_auth_error_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Auth Error", fontSize = 10.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.simulateException(ErrorCategory.FIREBASE_FIRESTORE_SYNC) },
                                modifier = Modifier.weight(1f).height(34.dp).testTag("simulate_firestore_error_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Firestore Err", fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateException(ErrorCategory.FIREBASE_QUOTA) },
                                modifier = Modifier.weight(1f).height(34.dp).testTag("simulate_quota_error_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quota Limit", fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateException(ErrorCategory.SERVER_ERROR) },
                                modifier = Modifier.weight(1f).height(34.dp).testTag("simulate_server_error_button"),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("503 Server", fontSize = 10.sp)
                            }
                        }
                    }

                    if (errorHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Captured Errors Log (${errorHistory.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(
                                onClick = { viewModel.clearErrorHistory() },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Clear", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            errorHistory.take(3).forEach { err ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${err.category.name}: ${err.title}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = err.userMessage,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(err.timestampMillis)),
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // System & Architecture Information
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Architecture & Platform", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Stack: Flutter / Android Native Architecture", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Backend: REST API + PostgreSQL + Cloud Microservices", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Offline Resilience: Local Room Database & Cache", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Hardware: Thermal Receipt Printer + Barcode & Cash Drawer Integration", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Version: Foodora Manager v2.4.0 (Enterprise SaaS)", fontSize = 11.sp, color = FoodoraOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showSavedMessage) {
        AlertDialog(
            onDismissRequest = { showSavedMessage = false },
            title = { Text("Settings Saved", fontWeight = FontWeight.Bold) },
            text = { Text("Business configuration, currency, and tax rate updated across all active terminals.") },
            confirmButton = {
                Button(
                    onClick = { showSavedMessage = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("OK")
                }
            }
        )
    }
}
