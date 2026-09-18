package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.error.AppError
import com.example.data.error.ErrorCategory
import com.example.data.error.SyncStatus
import com.example.model.Language
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GlobalErrorBanner(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onShowDetails: (AppError) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (error != null) {
            val (bgColor, iconColor, icon) = when (error.category) {
                ErrorCategory.NETWORK_OFFLINE -> Triple(
                    Color(0xFF332005),
                    Color(0xFFFFB74D),
                    Icons.Default.WifiOff
                )
                ErrorCategory.NETWORK_TIMEOUT -> Triple(
                    Color(0xFF332305),
                    Color(0xFFFFD54F),
                    Icons.Default.HourglassBottom
                )
                ErrorCategory.FIREBASE_AUTH -> Triple(
                    Color(0xFF360C0C),
                    Color(0xFFFF5252),
                    Icons.Default.Lock
                )
                ErrorCategory.FIREBASE_FIRESTORE_SYNC -> Triple(
                    Color(0xFF2E0E18),
                    Color(0xFFFF4081),
                    Icons.Default.CloudOff
                )
                ErrorCategory.FIREBASE_QUOTA -> Triple(
                    Color(0xFF2B1B04),
                    Color(0xFFFFB300),
                    Icons.Default.Speed
                )
                ErrorCategory.SERVER_ERROR -> Triple(
                    Color(0xFF360C0C),
                    Color(0xFFFF5252),
                    Icons.Default.Dns
                )
                ErrorCategory.CLIENT_ERROR -> Triple(
                    Color(0xFF291A00),
                    Color(0xFFFF9800),
                    Icons.Default.Warning
                )
                ErrorCategory.UNKNOWN -> Triple(
                    Color(0xFF261E2A),
                    Color(0xFFCE93D8),
                    Icons.Default.ErrorOutline
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("global_error_banner"),
                shape = RoundedCornerShape(14.dp),
                color = bgColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, iconColor.copy(alpha = 0.4f)),
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(iconColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = error.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = iconColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = error.category.name.replace("_", " "),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = iconColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = error.userMessage,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("dismiss_error_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss error",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onShowDetails(error) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("error_details_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.75f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Technical Details",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        if (error.canRetry) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = onRetry,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = iconColor,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("error_retry_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = error.actionLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorDetailsDialog(
    error: AppError,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedToClipboard by remember { mutableStateOf(false) }

    val formattedTime = remember(error.timestampMillis) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.format(Date(error.timestampMillis))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = null,
                    tint = FoodoraOrange
                )
                Text(
                    text = "Exception Diagnostics",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Category:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = error.category.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FoodoraOrange
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Context:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = error.contextInfo,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Timestamp:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "Technical Stack / Message:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error.technicalDetails,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (copiedToClipboard) {
                    Text(
                        text = "✓ Copied details to clipboard",
                        fontSize = 11.sp,
                        color = StatusGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fullReport = """
                        --- Foodora Exception Diagnostics ---
                        Category: ${error.category.name}
                        Context: ${error.contextInfo}
                        Time: $formattedTime
                        Title: ${error.title}
                        User Message: ${error.userMessage}
                        Technical: ${error.technicalDetails}
                    """.trimIndent()
                    clipboardManager.setText(AnnotatedString(fullReport))
                    copiedToClipboard = true
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Details", fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontSize = 12.sp)
            }
        }
    )
}

@Composable
fun CloudSyncStatusChip(
    status: SyncStatus,
    isSyncing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, tintColor, label, icon) = when {
        isSyncing -> Quadruple(
            Color(0xFF0D2538),
            Color(0xFF4FC3F7),
            "Syncing...",
            Icons.Default.Sync
        )
        status == SyncStatus.SYNCED -> Quadruple(
            Color(0xFF0F2B1D),
            StatusGreen,
            "Cloud Synced",
            Icons.Default.CloudDone
        )
        status == SyncStatus.FAILED -> Quadruple(
            Color(0xFF331114),
            Color(0xFFFF5252),
            "Sync Error",
            Icons.Default.CloudOff
        )
        status == SyncStatus.OFFLINE -> Quadruple(
            Color(0xFF2C2416),
            Color(0xFFFFB74D),
            "Offline Mode",
            Icons.Default.WifiOff
        )
        else -> Quadruple(
            Color(0xFF1E2124),
            Color.LightGray,
            "Online",
            Icons.Default.Cloud
        )
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, tintColor.copy(alpha = 0.4f)),
        modifier = modifier.testTag("cloud_sync_chip")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = tintColor
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
