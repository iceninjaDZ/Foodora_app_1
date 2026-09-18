package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.Employee
import com.example.model.UserRole
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun StaffScreen(viewModel: FoodoraViewModel) {
    val employees by viewModel.employees.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<Employee?>(null) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FoodoraOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White)
                        }

                        Column {
                            Text("Staff & Roles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${employees.size} Team Members • RBAC Active", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FoodoraOrange.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Current: ${currentRole.title}",
                            color = FoodoraOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Permissions Matrix Overview
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Role Permissions Matrix", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val matrix = listOf(
                        "Owner" to "Full Unlimited Access (Financials, Menu, Staff, System)",
                        "Manager" to "Menu, Floor, Inventory, Shift Reports, Staff",
                        "Cashier" to "POS Register, Cash Drawer, Daily Sales",
                        "Waiter" to "Table Floor Plan, Order Entry, Bill Requests",
                        "Kitchen" to "KDS Board, Station Prep, Recipe Specs",
                        "Delivery" to "Delivery Orders, Routing, Customer Dispatch"
                    )

                    matrix.forEach { (r, perm) ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = FoodoraOrange, fontWeight = FontWeight.Bold)
                            Text(text = "$r: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = perm, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Staff Directory & Add button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Team Directory", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_staff_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(StringsLocalization.get("add_staff", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(employees) { emp ->
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
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FoodoraOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = FoodoraOrange)
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FoodoraOrange.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = emp.role.title,
                                        color = FoodoraOrange,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${emp.phone} • PIN: **** • ${emp.shift}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { editingEmployee = emp },
                            modifier = Modifier.testTag("edit_employee_${emp.id}")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = StringsLocalization.get("btn_edit", currentLang),
                                tint = FoodoraOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        FilledTonalButton(
                            onClick = { viewModel.switchRole(emp.role) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Act As", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add Employee Dialog
    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newRole by remember { mutableStateOf(UserRole.WAITER) }
        var newPhone by remember { mutableStateOf("+213 ") }
        var newShift by remember { mutableStateOf("Morning Shift") }
        var newPin by remember { mutableStateOf("1234") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(StringsLocalization.get("add_staff", currentLang), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(StringsLocalization.get("staff_name", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(StringsLocalization.get("staff_role", currentLang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(UserRole.values()) { r ->
                            FilterChip(
                                selected = newRole == r,
                                onClick = { newRole = r },
                                label = { Text(r.title, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text(StringsLocalization.get("staff_phone", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newShift,
                        onValueChange = { newShift = it },
                        label = { Text(StringsLocalization.get("staff_shift", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it },
                        label = { Text("Security PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newEmp = Employee(
                            id = "emp_${System.currentTimeMillis()}",
                            name = newName.ifBlank { "Team Member" },
                            role = newRole,
                            email = "${newName.lowercase().replace(" ", ".")}@foodora.dz",
                            phone = newPhone.ifBlank { "+213 555 000 000" },
                            isActive = true,
                            pinCode = newPin.ifBlank { "0000" },
                            shift = newShift.ifBlank { "Morning Shift" }
                        )
                        viewModel.addEmployee(newEmp)
                        showAddDialog = false
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

    // Edit Employee Dialog
    if (editingEmployee != null) {
        val emp = editingEmployee!!
        var editName by remember(emp.id) { mutableStateOf(emp.name) }
        var editRole by remember(emp.id) { mutableStateOf(emp.role) }
        var editPhone by remember(emp.id) { mutableStateOf(emp.phone) }
        var editShift by remember(emp.id) { mutableStateOf(emp.shift) }
        var editPin by remember(emp.id) { mutableStateOf(emp.pinCode) }

        AlertDialog(
            onDismissRequest = { editingEmployee = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(StringsLocalization.get("edit_staff", currentLang), fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        employeeToDelete = emp
                        editingEmployee = null
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(StringsLocalization.get("staff_name", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(StringsLocalization.get("staff_role", currentLang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(UserRole.values()) { r ->
                            FilterChip(
                                selected = editRole == r,
                                onClick = { editRole = r },
                                label = { Text(r.title, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text(StringsLocalization.get("staff_phone", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editShift,
                        onValueChange = { editShift = it },
                        label = { Text(StringsLocalization.get("staff_shift", currentLang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPin,
                        onValueChange = { editPin = it },
                        label = { Text("Security PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = emp.copy(
                            name = editName.ifBlank { emp.name },
                            role = editRole,
                            phone = editPhone.ifBlank { emp.phone },
                            shift = editShift.ifBlank { emp.shift },
                            pinCode = editPin.ifBlank { emp.pinCode }
                        )
                        viewModel.updateEmployee(updated)
                        editingEmployee = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text(StringsLocalization.get("btn_save", currentLang))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEmployee = null }) {
                    Text(StringsLocalization.get("btn_cancel", currentLang))
                }
            }
        )
    }

    // Delete Employee Confirmation
    if (employeeToDelete != null) {
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            title = { Text(StringsLocalization.get("btn_delete", currentLang), fontWeight = FontWeight.Bold, color = StatusRed) },
            text = { Text("Are you sure you want to remove ${employeeToDelete!!.name} (${employeeToDelete!!.role.title}) from the staff team?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEmployee(employeeToDelete!!.id)
                        employeeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text(StringsLocalization.get("btn_delete", currentLang))
                }
            },
            dismissButton = {
                TextButton(onClick = { employeeToDelete = null }) {
                    Text(StringsLocalization.get("btn_cancel", currentLang))
                }
            }
        )
    }
}

