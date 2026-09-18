package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.Category
import com.example.model.MenuItem
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun MenuScreen(viewModel: FoodoraViewModel) {
    val menuItems by viewModel.menuItems.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    var itemToDelete by remember { mutableStateOf<MenuItem?>(null) }
    var showCategoryManagerDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    // Add item form fields
    var newNameEn by remember { mutableStateOf("") }
    var newNameAr by remember { mutableStateOf("") }
    var newPriceStr by remember { mutableStateOf("") }
    var newCostStr by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf(categories.firstOrNull()?.id ?: "cat_1") }
    var newDesc by remember { mutableStateOf("") }
    var newPrepTime by remember { mutableStateOf("12") }

    val displayedItems = if (selectedCategoryId == null) {
        menuItems
    } else {
        menuItems.filter { it.categoryId == selectedCategoryId }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header & Item count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Menu & Dishes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${menuItems.size} Total Products Across ${categories.size} Categories",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { showCategoryManagerDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("manage_categories_button")
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(StringsLocalization.get("edit_category", currentLang), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_menu_item_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Dish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                    val catName = when (currentLang) {
                        com.example.model.Language.AR -> category.nameAr
                        com.example.model.Language.FR -> category.nameFr
                        com.example.model.Language.EN -> category.nameEn
                    }
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(catName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FoodoraOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dishes list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedItems) { item ->
                    val itemName = when (currentLang) {
                        com.example.model.Language.AR -> item.nameAr
                        com.example.model.Language.FR -> item.nameFr
                        com.example.model.Language.EN -> item.nameEn
                    }
                    val itemDesc = when (currentLang) {
                        com.example.model.Language.AR -> item.descriptionAr
                        com.example.model.Language.FR -> item.descriptionFr
                        com.example.model.Language.EN -> item.descriptionEn
                    }
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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = itemName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (item.badge != null) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = FoodoraOrange.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = item.badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FoodoraOrange,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = itemDesc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "${profile.currencySymbol}${String.format("%.2f", item.price)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = FoodoraOrange
                                    )

                                    Text(
                                        text = "Cost: ${profile.currencySymbol}${String.format("%.2f", item.costPrice)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = "Prep: ${item.prepTimeMinutes}m",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Actions: Edit button & Availability Switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { editingMenuItem = item },
                                    modifier = Modifier.testTag("edit_dish_${item.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = StringsLocalization.get("btn_edit", currentLang),
                                        tint = FoodoraOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Switch(
                                        checked = item.isAvailable,
                                        onCheckedChange = { viewModel.toggleMenuItemAvailability(item.id) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = StatusGreen,
                                            uncheckedThumbColor = Color.Gray,
                                            uncheckedTrackColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                    Text(
                                        text = if (item.isAvailable) "Active" else "Hidden",
                                        fontSize = 10.sp,
                                        color = if (item.isAvailable) StatusGreen else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Dish Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Menu Item", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newNameEn,
                            onValueChange = { newNameEn = it },
                            label = { Text("Dish Name (English)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newNameAr,
                            onValueChange = { newNameAr = it },
                            label = { Text("Dish Name (Arabic) / اسم الطبق") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newPriceStr,
                                onValueChange = { newPriceStr = it },
                                label = { Text("Price (${profile.currencySymbol})") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newCostStr,
                                onValueChange = { newCostStr = it },
                                label = { Text("Cost Price") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Category selection
                        Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = newCategory == cat.id,
                                    onClick = { newCategory = cat.id },
                                    label = { Text(cat.nameEn, fontSize = 10.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = newPrepTime,
                            onValueChange = { newPrepTime = it },
                            label = { Text("Prep Time (minutes)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newDesc,
                            onValueChange = { newDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val price = newPriceStr.toDoubleOrNull() ?: 10.0
                            val cost = newCostStr.toDoubleOrNull() ?: (price * 0.35)
                            val prep = newPrepTime.toIntOrNull() ?: 12
                            val newItem = MenuItem(
                                id = "m_${System.currentTimeMillis()}",
                                nameEn = newNameEn.ifBlank { "Chef Special" },
                                nameAr = newNameAr.ifBlank { "طبق خاص" },
                                nameFr = newNameEn.ifBlank { "Spécialité du Chef" },
                                categoryId = newCategory,
                                price = price,
                                costPrice = cost,
                                descriptionEn = newDesc.ifBlank { "Freshly prepared dish with premium ingredients." },
                                descriptionAr = "طبق طازج مُعد بأجود المكونات الطبيعية.",
                                descriptionFr = "Plat fraîchement préparé avec des ingrédients de qualité.",
                                isAvailable = true,
                                prepTimeMinutes = prep
                            )
                            viewModel.addMenuItem(newItem)
                            showAddDialog = false
                            newNameEn = ""
                            newNameAr = ""
                            newPriceStr = ""
                            newCostStr = ""
                            newDesc = ""
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

        // Edit Dish Dialog
        if (editingMenuItem != null) {
            val item = editingMenuItem!!
            var editNameEn by remember(item.id) { mutableStateOf(item.nameEn) }
            var editNameAr by remember(item.id) { mutableStateOf(item.nameAr) }
            var editPrice by remember(item.id) { mutableStateOf(item.price.toString()) }
            var editCost by remember(item.id) { mutableStateOf(item.costPrice.toString()) }
            var editCatId by remember(item.id) { mutableStateOf(item.categoryId) }
            var editDescEn by remember(item.id) { mutableStateOf(item.descriptionEn) }
            var editDescAr by remember(item.id) { mutableStateOf(item.descriptionAr) }
            var editBadge by remember(item.id) { mutableStateOf(item.badge ?: "") }
            var editPrepTime by remember(item.id) { mutableStateOf(item.prepTimeMinutes.toString()) }
            var editIsAvailable by remember(item.id) { mutableStateOf(item.isAvailable) }

            AlertDialog(
                onDismissRequest = { editingMenuItem = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(StringsLocalization.get("edit_dish", currentLang), fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            itemToDelete = item
                            editingMenuItem = null
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
                            value = editNameEn,
                            onValueChange = { editNameEn = it },
                            label = { Text("Dish Name (English)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editNameAr,
                            onValueChange = { editNameAr = it },
                            label = { Text("Dish Name (Arabic) / اسم الطبق") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editPrice,
                                onValueChange = { editPrice = it },
                                label = { Text("Price (${profile.currencySymbol})") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = editCost,
                                onValueChange = { editCost = it },
                                label = { Text("Cost Price") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = editCatId == cat.id,
                                    onClick = { editCatId = cat.id },
                                    label = { Text(cat.nameEn, fontSize = 10.sp) }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editPrepTime,
                                onValueChange = { editPrepTime = it },
                                label = { Text("Prep Time (min)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = editBadge,
                                onValueChange = { editBadge = it },
                                label = { Text("Badge (Optional)") },
                                singleLine = true,
                                placeholder = { Text("Chef Special, Spicy...") },
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        OutlinedTextField(
                            value = editDescEn,
                            onValueChange = { editDescEn = it },
                            label = { Text("Description (English)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        OutlinedTextField(
                            value = editDescAr,
                            onValueChange = { editDescAr = it },
                            label = { Text("Description (Arabic) / الوصف") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Active on Menu", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = editIsAvailable,
                                onCheckedChange = { editIsAvailable = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = StatusGreen)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val price = editPrice.toDoubleOrNull() ?: item.price
                            val cost = editCost.toDoubleOrNull() ?: item.costPrice
                            val prep = editPrepTime.toIntOrNull() ?: item.prepTimeMinutes
                            val updated = item.copy(
                                nameEn = editNameEn.ifBlank { item.nameEn },
                                nameAr = editNameAr.ifBlank { item.nameAr },
                                price = price,
                                costPrice = cost,
                                categoryId = editCatId,
                                descriptionEn = editDescEn,
                                descriptionAr = editDescAr,
                                badge = if (editBadge.isBlank()) null else editBadge.trim(),
                                prepTimeMinutes = prep,
                                isAvailable = editIsAvailable
                            )
                            viewModel.updateMenuItem(updated)
                            editingMenuItem = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text(StringsLocalization.get("btn_save", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingMenuItem = null }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }

        // Delete Dish Confirmation Dialog
        if (itemToDelete != null) {
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text(StringsLocalization.get("btn_delete", currentLang), fontWeight = FontWeight.Bold, color = StatusRed) },
                text = { Text(StringsLocalization.get("delete_dish_confirm", currentLang) + "\n\n${itemToDelete!!.nameEn}") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteMenuItem(itemToDelete!!.id)
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

        // Category Manager Dialog
        if (showCategoryManagerDialog) {
            AlertDialog(
                onDismissRequest = { showCategoryManagerDialog = false },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(StringsLocalization.get("edit_category", currentLang), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showAddCategoryDialog = true }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = FoodoraOrange)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(cat.nameEn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${cat.nameAr} • ${cat.nameFr}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Row {
                                        IconButton(onClick = { categoryToEdit = cat }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = FoodoraOrange, modifier = Modifier.size(18.dp))
                                        }
                                        if (categories.size > 1) {
                                            IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCategoryManagerDialog = false }) {
                        Text("Close", color = FoodoraOrange)
                    }
                }
            )
        }

        // Edit Single Category Dialog
        if (categoryToEdit != null) {
            val cat = categoryToEdit!!
            var cNameEn by remember(cat.id) { mutableStateOf(cat.nameEn) }
            var cNameAr by remember(cat.id) { mutableStateOf(cat.nameAr) }
            var cNameFr by remember(cat.id) { mutableStateOf(cat.nameFr) }

            AlertDialog(
                onDismissRequest = { categoryToEdit = null },
                title = { Text(StringsLocalization.get("edit_category", currentLang), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = cNameEn,
                            onValueChange = { cNameEn = it },
                            label = { Text("Category Name (English)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = cNameAr,
                            onValueChange = { cNameAr = it },
                            label = { Text("Category Name (Arabic) / اسم التصنيف") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = cNameFr,
                            onValueChange = { cNameFr = it },
                            label = { Text("Category Name (French)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateCategory(
                                cat.copy(
                                    nameEn = cNameEn.ifBlank { cat.nameEn },
                                    nameAr = cNameAr.ifBlank { cat.nameAr },
                                    nameFr = cNameFr.ifBlank { cat.nameFr }
                                )
                            )
                            categoryToEdit = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text(StringsLocalization.get("btn_save", currentLang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { categoryToEdit = null }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }

        // Add Category Dialog
        if (showAddCategoryDialog) {
            var newCatEn by remember { mutableStateOf("") }
            var newCatAr by remember { mutableStateOf("") }
            var newCatFr by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddCategoryDialog = false },
                title = { Text(StringsLocalization.get("add_category", currentLang), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newCatEn,
                            onValueChange = { newCatEn = it },
                            label = { Text("Category Name (English)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newCatAr,
                            onValueChange = { newCatAr = it },
                            label = { Text("Category Name (Arabic) / اسم التصنيف") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newCatFr,
                            onValueChange = { newCatFr = it },
                            label = { Text("Category Name (French)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newCat = Category(
                                id = "cat_${System.currentTimeMillis()}",
                                nameEn = newCatEn.ifBlank { "New Category" },
                                nameAr = newCatAr.ifBlank { "تصنيف جديد" },
                                nameFr = newCatFr.ifBlank { "Nouvelle Catégorie" },
                                iconName = "restaurant"
                            )
                            viewModel.addCategory(newCat)
                            showAddCategoryDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCategoryDialog = false }) {
                        Text(StringsLocalization.get("btn_cancel", currentLang))
                    }
                }
            )
        }
    }
}

