package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.Customer
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun CrmScreen(viewModel: FoodoraViewModel) {
    val customers by viewModel.customers.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newIsVip by remember { mutableStateOf(false) }

    val filteredCustomers = customers.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header & Loyalty KPI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Customer CRM & Loyalty",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${customers.size} Registered VIP & Regular Diners",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_customer_button")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name or phone...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FoodoraOrange,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredCustomers) { customer ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (customer.isVip) FoodoraOrange else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (customer.isVip) Color.White else FoodoraOrange
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = customer.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (customer.isVip) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StatusOrange.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "VIP Gold",
                                                color = StatusOrange,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${customer.phone} • ${customer.totalOrders} Orders",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${profile.currencySymbol}${String.format("%.2f", customer.totalSpent)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = StatusOrange, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${customer.loyaltyPoints} pts",
                                        fontSize = 11.sp,
                                        color = StatusOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { editingCustomer = customer },
                                modifier = Modifier.size(32.dp).testTag("edit_customer_${customer.id}")
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = StringsLocalization.get("btn_edit", currentLang),
                                    tint = FoodoraOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Customer Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Customer to CRM", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text(StringsLocalization.get("customer_name", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newPhone,
                            onValueChange = { newPhone = it },
                            label = { Text(StringsLocalization.get("customer_phone", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text(StringsLocalization.get("customer_email", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(StringsLocalization.get("is_vip", currentLang), fontWeight = FontWeight.Medium)
                            Switch(
                                checked = newIsVip,
                                onCheckedChange = { newIsVip = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = FoodoraOrange)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newC = Customer(
                                id = "c_${System.currentTimeMillis()}",
                                name = newName.ifBlank { "New Diner" },
                                phone = newPhone.ifBlank { "+213 555 000 000" },
                                email = newEmail.ifBlank { "guest@mail.com" },
                                totalOrders = 1,
                                totalSpent = 0.0,
                                loyaltyPoints = if (newIsVip) 150 else 50,
                                isVip = newIsVip
                            )
                            viewModel.addCustomer(newC)
                            showAddDialog = false
                            newName = ""
                            newPhone = ""
                            newEmail = ""
                            newIsVip = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text(StringsLocalization.get("btn_save", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }

        // Edit Customer Dialog
        if (editingCustomer != null) {
            val customer = editingCustomer!!
            var editName by remember(customer.id) { mutableStateOf(customer.name) }
            var editPhone by remember(customer.id) { mutableStateOf(customer.phone) }
            var editEmail by remember(customer.id) { mutableStateOf(customer.email) }
            var editPoints by remember(customer.id) { mutableStateOf(customer.loyaltyPoints.toString()) }
            var editIsVip by remember(customer.id) { mutableStateOf(customer.isVip) }

            AlertDialog(
                onDismissRequest = { editingCustomer = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(StringsLocalization.get("edit_customer", currentLang), fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            customerToDelete = customer
                            editingCustomer = null
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text(StringsLocalization.get("customer_name", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text(StringsLocalization.get("customer_phone", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text(StringsLocalization.get("customer_email", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editPoints,
                            onValueChange = { editPoints = it },
                            label = { Text(StringsLocalization.get("loyalty_points", currentLang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(StringsLocalization.get("is_vip", currentLang), fontWeight = FontWeight.Medium)
                            Switch(
                                checked = editIsVip,
                                onCheckedChange = { editIsVip = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = FoodoraOrange)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val pts = editPoints.toIntOrNull() ?: customer.loyaltyPoints
                            val updated = customer.copy(
                                name = editName.ifBlank { customer.name },
                                phone = editPhone.ifBlank { customer.phone },
                                email = editEmail.ifBlank { customer.email },
                                loyaltyPoints = pts,
                                isVip = editIsVip
                            )
                            viewModel.updateCustomer(updated)
                            editingCustomer = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text(StringsLocalization.get("btn_save", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingCustomer = null }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }

        // Delete Customer Confirmation
        if (customerToDelete != null) {
            AlertDialog(
                onDismissRequest = { customerToDelete = null },
                title = { Text(StringsLocalization.get("btn_delete", currentLang), fontWeight = FontWeight.Bold, color = StatusRed) },
                text = { Text("Are you sure you want to remove ${customerToDelete!!.name} from CRM & Loyalty?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteCustomer(customerToDelete!!.id)
                            customerToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                    ) {
                        Text(StringsLocalization.get("btn_delete", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { customerToDelete = null }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }
    }
}

