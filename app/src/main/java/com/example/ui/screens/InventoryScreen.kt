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
import com.example.model.InventoryItem
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun InventoryScreen(viewModel: FoodoraViewModel) {
    val inventory by viewModel.inventory.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<InventoryItem?>(null) }
    var itemToDelete by remember { mutableStateOf<InventoryItem?>(null) }

    var newItemName by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("General Pantry") }
    var newItemStock by remember { mutableStateOf("") }
    var newItemUnit by remember { mutableStateOf("kg") }
    var newItemThreshold by remember { mutableStateOf("") }
    var newItemCost by remember { mutableStateOf("") }
    var newItemSupplier by remember { mutableStateOf("") }

    val lowStockItems = inventory.filter { it.currentStock <= it.minThreshold }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Inventory health banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (lowStockItems.isNotEmpty()) StatusRed.copy(alpha = 0.12f) else StatusGreen.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (lowStockItems.isNotEmpty()) StatusRed.copy(alpha = 0.4f) else StatusGreen.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (lowStockItems.isNotEmpty()) StatusRed else StatusGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (lowStockItems.isNotEmpty()) Icons.Default.Warning else Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (lowStockItems.isNotEmpty()) "${lowStockItems.size} Ingredients Low in Stock" else "Inventory Levels Healthy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lowStockItems.isNotEmpty()) "Re-order suggested from suppliers" else "All ${inventory.size} items are above safety threshold",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_inventory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Inventory Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(inventory) { item ->
                val isLow = item.currentStock <= item.minThreshold

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isLow) StatusRed.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (isLow) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StatusRed.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = StringsLocalization.get("status_low", currentLang),
                                                color = StatusRed,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Category: ${item.category} • Supplier: ${item.supplier}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Current Stock
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${item.currentStock} ${item.unit}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (isLow) StatusRed else StatusGreen
                                )
                                Text(
                                    text = "Min: ${item.minThreshold} ${item.unit}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Stock adjustment controls & Edit Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { editingItem = item },
                                    modifier = Modifier.size(32.dp).testTag("edit_inventory_${item.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = StringsLocalization.get("btn_edit", currentLang),
                                        tint = FoodoraOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "Cost: ${profile.currencySymbol}${String.format("%.2f", item.costPerUnit)}/${item.unit}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                OutlinedIconButton(
                                    onClick = { viewModel.adjustStock(item.id, -1.0) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "-1", modifier = Modifier.size(14.dp))
                                }

                                OutlinedIconButton(
                                    onClick = { viewModel.adjustStock(item.id, 1.0) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "+1", modifier = Modifier.size(14.dp))
                                }

                                FilledTonalButton(
                                    onClick = { viewModel.adjustStock(item.id, 10.0) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = FoodoraOrange.copy(alpha = 0.2f),
                                        contentColor = FoodoraOrange
                                    )
                                ) {
                                    Text("+10 Restock", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Ingredient Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Inventory Item", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            label = { Text("Ingredient / Supply Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newItemCategory,
                            onValueChange = { newItemCategory = it },
                            label = { Text("Category") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newItemStock,
                                onValueChange = { newItemStock = it },
                                label = { Text("Stock Quantity") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = newItemUnit,
                                onValueChange = { newItemUnit = it },
                                label = { Text("Unit (kg/L/pcs)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newItemThreshold,
                                onValueChange = { newItemThreshold = it },
                                label = { Text("Min Threshold") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = newItemCost,
                                onValueChange = { newItemCost = it },
                                label = { Text("Cost / Unit") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = newItemSupplier,
                            onValueChange = { newItemSupplier = it },
                            label = { Text("Supplier Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val stock = newItemStock.toDoubleOrNull() ?: 10.0
                            val threshold = newItemThreshold.toDoubleOrNull() ?: 5.0
                            val cost = newItemCost.toDoubleOrNull() ?: 5.0
                            val newItem = InventoryItem(
                                id = "inv_${System.currentTimeMillis()}",
                                name = newItemName.ifBlank { "New Ingredient" },
                                category = newItemCategory.ifBlank { "General Pantry" },
                                currentStock = stock,
                                unit = newItemUnit.ifBlank { "kg" },
                                minThreshold = threshold,
                                costPerUnit = cost,
                                supplier = newItemSupplier.ifBlank { "Local Food Service Supplier" }
                            )
                            viewModel.addInventoryItem(newItem)
                            showAddDialog = false
                            newItemName = ""
                            newItemStock = ""
                            newItemThreshold = ""
                            newItemCost = ""
                            newItemSupplier = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }

        // Edit Inventory Item Dialog
        if (editingItem != null) {
            val item = editingItem!!
            var editName by remember(item.id) { mutableStateOf(item.name) }
            var editCategory by remember(item.id) { mutableStateOf(item.category) }
            var editStock by remember(item.id) { mutableStateOf(item.currentStock.toString()) }
            var editUnit by remember(item.id) { mutableStateOf(item.unit) }
            var editThreshold by remember(item.id) { mutableStateOf(item.minThreshold.toString()) }
            var editCost by remember(item.id) { mutableStateOf(item.costPerUnit.toString()) }
            var editSupplier by remember(item.id) { mutableStateOf(item.supplier) }

            AlertDialog(
                onDismissRequest = { editingItem = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(StringsLocalization.get("edit_inventory", currentLang), fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            itemToDelete = item
                            editingItem = null
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
                            label = { Text("Item Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editCategory,
                            onValueChange = { editCategory = it },
                            label = { Text("Category") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editStock,
                                onValueChange = { editStock = it },
                                label = { Text("Current Stock") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = editUnit,
                                onValueChange = { editUnit = it },
                                label = { Text("Unit") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editThreshold,
                                onValueChange = { editThreshold = it },
                                label = { Text("Min Threshold") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = editCost,
                                onValueChange = { editCost = it },
                                label = { Text("Cost / Unit (${profile.currencySymbol})") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = editSupplier,
                            onValueChange = { editSupplier = it },
                            label = { Text("Supplier") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val stock = editStock.toDoubleOrNull() ?: item.currentStock
                            val threshold = editThreshold.toDoubleOrNull() ?: item.minThreshold
                            val cost = editCost.toDoubleOrNull() ?: item.costPerUnit
                            val updated = item.copy(
                                name = editName.ifBlank { item.name },
                                category = editCategory.ifBlank { item.category },
                                currentStock = stock,
                                unit = editUnit.ifBlank { item.unit },
                                minThreshold = threshold,
                                costPerUnit = cost,
                                supplier = editSupplier.ifBlank { item.supplier }
                            )
                            viewModel.updateInventoryItem(updated)
                            editingItem = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text(StringsLocalization.get("btn_save", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingItem = null }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }

        // Delete Inventory Item Confirmation
        if (itemToDelete != null) {
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text(StringsLocalization.get("btn_delete", currentLang), fontWeight = FontWeight.Bold, color = StatusRed) },
                text = { Text("Are you sure you want to remove ${itemToDelete!!.name} from inventory?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteInventoryItem(itemToDelete!!.id)
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                    ) {
                        Text(StringsLocalization.get("btn_delete", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }
    }
}
