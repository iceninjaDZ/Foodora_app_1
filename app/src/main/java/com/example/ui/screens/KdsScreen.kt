package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.model.OrderStatus
import com.example.model.OrderType
import com.example.ui.components.OrderStatusBadge
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun KdsScreen(viewModel: FoodoraViewModel) {
    val orders by viewModel.orders.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var statusFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var soundAlertTriggered by remember { mutableStateOf(false) }

    val activeOrders = orders.filter {
        it.status == OrderStatus.PENDING || it.status == OrderStatus.PREPARING || it.status == OrderStatus.READY
    }

    val displayedOrders = if (statusFilter == null) {
        activeOrders
    } else {
        activeOrders.filter { it.status == statusFilter }
    }

    val pendingCount = orders.count { it.status == OrderStatus.PENDING }
    val prepCount = orders.count { it.status == OrderStatus.PREPARING }
    val readyCount = orders.count { it.status == OrderStatus.READY }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Kitchen station header & audio alert bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(FoodoraOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = Color.White)
                    }

                    Column {
                        Text(
                            text = StringsLocalization.get("kds_title", currentLang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Station: Hot Line & Pass • ${activeOrders.size} Live Tickets",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { soundAlertTriggered = !soundAlertTriggered },
                    modifier = Modifier.testTag("kds_sound_button")
                ) {
                    Icon(
                        imageVector = if (soundAlertTriggered) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Kitchen Sound",
                        tint = if (soundAlertTriggered) FoodoraOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips: All, Pending, Preparing, Ready
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = statusFilter == null,
                    onClick = { statusFilter = null },
                    label = { Text("All Active (${activeOrders.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FoodoraOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = statusFilter == OrderStatus.PENDING,
                    onClick = { statusFilter = OrderStatus.PENDING },
                    label = { Text("${StringsLocalization.get("ticket_pending", currentLang)} ($pendingCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = statusFilter == OrderStatus.PREPARING,
                    onClick = { statusFilter = OrderStatus.PREPARING },
                    label = { Text("${StringsLocalization.get("ticket_prep", currentLang)} ($prepCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = statusFilter == OrderStatus.READY,
                    onClick = { statusFilter = OrderStatus.READY },
                    label = { Text("${StringsLocalization.get("ticket_ready", currentLang)} ($readyCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StatusGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ticket Cards List
        if (displayedOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All kitchen tickets completed!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "New orders from POS or QR will appear here instantly.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedOrders) { order ->
                    val elapsedMinutes = ((System.currentTimeMillis() - order.createdAtMillis) / (60 * 1000)).toInt().coerceAtLeast(1)
                    val (timerColor, timerBg) = when {
                        elapsedMinutes > 15 -> StatusRed to StatusRedBg
                        elapsedMinutes > 8 -> StatusOrange to StatusOrangeBg
                        else -> StatusGreen to StatusGreenBg
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (elapsedMinutes > 15) StatusRed else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kds_ticket_${order.orderNumber}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header: Order #, Type/Table, Timer
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
                                        fontSize = 18.sp,
                                        color = FoodoraOrange
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = if (order.tableNumber != null) "Table ${order.tableNumber}" else order.type.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Timer badge
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = timerBg
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = timerColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${elapsedMinutes}m",
                                            color = timerColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Customer: ${order.customerName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Items List
                            order.items.forEach { cartItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(FoodoraOrange.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${cartItem.quantity}x",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = FoodoraOrange
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = cartItem.menuItem.nameEn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (cartItem.notes.isNotBlank()) {
                                                Text(
                                                    text = "Note: ${cartItem.notes}",
                                                    fontSize = 11.sp,
                                                    color = StatusOrange,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bottom actions: Status badge & Bump action button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OrderStatusBadge(order.status)

                                Button(
                                    onClick = { viewModel.advanceOrderStatus(order.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (order.status) {
                                            OrderStatus.PENDING -> StatusBlue
                                            OrderStatus.PREPARING -> StatusGreen
                                            else -> FoodoraOrange
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = when (order.status) {
                                            OrderStatus.PENDING -> StringsLocalization.get("start_prep", currentLang)
                                            OrderStatus.PREPARING -> StringsLocalization.get("bump_order", currentLang)
                                            OrderStatus.READY -> StringsLocalization.get("ticket_served", currentLang)
                                            else -> "Next"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
