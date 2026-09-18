package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StringsLocalization
import com.example.model.ComparisonPeriod
import com.example.model.HourlyTrendPoint
import com.example.model.Language
import com.example.ui.components.MetricCard
import com.example.ui.components.RechartsNativeAreaChart
import com.example.ui.components.RechartsWebViewChart
import com.example.ui.state.FoodoraViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.*
import java.util.Locale

enum class RechartsRenderMode {
    NATIVE_CANVAS,
    INTERACTIVE_WEB
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyAnalyticsScreen(viewModel: FoodoraViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val selectedPeriod by viewModel.selectedComparisonPeriod.collectAsState()
    val trendPoints by viewModel.hourlySalesTrends.collectAsState()

    var renderMode by remember { mutableStateOf(RechartsRenderMode.NATIVE_CANVAS) }
    var showTableExpanded by remember { mutableStateOf(false) }

    // Aggregate calculations
    val todayTotal = trendPoints.sumOf { it.todayRevenue }
    val prevTotal = trendPoints.sumOf { it.previousPeriodRevenue }
    val totalDiff = todayTotal - prevTotal
    val growthPct = if (prevTotal > 0) (totalDiff / prevTotal) * 100 else 0.0

    val todayOrdersTotal = trendPoints.sumOf { it.todayOrdersCount }
    val prevOrdersTotal = trendPoints.sumOf { it.previousOrdersCount }
    val ordersDiffPct = if (prevOrdersTotal > 0) ((todayOrdersTotal - prevOrdersTotal).toDouble() / prevOrdersTotal) * 100 else 0.0

    val avgTicketToday = if (todayOrdersTotal > 0) todayTotal / todayOrdersTotal else 0.0
    val avgTicketPrev = if (prevOrdersTotal > 0) prevTotal / prevOrdersTotal else 0.0
    val ticketDiffPct = if (avgTicketPrev > 0) ((avgTicketToday - avgTicketPrev) / avgTicketPrev) * 100 else 0.0

    val peakHourPoint = trendPoints.maxByOrNull { it.todayRevenue }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("daily_analytics_screen")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Live Status
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = StringsLocalization.get("revenue_trends_recharts", currentLang),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FoodoraOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Recharts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FoodoraOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = StringsLocalization.get("daily_analytics_title", currentLang),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick Return to Dashboard button
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                        modifier = Modifier.testTag("back_to_dashboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Period Filter Selector Chips
                Text(
                    text = StringsLocalization.get("compare_periods", currentLang),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ComparisonPeriod.values().forEach { period ->
                        val isSelected = period == selectedPeriod
                        val label = when (currentLang) {
                            Language.AR -> period.labelAr
                            Language.FR -> period.labelFr
                            Language.EN -> period.labelEn
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectComparisonPeriod(period) },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FoodoraOrange,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) FoodoraOrange else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.testTag("period_chip_${period.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Engine switcher (Native Recharts Canvas vs Web Recharts Engine)
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Native option
                    Surface(
                        onClick = { renderMode = RechartsRenderMode.NATIVE_CANVAS },
                        shape = RoundedCornerShape(10.dp),
                        color = if (renderMode == RechartsRenderMode.NATIVE_CANVAS) FoodoraOrange else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = if (renderMode == RechartsRenderMode.NATIVE_CANVAS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = StringsLocalization.get("recharts_native_view", currentLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (renderMode == RechartsRenderMode.NATIVE_CANVAS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Web option
                    Surface(
                        onClick = { renderMode = RechartsRenderMode.INTERACTIVE_WEB },
                        shape = RoundedCornerShape(10.dp),
                        color = if (renderMode == RechartsRenderMode.INTERACTIVE_WEB) FoodoraOrange else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (renderMode == RechartsRenderMode.INTERACTIVE_WEB) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = StringsLocalization.get("recharts_web_view", currentLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (renderMode == RechartsRenderMode.INTERACTIVE_WEB) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Recharts Graph (Hero Visualizer)
        item {
            when (renderMode) {
                RechartsRenderMode.NATIVE_CANVAS -> {
                    RechartsNativeAreaChart(
                        dataPoints = trendPoints,
                        comparisonPeriod = selectedPeriod,
                        profile = profile,
                        modifier = Modifier.testTag("recharts_native_chart")
                    )
                }
                RechartsRenderMode.INTERACTIVE_WEB -> {
                    RechartsWebViewChart(
                        dataPoints = trendPoints,
                        comparisonPeriod = selectedPeriod,
                        profile = profile,
                        isDarkMode = isDarkMode,
                        modifier = Modifier.testTag("recharts_web_chart")
                    )
                }
            }
        }

        // 4 Core Comparative Metric KPI Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Comparative Performance Metrics",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Metric 1: Total Revenue
                    val revPositive = totalDiff >= 0
                    MetricCard(
                        title = StringsLocalization.get("today_revenue", currentLang),
                        value = profile.formatCurrency(todayTotal),
                        subtitle = "${if (revPositive) "+" else ""}${String.format(Locale.US, "%.1f", growthPct)}% vs prev",
                        icon = Icons.Default.Payments,
                        accentColor = if (revPositive) StatusGreen else StatusRed,
                        modifier = Modifier.weight(1f)
                    )

                    // Metric 2: Order Volume
                    val ordPositive = ordersDiffPct >= 0
                    MetricCard(
                        title = StringsLocalization.get("order_volume", currentLang),
                        value = "$todayOrdersTotal orders",
                        subtitle = "${if (ordPositive) "+" else ""}${String.format(Locale.US, "%.1f", ordersDiffPct)}% vs prev",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = if (ordPositive) StatusGreen else StatusOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Metric 3: Average Order Value (AOV)
                    val aovPositive = ticketDiffPct >= 0
                    MetricCard(
                        title = StringsLocalization.get("avg_order_value", currentLang),
                        value = profile.formatCurrency(avgTicketToday),
                        subtitle = "${if (aovPositive) "+" else ""}${String.format(Locale.US, "%.1f", ticketDiffPct)}% vs prev",
                        icon = Icons.Default.ShoppingBag,
                        accentColor = if (aovPositive) StatusGreen else StatusOrange,
                        modifier = Modifier.weight(1f)
                    )

                    // Metric 4: Peak Sales Hour
                    MetricCard(
                        title = StringsLocalization.get("peak_hour", currentLang),
                        value = peakHourPoint?.hourLabel ?: "--:--",
                        subtitle = peakHourPoint?.let { profile.formatCurrency(it.todayRevenue) } ?: "N/A",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = FoodoraOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sales Channel Breakdown (Dine-in vs Delivery vs Takeout)
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = StringsLocalization.get("channel_breakdown", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Pacing: +18.4% Ahead",
                                color = StatusGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Channel 1: Dine-In
                    ChannelPacingRow(
                        title = "Dine-In Restaurant",
                        sharePct = 54,
                        todayAmt = todayTotal * 0.54,
                        prevAmt = prevTotal * 0.50,
                        barColor = FoodoraOrange,
                        icon = Icons.Default.Restaurant,
                        profile = profile
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel 2: Delivery Fleet
                    ChannelPacingRow(
                        title = "Delivery Fleet",
                        sharePct = 26,
                        todayAmt = todayTotal * 0.26,
                        prevAmt = prevTotal * 0.24,
                        barColor = StatusBlue,
                        icon = Icons.Default.DeliveryDining,
                        profile = profile
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel 3: Takeout & Counter
                    ChannelPacingRow(
                        title = "Takeout & Counter",
                        sharePct = 20,
                        todayAmt = todayTotal * 0.20,
                        prevAmt = prevTotal * 0.26,
                        barColor = Color(0xFF8B5CF6),
                        icon = Icons.Default.TakeoutDining,
                        profile = profile
                    )
                }
            }
        }

        // Hourly Velocity & Variance Details (Expandable)
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTableExpanded = !showTableExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = StringsLocalization.get("hourly_breakdown", currentLang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${trendPoints.size} hours logged (08:00 - 23:00)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { showTableExpanded = !showTableExpanded }) {
                            Icon(
                                imageVector = if (showTableExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Hourly Table"
                            )
                        }
                    }

                    AnimatedVisibility(visible = showTableExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            trendPoints.forEach { pt ->
                                HourlyVelocityRow(pt = pt, profile = profile)
                            }
                        }
                    }

                    if (!showTableExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        // Show peak 3 hours by revenue as a quick snapshot
                        val top3 = trendPoints.sortedByDescending { it.todayRevenue }.take(3)
                        top3.forEach { pt ->
                            HourlyVelocityRow(pt = pt, profile = profile)
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        TextButton(
                            onClick = { showTableExpanded = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("View All ${trendPoints.size} Hourly Windows", fontSize = 12.sp, color = FoodoraOrange)
                        }
                    }
                }
            }
        }

        // Operational AI Takeaway Insights
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = FoodoraOrange.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, FoodoraOrange.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = FoodoraOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Daily Analytics Intelligence",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Dinner rush peaked at 20:00 with ${profile.formatCurrency(peakHourPoint?.todayRevenue ?: 0.0)} (+21% vs comparison period). Truffle Smash Burgers and Artisan Pizzas made up 64% of volume. Suggest scheduling +1 kitchen line cook for tomorrow's 19:00 - 21:30 shift.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelPacingRow(
    title: String,
    sharePct: Int,
    todayAmt: Double,
    prevAmt: Double,
    barColor: Color,
    icon: ImageVector,
    profile: com.example.model.BusinessProfile
) {
    val diff = todayAmt - prevAmt
    val pctGrowth = if (prevAmt > 0) (diff / prevAmt) * 100 else 0.0
    val isPositive = diff >= 0

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, contentDescription = null, tint = barColor, modifier = Modifier.size(16.dp))
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("($sharePct%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    profile.formatCurrency(todayAmt),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.1f", pctGrowth)}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) StatusGreen else StatusRed
                )
            }
        }

        LinearProgressIndicator(
            progress = { (sharePct / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun HourlyVelocityRow(
    pt: HourlyTrendPoint,
    profile: com.example.model.BusinessProfile
) {
    val diff = pt.todayRevenue - pt.previousPeriodRevenue
    val pct = if (pt.previousPeriodRevenue > 0) (diff / pt.previousPeriodRevenue) * 100 else 0.0
    val isPositive = diff >= 0

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = pt.hourLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (pt.periodLabel.isNotBlank()) {
                    Text(
                        text = pt.periodLabel,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = profile.formatCurrency(pt.todayRevenue),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = FoodoraOrange
                    )
                    Text(
                        text = "Prev: ${profile.formatCurrency(pt.previousPeriodRevenue)}",
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isPositive) StatusGreen.copy(alpha = 0.12f) else StatusRed.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.1f", pct)}%",
                        color = if (isPositive) StatusGreen else StatusRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
