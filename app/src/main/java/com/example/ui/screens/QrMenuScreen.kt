package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.model.MenuItem
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*

@Composable
fun QrMenuScreen(viewModel: FoodoraViewModel) {
    val profile by viewModel.businessProfile.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var selectedTable by remember { mutableStateOf(1) }
    var showCustomerPreview by remember { mutableStateOf(false) }
    var printSuccessAlert by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Explainer card
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(FoodoraOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.White)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = StringsLocalization.get("qr_menu_title", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = StringsLocalization.get("qr_desc", currentLang),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Table selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Table for QR Stand:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((1..9).toList()) { tbl ->
                        val isSelected = selectedTable == tbl
                        Surface(
                            onClick = { selectedTable = tbl },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Text(
                                text = "Table $tbl",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Printable QR Stand Mockup
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.dp, FoodoraOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FoodoraOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = profile.name.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "TABLE #$selectedTable • SCAN FOR DIGITAL MENU",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF6B6B78)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized QR Code Pattern
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        QrCodeVectorView()
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "No app download needed • Fast mobile order",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Text(
                        text = "Powered by Foodora Cloud POS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodoraOrange
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showCustomerPreview = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("preview_qr_menu_button"),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(StringsLocalization.get("preview_menu", currentLang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { printSuccessAlert = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("print_qr_button"),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp), tint = FoodoraOrange)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Print Stand", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FoodoraOrange)
                }
            }
        }
    }

    // Customer Menu Preview Modal
    if (showCustomerPreview) {
        AlertDialog(
            onDismissRequest = { showCustomerPreview = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = FoodoraOrange)
                    Text("Customer View (Table #$selectedTable)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    Text(
                        text = "This is what customers see on their mobile browser when scanning Table #$selectedTable QR code:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(menuItems.take(5)) { item ->
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
                                        Text(item.nameEn, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(item.descriptionEn, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        Text("${profile.currencySymbol}${String.format("%.2f", item.price)}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = FoodoraOrange)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FoodoraOrange
                                    ) {
                                        Text(
                                            text = "+ Order",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCustomerPreview = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("Close Preview")
                }
            }
        )
    }

    if (printSuccessAlert) {
        AlertDialog(
            onDismissRequest = { printSuccessAlert = false },
            title = { Text("Print Table QR Stand", fontWeight = FontWeight.Bold) },
            text = {
                Text("QR stand for Table #$selectedTable was sent to the network thermal receipt & label printer. Dimensions: 10cm x 15cm table tent format.")
            },
            confirmButton = {
                Button(
                    onClick = { printSuccessAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun QrCodeVectorView() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val step = w / 7f

        // Draw Corner Finder Patterns (Top-Left, Top-Right, Bottom-Left)
        drawRect(Color.Black, Offset(0f, 0f), Size(step * 2.2f, step * 2.2f))
        drawRect(Color.White, Offset(step * 0.4f, step * 0.4f), Size(step * 1.4f, step * 1.4f))
        drawRect(Color.Black, Offset(step * 0.7f, step * 0.7f), Size(step * 0.8f, step * 0.8f))

        drawRect(Color.Black, Offset(w - step * 2.2f, 0f), Size(step * 2.2f, step * 2.2f))
        drawRect(Color.White, Offset(w - step * 1.8f, step * 0.4f), Size(step * 1.4f, step * 1.4f))
        drawRect(Color.Black, Offset(w - step * 1.5f, step * 0.7f), Size(step * 0.8f, step * 0.8f))

        drawRect(Color.Black, Offset(0f, h - step * 2.2f), Size(step * 2.2f, step * 2.2f))
        drawRect(Color.White, Offset(step * 0.4f, h - step * 1.8f), Size(step * 1.4f, step * 1.4f))
        drawRect(Color.Black, Offset(step * 0.7f, h - step * 1.5f), Size(step * 0.8f, step * 0.8f))

        // Center Foodora Orange Dot
        drawCircle(FoodoraOrange, radius = step * 0.55f, center = Offset(w / 2f, h / 2f))

        // Some stylized data modules
        drawRect(Color.Black, Offset(step * 3f, step * 1f), Size(step * 0.7f, step * 0.7f))
        drawRect(Color.Black, Offset(step * 3.8f, step * 1.8f), Size(step * 0.7f, step * 0.7f))
        drawRect(Color.Black, Offset(step * 1.5f, step * 3.2f), Size(step * 0.7f, step * 0.7f))
        drawRect(Color.Black, Offset(step * 4.6f, step * 3.2f), Size(step * 0.7f, step * 0.7f))
        drawRect(Color.Black, Offset(step * 3.2f, step * 4.6f), Size(step * 0.7f, step * 0.7f))
        drawRect(Color.Black, Offset(step * 4.8f, step * 5.2f), Size(step * 0.7f, step * 0.7f))
    }
}
