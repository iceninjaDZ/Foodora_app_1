package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.model.OrderStatus
import com.example.ui.components.MetricCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.*

@Composable
fun DashboardScreen(viewModel: FoodoraViewModel) {
    val profile by viewModel.businessProfile.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()

    val totalRevenue = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.total }
    val totalExpense = expenses.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpense
    val activeOrdersCount = orders.count {
        it.status == OrderStatus.PENDING || it.status == OrderStatus.PREPARING || it.status == OrderStatus.READY || it.status == OrderStatus.OUT_FOR_DELIVERY
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business banner
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = profile.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FoodoraOrange.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = profile.plan.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FoodoraOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${profile.type.displayName} • ${profile.branchesCount} Branches Active",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(FoodoraOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // 4 KPI Metrics
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = StringsLocalization.get("kpi_revenue", currentLang),
                        value = "${profile.currencySymbol}${String.format("%.2f", totalRevenue)}",
                        subtitle = "+14.8% vs yesterday",
                        icon = Icons.Default.TrendingUp,
                        accentColor = StatusGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = StringsLocalization.get("kpi_orders", currentLang),
                        value = "$activeOrdersCount Active",
                        subtitle = "${orders.size} Total today",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = FoodoraOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = StringsLocalization.get("kpi_expenses", currentLang),
                        value = "${profile.currencySymbol}${String.format("%.2f", totalExpense)}",
                        subtitle = "5 entries recorded",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = StatusOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = StringsLocalization.get("kpi_profit", currentLang),
                        value = "${profile.currencySymbol}${String.format("%.2f", netProfit)}",
                        subtitle = "Net Margin: ${if (totalRevenue > 0) String.format("%.1f", (netProfit / totalRevenue) * 100) else "0"}%",
                        icon = Icons.Default.Savings,
                        accentColor = if (netProfit >= 0) StatusGreen else StatusRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Daily Analytics (Recharts) Highlight Card
        item {
            Surface(
                onClick = { viewModel.navigateTo(Screen.ANALYTICS) },
                shape = RoundedCornerShape(18.dp),
                color = FoodoraOrange.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FoodoraOrange.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_recharts_analytics_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FoodoraOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = StringsLocalization.get("revenue_trends_recharts", currentLang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FoodoraOrange
                                ) {
                                    Text(
                                        text = "NEW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Compare today's hourly revenue vs. yesterday & previous periods",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Analytics",
                        tint = FoodoraOrange
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = StringsLocalization.get("quick_actions", currentLang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.POS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_new_order"),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(StringsLocalization.get("new_order", currentLang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.KDS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_open_kds"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, modifier = Modifier.size(16.dp), tint = FoodoraOrange)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(StringsLocalization.get("open_kds", currentLang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Orders Tracker
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringsLocalization.get("live_orders", currentLang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = { viewModel.navigateTo(Screen.KDS) }) {
                    Text(
                        text = StringsLocalization.get("view_all", currentLang),
                        color = FoodoraOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(orders.take(4)) { order ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "#${order.orderNumber}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = FoodoraOrange
                            )
                            Text(
                                text = if (order.tableNumber != null) "Table ${order.tableNumber}" else order.type.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OrderStatusBadge(order.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = order.customerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = order.items.joinToString(", ") { "${it.quantity}x ${it.menuItem.nameEn}" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${profile.currencySymbol}${String.format("%.2f", order.total)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (order.status != OrderStatus.COMPLETED && order.status != OrderStatus.CANCELLED) {
                            FilledTonalButton(
                                onClick = { viewModel.advanceOrderStatus(order.id) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = FoodoraOrange.copy(alpha = 0.2f),
                                    contentColor = FoodoraOrange
                                )
                            ) {
                                Text(
                                    text = when (order.status) {
                                        OrderStatus.PENDING -> "Start Prep"
                                        OrderStatus.PREPARING -> "Mark Ready"
                                        OrderStatus.READY -> "Serve"
                                        OrderStatus.OUT_FOR_DELIVERY -> "Delivered"
                                        else -> "Next"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Selling items
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = StringsLocalization.get("top_selling", currentLang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                menuItems.take(3).forEach { dish ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dish.nameEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${profile.currencySymbol}${String.format("%.2f", dish.price)} • Prep: ${dish.prepTimeMinutes} mins",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "★ High Margin",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
