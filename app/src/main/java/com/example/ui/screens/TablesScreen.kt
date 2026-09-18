package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.model.DiningTable
import com.example.model.OrderType
import com.example.model.TableStatus
import com.example.ui.components.TableStatusBadge
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.*

@Composable
fun TablesScreen(viewModel: FoodoraViewModel) {
    val tables by viewModel.diningTables.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var selectedSection by remember { mutableStateOf<String?>(null) }
    var selectedTableForAction by remember { mutableStateOf<DiningTable?>(null) }
    var showAddTableDialog by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<DiningTable?>(null) }
    var tableToDelete by remember { mutableStateOf<DiningTable?>(null) }

    val sections = listOf("All", "Indoor", "Patio", "VIP")

    val displayedTables = if (selectedSection == null || selectedSection == "All") {
        tables
    } else {
        tables.filter { it.section.equals(selectedSection, ignoreCase = true) }
    }

    val availableCount = tables.count { it.status == TableStatus.AVAILABLE }
    val occupiedCount = tables.count { it.status == TableStatus.OCCUPIED }
    val billRequestedCount = tables.count { it.status == TableStatus.BILL_REQUESTED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Floor status summary
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
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$availableCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StatusGreen)
                    Text(StringsLocalization.get("available", currentLang), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$occupiedCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StatusOrange)
                    Text(StringsLocalization.get("occupied", currentLang), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$billRequestedCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StatusRed)
                    Text(StringsLocalization.get("bill_requested", currentLang), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section Filter Tabs & Add Table
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sections) { sec ->
                    val isSelected = (sec == "All" && selectedSection == null) || selectedSection == sec
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSection = if (sec == "All") null else sec },
                        label = {
                            val label = when (sec) {
                                "Indoor" -> StringsLocalization.get("indoor", currentLang)
                                "Patio" -> StringsLocalization.get("patio", currentLang)
                                "VIP" -> StringsLocalization.get("vip", currentLang)
                                else -> StringsLocalization.get("all_categories", currentLang)
                            }
                            Text(label, fontSize = 11.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FoodoraOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = { showAddTableDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_table_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(StringsLocalization.get("add_table", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tables Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(displayedTables) { table ->
                val borderColor = when (table.status) {
                    TableStatus.AVAILABLE -> StatusGreen.copy(alpha = 0.5f)
                    TableStatus.OCCUPIED -> StatusOrange.copy(alpha = 0.5f)
                    TableStatus.RESERVED -> StatusBlue.copy(alpha = 0.5f)
                    TableStatus.BILL_REQUESTED -> StatusRed.copy(alpha = 0.7f)
                }

                Surface(
                    onClick = { selectedTableForAction = table },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                    modifier = Modifier.testTag("table_card_${table.number}")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(FoodoraOrange.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "T${table.number}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = FoodoraOrange
                                    )
                                }

                                Text(
                                    text = "${table.capacity} Seats",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TableStatusBadge(table.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column {
                            Text(
                                text = "Section: ${table.section}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Server: ${table.waiterName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (table.status == TableStatus.OCCUPIED || table.status == TableStatus.BILL_REQUESTED) {
                                Text(
                                    text = "${profile.currencySymbol}${String.format("%.2f", table.billAmount)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = FoodoraOrange
                                )
                            } else {
                                Text(
                                    text = "Ready for guests",
                                    fontSize = 11.sp,
                                    color = StatusGreen
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { editingTable = table },
                                    modifier = Modifier.size(28.dp).testTag("edit_table_${table.number}")
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = StringsLocalization.get("btn_edit", currentLang),
                                        tint = FoodoraOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = "Actions",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Table Actions Dialog
    if (selectedTableForAction != null) {
        val table = selectedTableForAction!!
        AlertDialog(
            onDismissRequest = { selectedTableForAction = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.TableRestaurant, contentDescription = null, tint = FoodoraOrange)
                        Text("Table #${table.number} (${table.section})", fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = {
                        editingTable = table
                        selectedTableForAction = null
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = FoodoraOrange)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Status: ${table.status.name} • Capacity: ${table.capacity} people", fontSize = 12.sp)
                    if (table.billAmount > 0) {
                        Text("Current Bill: ${profile.currencySymbol}${String.format("%.2f", table.billAmount)}", fontWeight = FontWeight.Bold, color = FoodoraOrange)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action 1: New POS Order for this table
                    Button(
                        onClick = {
                            viewModel.setCartOrderType(OrderType.DINE_IN)
                            viewModel.setCartTableNumber(table.number)
                            viewModel.navigateTo(Screen.POS)
                            selectedTableForAction = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(StringsLocalization.get("open_bill", currentLang))
                    }

                    // Action 2: Change Status
                    if (table.status == TableStatus.AVAILABLE) {
                        OutlinedButton(
                            onClick = {
                                viewModel.updateTableStatus(table.id, TableStatus.OCCUPIED)
                                selectedTableForAction = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(StringsLocalization.get("occupy_table", currentLang))
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.updateTableStatus(table.id, TableStatus.RESERVED)
                                selectedTableForAction = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Mark Reserved")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                viewModel.updateTableStatus(table.id, TableStatus.BILL_REQUESTED)
                                selectedTableForAction = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(StringsLocalization.get("bill_requested", currentLang))
                        }

                        Button(
                            onClick = {
                                viewModel.updateTableStatus(table.id, TableStatus.AVAILABLE)
                                selectedTableForAction = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(StringsLocalization.get("clear_table", currentLang))
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            editingTable = table
                            selectedTableForAction = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(StringsLocalization.get("edit_table", currentLang))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTableForAction = null }) {
                    Text("Close", color = FoodoraOrange)
                }
            }
        )
    }

    // Add Table Dialog
    if (showAddTableDialog) {
        var newTableNumber by remember { mutableStateOf((tables.maxOfOrNull { it.number } ?: 0) + 1) }
        var newCapacity by remember { mutableStateOf("4") }
        var newSection by remember { mutableStateOf(selectedSection ?: "Indoor") }
        var newWaiter by remember { mutableStateOf("Assigned Waiter") }

        AlertDialog(
            onDismissRequest = { showAddTableDialog = false },
            title = { Text(StringsLocalization.get("add_table", currentLang), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTableNumber.toString(),
                        onValueChange = { newTableNumber = it.toIntOrNull() ?: 1 },
                        label = { Text("Table Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newCapacity,
                        onValueChange = { newCapacity = it },
                        label = { Text(StringsLocalization.get("table_seats", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newSection,
                        onValueChange = { newSection = it },
                        label = { Text(StringsLocalization.get("table_section", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newWaiter,
                        onValueChange = { newWaiter = it },
                        label = { Text(StringsLocalization.get("table_waiter", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cap = newCapacity.toIntOrNull() ?: 4
                        val newTable = DiningTable(
                            id = "table_${System.currentTimeMillis()}",
                            number = newTableNumber,
                            capacity = cap,
                            status = TableStatus.AVAILABLE,
                            section = newSection.ifBlank { "Indoor" },
                            waiterName = newWaiter.ifBlank { "Waiter" }
                        )
                        viewModel.addDiningTable(newTable)
                        showAddTableDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTableDialog = false }) {
                    Text(StringsLocalization.get("btn_cancel", currentLang))
                }
            }
        )
    }

    // Edit Table Dialog
    if (editingTable != null) {
        val table = editingTable!!
        var editNumber by remember(table.id) { mutableStateOf(table.number.toString()) }
        var editCapacity by remember(table.id) { mutableStateOf(table.capacity.toString()) }
        var editSection by remember(table.id) { mutableStateOf(table.section) }
        var editWaiter by remember(table.id) { mutableStateOf(table.waiterName) }

        AlertDialog(
            onDismissRequest = { editingTable = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(StringsLocalization.get("edit_table", currentLang), fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        tableToDelete = table
                        editingTable = null
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editNumber,
                        onValueChange = { editNumber = it },
                        label = { Text("Table Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editCapacity,
                        onValueChange = { editCapacity = it },
                        label = { Text(StringsLocalization.get("table_seats", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editSection,
                        onValueChange = { editSection = it },
                        label = { Text(StringsLocalization.get("table_section", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editWaiter,
                        onValueChange = { editWaiter = it },
                        label = { Text(StringsLocalization.get("table_waiter", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = editNumber.toIntOrNull() ?: table.number
                        val cap = editCapacity.toIntOrNull() ?: table.capacity
                        val updated = table.copy(
                            number = num,
                            capacity = cap,
                            section = editSection.ifBlank { table.section },
                            waiterName = editWaiter.ifBlank { table.waiterName }
                        )
                        viewModel.updateDiningTable(updated)
                        editingTable = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text(StringsLocalization.get("btn_save", currentLang))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTable = null }) {
                    Text(StringsLocalization.get("btn_cancel", currentLang))
                }
            }
        )
    }

    // Delete Table Confirmation
    if (tableToDelete != null) {
        AlertDialog(
            onDismissRequest = { tableToDelete = null },
            title = { Text(StringsLocalization.get("btn_delete", currentLang), fontWeight = FontWeight.Bold, color = StatusRed) },
            text = { Text("Are you sure you want to delete Table #${tableToDelete!!.number} (${tableToDelete!!.section})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDiningTable(tableToDelete!!.id)
                        tableToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text(StringsLocalization.get("btn_delete", currentLang))
                }
            },
            dismissButton = {
                TextButton(onClick = { tableToDelete = null }) {
                    Text(StringsLocalization.get("btn_cancel", currentLang))
                }
            }
        )
    }
}
