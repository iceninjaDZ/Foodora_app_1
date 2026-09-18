package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.model.TableStatus
import com.example.model.UserRole
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodoraTopBar(
    viewModel: FoodoraViewModel,
    onOpenNavMenu: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var showRoleDialog by remember { mutableStateOf(false) }
    var showNotifDialog by remember { mutableStateOf(false) }
    var showLangMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left branding + module launcher
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onOpenNavMenu,
                    modifier = Modifier.testTag("nav_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu Navigation",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FoodoraOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "FOODORA",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = FoodoraOrange
                    )
                    Text(
                        text = "MANAGER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right actions: Active Role, Notifications, Lang, Dark/Light
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Role Badge Button
                Surface(
                    onClick = { showRoleDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    color = FoodoraOrange.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FoodoraOrange.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("role_switcher_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FoodoraOrange)
                        )
                        Text(
                            text = currentRole.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FoodoraOrange
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = FoodoraOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Notification bell with badge
                Box {
                    IconButton(
                        onClick = { showNotifDialog = true },
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = if (unreadCount > 0) FoodoraOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 6.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(StatusRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadCount",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Language Selector
                Box {
                    IconButton(
                        onClick = { showLangMenu = true },
                        modifier = Modifier.testTag("language_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("English (EN)") },
                            onClick = {
                                viewModel.setLanguage(Language.EN)
                                showLangMenu = false
                            },
                            leadingIcon = {
                                if (currentLanguage == Language.EN) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = FoodoraOrange)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("العربية (AR • RTL)") },
                            onClick = {
                                viewModel.setLanguage(Language.AR)
                                showLangMenu = false
                            },
                            leadingIcon = {
                                if (currentLanguage == Language.AR) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = FoodoraOrange)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Français (FR)") },
                            onClick = {
                                viewModel.setLanguage(Language.FR)
                                showLangMenu = false
                            },
                            leadingIcon = {
                                if (currentLanguage == Language.FR) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = FoodoraOrange)
                                }
                            }
                        )
                    }
                }

                // Dark/Light mode toggle
                IconButton(
                    onClick = { viewModel.toggleDarkMode() },
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark/Light Mode",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sign Out / Switch Account
                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.testTag("logout_topbar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout / Switch Account",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showRoleDialog) {
        RoleSwitchDialog(
            currentRole = currentRole,
            onSelectRole = { role ->
                viewModel.switchRole(role)
                showRoleDialog = false
            },
            onDismiss = { showRoleDialog = false }
        )
    }

    if (showNotifDialog) {
        NotificationDialog(
            viewModel = viewModel,
            onDismiss = { showNotifDialog = false }
        )
    }
}

@Composable
fun RoleSwitchDialog(
    currentRole: UserRole,
    onSelectRole: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = FoodoraOrange)
                Text(text = "Switch User Role", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Test Foodora Manager from different operational perspectives:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                val roleInfo = listOf(
                    Triple(UserRole.OWNER, "Full business access, analytics, financial reports", Icons.Default.VerifiedUser),
                    Triple(UserRole.MANAGER, "Manage staff, menu, tables, inventory & settings", Icons.Default.ManageAccounts),
                    Triple(UserRole.CASHIER, "Fast register POS, payments, receipts & orders", Icons.Default.PointOfSale),
                    Triple(UserRole.WAITER, "Table floor plan, taking orders & bill requests", Icons.Default.RoomService),
                    Triple(UserRole.KITCHEN, "Live Kitchen Display System (KDS) & order bump", Icons.Default.OutdoorGrill),
                    Triple(UserRole.DELIVERY, "Fleet delivery dispatch, address & order tracking", Icons.Default.TwoWheeler)
                )

                roleInfo.forEach { (role, desc, icon) ->
                    val isSelected = role == currentRole
                    Surface(
                        onClick = { onSelectRole(role) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) FoodoraOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else FoodoraOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FoodoraOrange)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = FoodoraOrange)
            }
        }
    )
}

@Composable
fun NotificationDialog(
    viewModel: FoodoraViewModel,
    onDismiss: () -> Unit
) {
    val notifs by viewModel.notifications.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = FoodoraOrange)
                    Text(text = "Live Alerts (${notifs.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                if (notifs.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAllNotifications() }) {
                        Text("Clear All", fontSize = 12.sp, color = StatusRed)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (notifs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No new alerts. Operations running smoothly.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    notifs.forEach { notif ->
                        val icon = when (notif.type) {
                            "ORDER" -> Icons.Default.ReceiptLong
                            "KDS" -> Icons.Default.SoupKitchen
                            "STOCK" -> Icons.Default.Warning
                            "DELIVERY" -> Icons.Default.DeliveryDining
                            else -> Icons.Default.Info
                        }
                        val tint = when (notif.type) {
                            "STOCK" -> StatusRed
                            "KDS" -> StatusOrange
                            "DELIVERY" -> StatusBlue
                            else -> StatusGreen
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(tint.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = notif.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = FoodoraOrange)
            }
        }
    )
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (color, text) = when (status) {
        OrderStatus.PENDING -> StatusOrange to "Pending"
        OrderStatus.PREPARING -> StatusBlue to "Preparing"
        OrderStatus.READY -> StatusGreen to "Ready"
        OrderStatus.SERVED -> Color(0xFF8B5CF6) to "Served"
        OrderStatus.OUT_FOR_DELIVERY -> FoodoraOrange to "Delivering"
        OrderStatus.COMPLETED -> Color.Gray to "Completed"
        OrderStatus.CANCELLED -> StatusRed to "Cancelled"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun TableStatusBadge(status: TableStatus) {
    val (color, text) = when (status) {
        TableStatus.AVAILABLE -> StatusGreen to "Available"
        TableStatus.OCCUPIED -> StatusOrange to "Occupied"
        TableStatus.RESERVED -> StatusBlue to "Reserved"
        TableStatus.BILL_REQUESTED -> StatusRed to "Bill Requested"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun FoodoraBottomNav(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit,
    onOpenMoreMenu: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.DASHBOARD,
            onClick = { onSelectScreen(Screen.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FoodoraOrange,
                selectedTextColor = FoodoraOrange,
                indicatorColor = FoodoraOrange.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == Screen.POS,
            onClick = { onSelectScreen(Screen.POS) },
            icon = { Icon(Icons.Default.PointOfSale, contentDescription = "POS") },
            label = { Text("POS", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FoodoraOrange,
                selectedTextColor = FoodoraOrange,
                indicatorColor = FoodoraOrange.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == Screen.KDS,
            onClick = { onSelectScreen(Screen.KDS) },
            icon = { Icon(Icons.Default.SoupKitchen, contentDescription = "KDS") },
            label = { Text("KDS", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FoodoraOrange,
                selectedTextColor = FoodoraOrange,
                indicatorColor = FoodoraOrange.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == Screen.TABLES,
            onClick = { onSelectScreen(Screen.TABLES) },
            icon = { Icon(Icons.Default.TableBar, contentDescription = "Tables") },
            label = { Text("Tables", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FoodoraOrange,
                selectedTextColor = FoodoraOrange,
                indicatorColor = FoodoraOrange.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onOpenMoreMenu,
            icon = { Icon(Icons.Default.Apps, contentDescription = "All Apps") },
            label = { Text("More", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FoodoraOrange,
                selectedTextColor = FoodoraOrange,
                indicatorColor = FoodoraOrange.copy(alpha = 0.15f)
            )
        )
    }
}
