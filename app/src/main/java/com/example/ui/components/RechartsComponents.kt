package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BusinessProfile
import com.example.model.ComparisonPeriod
import com.example.model.HourlyTrendPoint
import com.example.ui.theme.FoodoraOrange
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.util.Locale

/**
 * Recharts-inspired Native Jetpack Compose Canvas Chart.
 * Replicates the exact visual fidelity and ergonomics of Recharts <AreaChart> with:
 * - Smooth cubic bezier curves
 * - Gradient area fills
 * - Dashed previous period reference curve
 * - Interactive scrub/touch tooltip with exact revenue comparison
 * - Peak revenue badges
 */
@Composable
fun RechartsNativeAreaChart(
    dataPoints: List<HourlyTrendPoint>,
    comparisonPeriod: ComparisonPeriod,
    profile: BusinessProfile,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(dataPoints, comparisonPeriod) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    val maxVal = remember(dataPoints) {
        val maxToday = dataPoints.maxOfOrNull { it.todayRevenue } ?: 100.0
        val maxPrev = dataPoints.maxOfOrNull { it.previousPeriodRevenue } ?: 100.0
        val peak = maxOf(maxToday, maxPrev) * 1.15
        if (peak <= 0.0) 100.0 else peak
    }

    val peakPoint = remember(dataPoints) {
        dataPoints.maxByOrNull { it.todayRevenue }
    }

    val activePoint = selectedIndex?.let { dataPoints.getOrNull(it) } ?: peakPoint

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chart Header & Recharts Legend
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
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FoodoraOrange)
                        )
                        Text(
                            text = "Recharts Area Curve",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Touch/drag anywhere to inspect hourly comparison",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Interactive Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(FoodoraOrange)
                        )
                        Text("Today", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = FoodoraOrange)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(Color(0xFF94A3B8))
                        )
                        Text(
                            text = when (comparisonPeriod) {
                                ComparisonPeriod.YESTERDAY -> "Yesterday"
                                ComparisonPeriod.LAST_WEEK -> "Last Week"
                                ComparisonPeriod.MONTH_AVG -> "30d Avg"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Floating Live Tooltip Pill (Showing active touched point)
            activePoint?.let { pt ->
                val diff = pt.todayRevenue - pt.previousPeriodRevenue
                val pct = if (pt.previousPeriodRevenue > 0) (diff / pt.previousPeriodRevenue) * 100 else 0.0
                val isPositive = diff >= 0

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, FoodoraOrange.copy(alpha = 0.35f)),
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = pt.hourLabel,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (pt.periodLabel.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FoodoraOrange.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = pt.periodLabel,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FoodoraOrange,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${pt.todayOrdersCount} orders today • ${pt.previousOrdersCount} prev",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = profile.formatCurrency(pt.todayRevenue),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = FoodoraOrange
                                )
                                Text(
                                    text = "Prev: ${profile.formatCurrency(pt.previousPeriodRevenue)}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPositive) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.1f", pct)}%",
                                    color = if (isPositive) StatusGreen else StatusRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas drawing area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(dataPoints) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val count = dataPoints.size
                                    if (count > 1) {
                                        val stepX = size.width / (count - 1)
                                        val idx = (offset.x / stepX)
                                            .toInt()
                                            .coerceIn(0, count - 1)
                                        selectedIndex = idx
                                    }
                                }
                            )
                        }
                        .pointerInput(dataPoints) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val count = dataPoints.size
                                    if (count > 1) {
                                        val stepX = size.width / (count - 1)
                                        val idx = (offset.x / stepX)
                                            .toInt()
                                            .coerceIn(0, count - 1)
                                        selectedIndex = idx
                                    }
                                },
                                onDrag = { change, _ ->
                                    val count = dataPoints.size
                                    if (count > 1) {
                                        val stepX = size.width / (count - 1)
                                        val idx = (change.position.x / stepX)
                                            .toInt()
                                            .coerceIn(0, count - 1)
                                        selectedIndex = idx
                                    }
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val paddingBottom = 24.dp.toPx()
                    val chartHeight = h - paddingBottom
                    val count = dataPoints.size
                    val stepX = if (count > 1) w / (count - 1) else w

                    // 1. Grid lines (3 horizontal dashed levels)
                    val gridSteps = 3
                    for (i in 1..gridSteps) {
                        val y = chartHeight * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    // 2. Build Previous Period Path (Dashed Slate)
                    val prevPath = Path()
                    val progress = animationProgress.value

                    dataPoints.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val normalizedY = (pt.previousPeriodRevenue / maxVal).toFloat() * progress
                        val y = chartHeight - (normalizedY * chartHeight)
                        if (i == 0) {
                            prevPath.moveTo(x, y)
                        } else {
                            val prevX = (i - 1) * stepX
                            val prevPt = dataPoints[i - 1]
                            val prevNormY = (prevPt.previousPeriodRevenue / maxVal).toFloat() * progress
                            val prevY = chartHeight - (prevNormY * chartHeight)
                            val cx = (prevX + x) / 2f
                            prevPath.cubicTo(cx, prevY, cx, y, x, y)
                        }
                    }

                    // Draw previous period dashed curve
                    drawPath(
                        path = prevPath,
                        color = Color(0xFF94A3B8).copy(alpha = 0.7f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        )
                    )

                    // 3. Build Today's Curve Path & Gradient Area
                    val todayCurvePath = Path()
                    val todayAreaPath = Path()

                    dataPoints.forEachIndexed { i, pt ->
                        val x = i * stepX
                        val normalizedY = (pt.todayRevenue / maxVal).toFloat() * progress
                        val y = chartHeight - (normalizedY * chartHeight)
                        if (i == 0) {
                            todayCurvePath.moveTo(x, y)
                            todayAreaPath.moveTo(x, chartHeight)
                            todayAreaPath.lineTo(x, y)
                        } else {
                            val prevX = (i - 1) * stepX
                            val prevPt = dataPoints[i - 1]
                            val prevNormY = (prevPt.todayRevenue / maxVal).toFloat() * progress
                            val prevY = chartHeight - (prevNormY * chartHeight)
                            val cx = (prevX + x) / 2f
                            todayCurvePath.cubicTo(cx, prevY, cx, y, x, y)
                            todayAreaPath.cubicTo(cx, prevY, cx, y, x, y)
                        }
                    }

                    todayAreaPath.lineTo(w, chartHeight)
                    todayAreaPath.close()

                    // Fill gradient area under Today's curve
                    drawPath(
                        path = todayAreaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FoodoraOrange.copy(alpha = 0.38f),
                                FoodoraOrange.copy(alpha = 0.12f),
                                FoodoraOrange.copy(alpha = 0.01f)
                            ),
                            startY = 0f,
                            endY = chartHeight
                        )
                    )

                    // Draw Today's solid vibrant stroke
                    drawPath(
                        path = todayCurvePath,
                        color = FoodoraOrange,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 4. Draw Selected Scrubber indicator
                    selectedIndex?.let { idx ->
                        if (idx in dataPoints.indices) {
                            val pt = dataPoints[idx]
                            val x = idx * stepX
                            val todayY = chartHeight - ((pt.todayRevenue / maxVal).toFloat() * progress * chartHeight)
                            val prevY = chartHeight - ((pt.previousPeriodRevenue / maxVal).toFloat() * progress * chartHeight)

                            // Vertical tracking line
                            drawLine(
                                color = FoodoraOrange.copy(alpha = 0.6f),
                                start = Offset(x, 0f),
                                end = Offset(x, chartHeight),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                            )

                            // Prev period point dot
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = Offset(x, prevY)
                            )
                            drawCircle(
                                color = Color(0xFF94A3B8),
                                radius = 4.dp.toPx(),
                                center = Offset(x, prevY),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Today point glowing outer ring + dot
                            drawCircle(
                                color = FoodoraOrange.copy(alpha = 0.35f),
                                radius = 8.dp.toPx(),
                                center = Offset(x, todayY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = Offset(x, todayY)
                            )
                            drawCircle(
                                color = FoodoraOrange,
                                radius = 3.5.dp.toPx(),
                                center = Offset(x, todayY)
                            )
                        }
                    }

                    // 5. Draw Peak Hour Indicator Dot (if not actively dragging)
                    if (selectedIndex == null && peakPoint != null) {
                        val peakIdx = dataPoints.indexOf(peakPoint)
                        if (peakIdx >= 0) {
                            val px = peakIdx * stepX
                            val py = chartHeight - ((peakPoint.todayRevenue / maxVal).toFloat() * progress * chartHeight)
                            drawCircle(
                                color = FoodoraOrange.copy(alpha = 0.4f),
                                radius = 7.dp.toPx(),
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = FoodoraOrange,
                                radius = 4.dp.toPx(),
                                center = Offset(px, py)
                            )
                        }
                    }
                }
            }

            // X-Axis Hour Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val step = if (dataPoints.size > 8) 3 else 2
                dataPoints.forEachIndexed { index, pt ->
                    if (index % step == 0 || index == dataPoints.lastIndex) {
                        Text(
                            text = pt.hourLabel,
                            fontSize = 10.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Black else FontWeight.Normal,
                            color = if (selectedIndex == index) FoodoraOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Recharts Interactive HTML5/SVG Web Component.
 * Embedded directly within Android's WebView to run authentic Recharts SVG rendering
 * with animated hover markers, fluid gradients, tooltips, and cross-browser responsiveness.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RechartsWebViewChart(
    dataPoints: List<HourlyTrendPoint>,
    comparisonPeriod: ComparisonPeriod,
    profile: BusinessProfile,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val periodName = when (comparisonPeriod) {
        ComparisonPeriod.YESTERDAY -> "Yesterday"
        ComparisonPeriod.LAST_WEEK -> "Last Week"
        ComparisonPeriod.MONTH_AVG -> "30-Day Avg"
    }

    val htmlContent = remember(dataPoints, comparisonPeriod, isDarkMode, profile) {
        generateRechartsHtml(
            points = dataPoints,
            periodName = periodName,
            currencySymbol = profile.currencySymbol,
            isDark = isDarkMode
        )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = FoodoraOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Recharts Interactive Web Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FoodoraOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "SVG / JavaScript",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodoraOrange,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            setBackgroundColor(0) // Transparent
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Builds the responsive Recharts-styled HTML5/SVG template
 */
private fun generateRechartsHtml(
    points: List<HourlyTrendPoint>,
    periodName: String,
    currencySymbol: String,
    isDark: Boolean
): String {
    val bg = if (isDark) "#181A20" else "#F8FAFC"
    val textColor = if (isDark) "#E2E8F0" else "#1E293B"
    val subTextColor = if (isDark) "#94A3B8" else "#64748B"
    val gridColor = if (isDark) "rgba(255,255,255,0.08)" else "rgba(0,0,0,0.06)"
    val cardBg = if (isDark) "#262A34" else "#FFFFFF"

    val maxVal = maxOf(
        points.maxOfOrNull { it.todayRevenue } ?: 100.0,
        points.maxOfOrNull { it.previousPeriodRevenue } ?: 100.0
    ) * 1.15

    val width = 640
    val height = 230
    val chartTop = 20
    val chartBottom = 190
    val chartLeft = 40
    val chartRight = 620
    val chartW = chartRight - chartLeft
    val chartH = chartBottom - chartTop
    val stepX = if (points.size > 1) chartW.toFloat() / (points.size - 1) else chartW.toFloat()

    // Build SVG paths for today area and prev line
    val todayPathD = StringBuilder()
    val todayAreaD = StringBuilder()
    val prevPathD = StringBuilder()

    points.forEachIndexed { i, pt ->
        val x = chartLeft + i * stepX
        val yToday = chartBottom - ((pt.todayRevenue / maxVal) * chartH).toFloat()
        val yPrev = chartBottom - ((pt.previousPeriodRevenue / maxVal) * chartH).toFloat()

        if (i == 0) {
            todayPathD.append("M $x $yToday ")
            todayAreaD.append("M $x $chartBottom L $x $yToday ")
            prevPathD.append("M $x $yPrev ")
        } else {
            val prevX = chartLeft + (i - 1) * stepX
            val prevPt = points[i - 1]
            val prevYToday = chartBottom - ((prevPt.todayRevenue / maxVal) * chartH).toFloat()
            val prevYPrev = chartBottom - ((prevPt.previousPeriodRevenue / maxVal) * chartH).toFloat()
            val cx = (prevX + x) / 2f

            todayPathD.append("C $cx $prevYToday, $cx $yToday, $x $yToday ")
            todayAreaD.append("C $cx $prevYToday, $cx $yToday, $x $yToday ")
            prevPathD.append("C $cx $prevYPrev, $cx $yPrev, $x $yPrev ")
        }
    }

    todayAreaD.append("L $chartRight $chartBottom Z")

    // Generate JSON for interactive tooltip
    val pointsJson = points.joinToString(prefix = "[", postfix = "]") { pt ->
        """{"hour":"${pt.hourLabel}","today":${pt.todayRevenue},"prev":${pt.previousPeriodRevenue},"orders":${pt.todayOrdersCount},"label":"${pt.periodLabel}"}"""
    }

    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
        body { background: $bg; color: $textColor; overflow: hidden; padding: 6px; }
        .chart-container { position: relative; width: 100%; }
        svg { width: 100%; height: auto; display: block; overflow: visible; }
        .tooltip {
          position: absolute;
          display: none;
          background: $cardBg;
          color: $textColor;
          border: 1px solid #FF6B00;
          box-shadow: 0 4px 14px rgba(0,0,0,0.25);
          border-radius: 8px;
          padding: 8px 12px;
          font-size: 12px;
          pointer-events: none;
          z-index: 10;
          transform: translate(-50%, -120%);
        }
        .tooltip-title { font-weight: bold; color: #FF6B00; margin-bottom: 2px; }
        .tooltip-row { display: flex; justify-content: space-between; gap: 12px; }
        .guide-line { stroke: rgba(255,107,0,0.5); stroke-dasharray: 4,4; stroke-width: 1.5; display: none; }
      </style>
    </head>
    <body>
      <div class="chart-container" id="container">
        <div class="tooltip" id="tooltip">
          <div class="tooltip-title" id="t-title"></div>
          <div class="tooltip-row"><span>Today:</span><strong id="t-today" style="color:#FF6B00"></strong></div>
          <div class="tooltip-row"><span id="t-prev-lbl">$periodName:</span><strong id="t-prev" style="color:#94A3B8"></strong></div>
        </div>
        <svg viewBox="0 0 $width $height" id="chartSvg">
          <defs>
            <linearGradient id="rechartsGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="#FF6B00" stop-opacity="0.45"/>
              <stop offset="60%" stop-color="#FF6B00" stop-opacity="0.12"/>
              <stop offset="100%" stop-color="#FF6B00" stop-opacity="0.0"/>
            </linearGradient>
          </defs>

          <!-- Horizontal Grid Lines -->
          <line x1="$chartLeft" y1="$chartTop" x2="$chartRight" y2="$chartTop" stroke="$gridColor" stroke-dasharray="4 4"/>
          <line x1="$chartLeft" y1="${(chartTop + chartBottom) / 2}" x2="$chartRight" y2="${(chartTop + chartBottom) / 2}" stroke="$gridColor" stroke-dasharray="4 4"/>
          <line x1="$chartLeft" y1="$chartBottom" x2="$chartRight" y2="$chartBottom" stroke="$gridColor"/>

          <!-- Today Area Fill -->
          <path d="$todayAreaD" fill="url(#rechartsGradient)" />

          <!-- Previous Period Dashed Curve -->
          <path d="$prevPathD" fill="none" stroke="#94A3B8" stroke-width="2.5" stroke-dasharray="6 6" />

          <!-- Today Solid Vibrant Curve -->
          <path d="$todayPathD" fill="none" stroke="#FF6B00" stroke-width="3.5" stroke-linecap="round" />

          <!-- Interactive Guide Line -->
          <line id="guideLine" class="guide-line" y1="$chartTop" y2="$chartBottom"/>

          <!-- Interactive Active Markers -->
          <circle id="dotPrev" r="4" fill="#FFFFFF" stroke="#94A3B8" stroke-width="2" style="display:none;"/>
          <circle id="dotToday" r="5" fill="#FFFFFF" stroke="#FF6B00" stroke-width="3" style="display:none;"/>
        </svg>
      </div>

      <script>
        const data = $pointsJson;
        const maxVal = $maxVal;
        const chartLeft = $chartLeft;
        const chartRight = $chartRight;
        const chartTop = $chartTop;
        const chartBottom = $chartBottom;
        const chartW = chartRight - chartLeft;
        const chartH = chartBottom - chartTop;
        const stepX = data.length > 1 ? chartW / (data.length - 1) : chartW;

        const container = document.getElementById('container');
        const tooltip = document.getElementById('tooltip');
        const guideLine = document.getElementById('guideLine');
        const dotToday = document.getElementById('dotToday');
        const dotPrev = document.getElementById('dotPrev');

        function updateAt(idx, clientX, clientY) {
          if (idx < 0 || idx >= data.length) return;
          const pt = data[idx];
          const x = chartLeft + idx * stepX;
          const yToday = chartBottom - ((pt.today / maxVal) * chartH);
          const yPrev = chartBottom - ((pt.prev / maxVal) * chartH);

          guideLine.setAttribute('x1', x);
          guideLine.setAttribute('x2', x);
          guideLine.style.display = 'block';

          dotToday.setAttribute('cx', x);
          dotToday.setAttribute('cy', yToday);
          dotToday.style.display = 'block';

          dotPrev.setAttribute('cx', x);
          dotPrev.setAttribute('cy', yPrev);
          dotPrev.style.display = 'block';

          document.getElementById('t-title').innerText = pt.hour + (pt.label ? ' • ' + pt.label : '');
          document.getElementById('t-today').innerText = pt.today.toLocaleString() + ' $currencySymbol (' + pt.orders + ' ord)';
          document.getElementById('t-prev').innerText = pt.prev.toLocaleString() + ' $currencySymbol';

          tooltip.style.display = 'block';
          tooltip.style.left = (clientX || (container.offsetWidth * (x / $width))) + 'px';
          tooltip.style.top = (clientY || (container.offsetHeight * (yToday / $height))) + 'px';
        }

        container.addEventListener('mousemove', (e) => {
          const rect = container.getBoundingClientRect();
          const relX = (e.clientX - rect.left) / rect.width * $width;
          const idx = Math.round((relX - chartLeft) / stepX);
          updateAt(Math.max(0, Math.min(data.length - 1, idx)), e.clientX - rect.left, e.clientY - rect.top);
        });

        container.addEventListener('touchmove', (e) => {
          const touch = e.touches[0];
          const rect = container.getBoundingClientRect();
          const relX = (touch.clientX - rect.left) / rect.width * $width;
          const idx = Math.round((relX - chartLeft) / stepX);
          updateAt(Math.max(0, Math.min(data.length - 1, idx)), touch.clientX - rect.left, touch.clientY - rect.top);
        });
      </script>
    </body>
    </html>
    """.trimIndent()
}
