package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.FoodoraOrange

data class AppModule(
    val screen: Screen,
    val titleKey: String,
    val defaultTitle: String,
    val icon: ImageVector,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllModulesSheet(
    viewModel: FoodoraViewModel,
    onDismiss: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    val modules = listOf(
        AppModule(Screen.DASHBOARD, "nav_dashboard", "Dashboard", Icons.Default.Dashboard),
        AppModule(Screen.POS, "nav_pos", "POS Register", Icons.Default.PointOfSale, "Live"),
        AppModule(Screen.KDS, "nav_kds", "Kitchen (KDS)", Icons.Default.SoupKitchen, "Active"),
        AppModule(Screen.TABLES, "nav_tables", "Tables & Floor", Icons.Default.TableBar),
        AppModule(Screen.MENU, "nav_menu", "Menu & Products", Icons.Default.RestaurantMenu),
        AppModule(Screen.INVENTORY, "nav_inventory", "Inventory & Stock", Icons.Default.Inventory2, "Alert"),
        AppModule(Screen.DELIVERY, "nav_delivery", "Delivery Fleet", Icons.Default.DeliveryDining),
        AppModule(Screen.CRM, "nav_crm", "Customer CRM", Icons.Default.PeopleAlt),
        AppModule(Screen.ANALYTICS, "nav_analytics", "Daily Analytics", Icons.Default.TrendingUp, "Recharts"),
        AppModule(Screen.REPORTS, "nav_reports", "P&L & Reports", Icons.Default.BarChart),
        AppModule(Screen.STAFF, "nav_staff", "Staff & Permissions", Icons.Default.Badge),
        AppModule(Screen.QR_MENU, "nav_qr", "QR Digital Menu", Icons.Default.QrCode2, "New"),
        AppModule(Screen.SETTINGS, "nav_settings", "Settings & SaaS", Icons.Default.Settings)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Foodora Ecosystem",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Switch between restaurant management modules",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(modules) { module ->
                    val isSelected = currentScreen == module.screen
                    val label = StringsLocalization.get(module.titleKey, currentLanguage)

                    Surface(
                        onClick = {
                            viewModel.navigateTo(module.screen)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) FoodoraOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.height(100.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = module.icon,
                                        contentDescription = label,
                                        tint = if (isSelected) Color.White else FoodoraOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                if (module.badge != null) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 6.dp, y = (-4).dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(FoodoraOrange)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = module.badge,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
