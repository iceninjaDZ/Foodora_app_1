package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.model.*
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(viewModel: FoodoraViewModel) {
    val categories by viewModel.categories.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartOrderType by viewModel.cartOrderType.collectAsState()
    val cartTableNumber by viewModel.cartTableNumber.collectAsState()
    val cartCustomerName by viewModel.cartCustomerName.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showCartSheet by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var lastPlacedOrder by remember { mutableStateOf<Order?>(null) }
    var showReceiptDialog by remember { mutableStateOf(false) }

    val filteredItems = menuItems.filter { item ->
        val matchesCategory = selectedCategoryId == null || item.categoryId == selectedCategoryId
        val matchesSearch = searchQuery.isBlank() ||
                item.nameEn.contains(searchQuery, ignoreCase = true) ||
                item.nameAr.contains(searchQuery, ignoreCase = true) ||
                item.nameFr.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val cartItemCount = cart.sumOf { it.quantity }
    val cartSubtotal = cart.sumOf { it.menuItem.price * it.quantity }
    val cartTax = cartSubtotal * (profile.taxRatePercent / 100.0)
    val cartTotal = cartSubtotal + cartTax

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Order Type Selector + Table selector
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    OrderType.DINE_IN to StringsLocalization.get("dine_in", currentLang),
                    OrderType.TAKEOUT to StringsLocalization.get("takeout", currentLang),
                    OrderType.DELIVERY to StringsLocalization.get("delivery", currentLang)
                ).forEach { (type, label) ->
                    val isSelected = cartOrderType == type
                    Surface(
                        onClick = { viewModel.setCartOrderType(type) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) FoodoraOrange else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Table picker row (if Dine In)
        if (cartOrderType == OrderType.DINE_IN) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${StringsLocalization.get("table", currentLang)}:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items((1..9).toList()) { tableNum ->
                        val isSelected = cartTableNumber == tableNum
                        Surface(
                            onClick = { viewModel.setCartTableNumber(tableNum) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) FoodoraOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Text(
                                text = "T-$tableNum",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(StringsLocalization.get("search_items", currentLang), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pos_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FoodoraOrange,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { selectedCategoryId = null },
                    label = { Text(StringsLocalization.get("all_categories", currentLang), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FoodoraOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(categories) { category ->
                val label = when (currentLang) {
                    Language.AR -> category.nameAr
                    Language.FR -> category.nameFr
                    Language.EN -> category.nameEn
                }
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { selectedCategoryId = category.id },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FoodoraOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(filteredItems) { item ->
                val itemTitle = when (currentLang) {
                    Language.AR -> item.nameAr
                    Language.FR -> item.nameFr
                    Language.EN -> item.nameEn
                }
                val inCartQty = cart.firstOrNull { it.menuItem.id == item.id }?.quantity ?: 0

                Surface(
                    onClick = { viewModel.addToCart(item) },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (inCartQty > 0) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.testTag("pos_item_${item.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            if (item.badge != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FoodoraOrange.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = item.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FoodoraOrange,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            if (inCartQty > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(FoodoraOrange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$inCartQty",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = itemTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${profile.currencySymbol}${String.format("%.2f", item.price)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = FoodoraOrange
                            )

                            FilledIconButton(
                                onClick = { viewModel.addToCart(item) },
                                modifier = Modifier.size(28.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = FoodoraOrange.copy(alpha = 0.2f),
                                    contentColor = FoodoraOrange
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Floating Cart Summary Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (cartItemCount > 0) FoodoraOrange else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
                .clickable {
                    if (cartItemCount > 0) showCartSheet = true
                }
                .testTag("pos_cart_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (cartItemCount > 0) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = if (cartItemCount > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (cartItemCount > 0) "$cartItemCount Items Selected" else StringsLocalization.get("cart_empty", currentLang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cartItemCount > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (cartItemCount > 0) {
                            Text(
                                text = "Tap to review & charge",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                if (cartItemCount > 0) {
                    Text(
                        text = "${profile.currencySymbol}${String.format("%.2f", cartTotal)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Full Cart Review BottomSheet
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Order Cart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    TextButton(onClick = {
                        viewModel.clearCart()
                        showCartSheet = false
                    }) {
                        Text(StringsLocalization.get("clear_cart", currentLang), color = StatusRed, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cart) { cartItem ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cartItem.menuItem.nameEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${profile.currencySymbol}${String.format("%.2f", cartItem.menuItem.price * cartItem.quantity)}",
                                        fontSize = 12.sp,
                                        color = FoodoraOrange,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.decreaseCartItem(cartItem.menuItem) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = "${cartItem.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    IconButton(
                                        onClick = { viewModel.increaseCartItem(cartItem.menuItem) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(StringsLocalization.get("subtotal", currentLang), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${profile.currencySymbol}${String.format("%.2f", cartSubtotal)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(StringsLocalization.get("tax", currentLang), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${profile.currencySymbol}${String.format("%.2f", cartTax)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(StringsLocalization.get("total", currentLang), fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text("${profile.currencySymbol}${String.format("%.2f", cartTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = FoodoraOrange)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showCartSheet = false
                        showPaymentDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pos_checkout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(StringsLocalization.get("charge", currentLang), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Payment Dialog
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = FoodoraOrange)
                    Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Total Payable: ${profile.currencySymbol}${String.format("%.2f", cartTotal)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = FoodoraOrange
                    )

                    listOf(
                        Triple(PaymentMethod.CASH, StringsLocalization.get("pay_cash", currentLang), Icons.Default.AttachMoney),
                        Triple(PaymentMethod.CREDIT_CARD, StringsLocalization.get("pay_card", currentLang), Icons.Default.CreditCard),
                        Triple(PaymentMethod.DIGITAL_WALLET, StringsLocalization.get("pay_digital", currentLang), Icons.Default.QrCode)
                    ).forEach { (method, label, icon) ->
                        Surface(
                            onClick = {
                                val order = viewModel.checkoutCart(method)
                                lastPlacedOrder = order
                                showPaymentDialog = false
                                showReceiptDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(FoodoraOrange.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = FoodoraOrange)
                                }
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Receipt Confirmation Dialog
    if (showReceiptDialog && lastPlacedOrder != null) {
        val placed = lastPlacedOrder!!
        AlertDialog(
            onDismissRequest = { showReceiptDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                    Text("Order #${placed.orderNumber} Confirmed", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StatusGreen.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = StringsLocalization.get("print_receipt", currentLang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Type: ${placed.type.name} • Payment: ${placed.paymentMethod.name}", fontSize = 12.sp)
                    if (placed.tableNumber != null) {
                        Text("Assigned to Table ${placed.tableNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("Total Charged: ${profile.currencySymbol}${String.format("%.2f", placed.total)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = FoodoraOrange)
                    Text("Status: Dispatched to Kitchen Display System (KDS)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReceiptDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("Done")
                }
            }
        )
    }
}
