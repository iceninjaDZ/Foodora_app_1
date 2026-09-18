package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun DeliveryScreen(viewModel: FoodoraViewModel) {
    val orders by viewModel.orders.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var callDialogCustomer by remember { mutableStateOf<Pair<String, String>?>(null) }

    val deliveryOrders = orders.filter { it.type == OrderType.DELIVERY }
    val inTransitOrders = deliveryOrders.filter { it.status == OrderStatus.OUT_FOR_DELIVERY }
    val readyForDispatch = deliveryOrders.filter { it.status == OrderStatus.READY || it.status == OrderStatus.PREPARING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Fleet summary
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color.White)
                    }

                    Column {
                        Text(
                            text = StringsLocalization.get("active_deliveries", currentLang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${inTransitOrders.size} In Transit • ${readyForDispatch.size} Staged for Pickup",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "GPS Online",
                        color = StatusGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (deliveryOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active delivery orders. Create one via POS!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deliveryOrders) { order ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivery_card_${order.orderNumber}")
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

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FoodoraOrange.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "ETA ~${order.estimatedDeliveryMins ?: 20}m",
                                            color = FoodoraOrange,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                OrderStatusBadge(order.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = order.customerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (order.deliveryAddress.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = order.deliveryAddress,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Driver: ${order.driverName ?: "Unassigned"} • Items: ${order.items.size} (${profile.currencySymbol}${String.format("%.2f", order.total)})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        callDialogCustomer = order.customerName to (order.customerPhone.ifBlank { "+1 555-0199" })
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(StringsLocalization.get("call_customer", currentLang), fontSize = 11.sp)
                                }

                                if (order.status != OrderStatus.OUT_FOR_DELIVERY && order.status != OrderStatus.COMPLETED) {
                                    Button(
                                        onClick = { viewModel.dispatchDeliveryOrder(order.id, "Tariq Mansour") },
                                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(StringsLocalization.get("dispatch", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else if (order.status == OrderStatus.OUT_FOR_DELIVERY) {
                                    Button(
                                        onClick = { viewModel.markOrderDelivered(order.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(StringsLocalization.get("delivered", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Call customer dialog
        if (callDialogCustomer != null) {
            val (name, phone) = callDialogCustomer!!
            AlertDialog(
                onDismissRequest = { callDialogCustomer = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = StatusGreen)
                        Text("Call Customer", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Customer: $name", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Phone: $phone", fontSize = 13.sp, color = FoodoraOrange, fontWeight = FontWeight.SemiBold)
                        Text("Connected directly through Foodora VoIP dispatch gateway.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { callDialogCustomer = null },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text("End Call")
                    }
                }
            )
        }
    }
}
