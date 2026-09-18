package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.Expense
import com.example.model.OrderStatus
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun ReportsScreen(viewModel: FoodoraViewModel) {
    val orders by viewModel.orders.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<Expense?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    var expenseTitle by remember { mutableStateOf("") }
    var expenseAmount by remember { mutableStateOf("") }
    var expenseCategory by remember { mutableStateOf("Ingredients") }

    val totalRevenue = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.total }
    val totalExpense = expenses.sumOf { it.amount }
    val netProfit = totalRevenue - totalExpense
    val profitMargin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // P&L Overview Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = StringsLocalization.get("financial_summary", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Margin ${String.format("%.1f", profitMargin)}%",
                                color = StatusGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sales", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${profile.currencySymbol}${String.format("%.2f", totalRevenue)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusGreen
                            )
                        }
                        Column {
                            Text("Total Expenses", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${profile.currencySymbol}${String.format("%.2f", totalExpense)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = StatusOrange
                            )
                        }
                        Column {
                            Text("Net Profit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${profile.currencySymbol}${String.format("%.2f", netProfit)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (netProfit >= 0) StatusGreen else StatusRed
                            )
                        }
                    }
                }
            }
        }

        // Hourly Sales Traffic Visualization
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = StringsLocalization.get("sales_by_hour", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        TextButton(onClick = { viewModel.navigateTo(com.example.ui.state.Screen.ANALYTICS) }) {
                            Text("Recharts Daily Trends ↗", color = FoodoraOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val hours = listOf("11 AM" to 0.4f, "1 PM" to 0.95f, "3 PM" to 0.35f, "6 PM" to 0.75f, "8 PM" to 1.0f, "10 PM" to 0.5f)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        hours.forEach { (hr, weight) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(26.dp)
                                        .height((50 * weight).dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (weight >= 0.9f) FoodoraOrange else FoodoraOrange.copy(alpha = 0.4f))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(hr, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Expenses section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operational Expenses",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Button(
                    onClick = { showAddExpenseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("log_expense_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Expense", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(expenses) { expense ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "Category: ${expense.category} • Logged by: ${expense.loggedBy}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "-${profile.currencySymbol}${String.format("%.2f", expense.amount)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = StatusRed
                        )

                        IconButton(
                            onClick = { editingExpense = expense },
                            modifier = Modifier.size(32.dp).testTag("edit_expense_${expense.id}")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Expense",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { expenseToDelete = expense },
                            modifier = Modifier.size(32.dp).testTag("delete_expense_${expense.id}")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Expense",
                                tint = StatusRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExpenseDialog = false },
            title = { Text("Log Business Expense", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expenseTitle,
                        onValueChange = { expenseTitle = it },
                        label = { Text("Expense Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = expenseAmount,
                        onValueChange = { expenseAmount = it },
                        label = { Text("Amount (${profile.currencySymbol})") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = expenseCategory,
                        onValueChange = { expenseCategory = it },
                        label = { Text("Category (Ingredients, Rent, Supplies...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = expenseAmount.toDoubleOrNull() ?: 50.0
                        viewModel.addExpense(
                            Expense(
                                id = "exp_${System.currentTimeMillis()}",
                                title = expenseTitle.ifBlank { "Operational Cost" },
                                category = expenseCategory.ifBlank { "General" },
                                amount = amt,
                                dateMillis = System.currentTimeMillis(),
                                loggedBy = "Manager"
                            )
                        )
                        showAddExpenseDialog = false
                        expenseTitle = ""
                        expenseAmount = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("Save Expense")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExpenseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Expense Dialog
    editingExpense?.let { exp ->
        var editTitle by remember(exp) { mutableStateOf(exp.title) }
        var editAmount by remember(exp) { mutableStateOf(exp.amount.toString()) }
        var editCategory by remember(exp) { mutableStateOf(exp.category) }

        AlertDialog(
            onDismissRequest = { editingExpense = null },
            title = { Text("Edit Business Expense", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Expense Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it },
                        label = { Text("Amount (${profile.currencySymbol})") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = editAmount.toDoubleOrNull() ?: exp.amount
                        viewModel.updateExpense(
                            exp.copy(
                                title = editTitle.ifBlank { exp.title },
                                amount = amt,
                                category = editCategory.ifBlank { exp.category }
                            )
                        )
                        editingExpense = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingExpense = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Expense Confirmation Dialog
    expenseToDelete?.let { exp ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Delete Expense Record?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete \"${exp.title}\" (${profile.currencySymbol}${String.format("%.2f", exp.amount)}) from financial records? This will update net profit calculations.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(exp.id)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text("Delete Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
