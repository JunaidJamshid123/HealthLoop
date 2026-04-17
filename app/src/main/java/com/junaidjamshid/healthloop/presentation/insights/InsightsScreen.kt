package com.junaidjamshid.healthloop.presentation.insights

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.junaidjamshid.healthloop.R
import com.junaidjamshid.healthloop.presentation.model.UiState
import com.junaidjamshid.healthloop.ui.theme.*
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart as VicoLineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.marker.Marker
import android.graphics.Typeface
import kotlinx.coroutines.delay

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    
    // Animation states
    var showHealthScore by remember { mutableStateOf(false) }
    var showWeeklyTrends by remember { mutableStateOf(false) }
    var showKeyInsights by remember { mutableStateOf(false) }
    var showQuickStats by remember { mutableStateOf(false) }
    var showSleepAnalysis by remember { mutableStateOf(false) }
    var showMoodDistribution by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        showHealthScore = true
        delay(150)
        showWeeklyTrends = true
        delay(150)
        showKeyInsights = true
        delay(150)
        showQuickStats = true
        delay(150)
        showSleepAnalysis = true
        delay(150)
        showMoodDistribution = true
    }

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = DeepBlack,
                    fontSize = 16.sp
                )
            }
        }
        is UiState.Success -> {
            val data = state.data
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        // Header
        Text(
            text = "Insights",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Period Selector
        PeriodSelectorChips(
            selectedPeriod = when (selectedPeriod) {
                TimePeriod.WEEK -> "Week"
                TimePeriod.MONTH -> "Month"
                TimePeriod.YEAR -> "Year"
            },
            onPeriodSelected = { period ->
                viewModel.setTimePeriod(
                    when (period) {
                        "Week" -> TimePeriod.WEEK
                        "Month" -> TimePeriod.MONTH
                        else -> TimePeriod.YEAR
                    }
                )
            }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Health Score Card
        AnimatedVisibility(
            visible = showHealthScore,
            enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(400)
            )
        ) {
            HealthScoreCard(
                score = data.healthScore,
                label = data.healthScoreLabel,
                scoreChange = data.scoreChange,
                selectedPeriod = selectedPeriod
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Weekly Trends Chart
        AnimatedVisibility(
            visible = showWeeklyTrends,
            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(400)
            )
        ) {
            WeeklyTrendsCard(
                trendData = data.weeklyTrendData,
                labels = data.weeklyLabels,
                periodLabel = when (selectedPeriod) {
                    TimePeriod.WEEK -> "Weekly"
                    TimePeriod.MONTH -> "Monthly"
                    TimePeriod.YEAR -> "Yearly"
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Key Insights
        AnimatedVisibility(
            visible = showKeyInsights,
            enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = tween(400)
            )
        ) {
            KeyInsightsCard(insights = data.insights)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Stats Row
        AnimatedVisibility(
            visible = showQuickStats,
            enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                initialOffsetX = { it / 2 },
                animationSpec = tween(400)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatCard(
                    icon = R.drawable.sleepingg,
                    title = "Avg Sleep",
                    value = if (data.avgSleep > 0) String.format("%.1f hrs", data.avgSleep) else "No Data",
                    backgroundColor = SoftGreen.copy(alpha = 0.4f),
                    accentColor = SoftGreenDark,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    icon = R.drawable.walkk,
                    title = "Avg Steps",
                    value = if (data.avgSteps > 0) {
                        if (data.avgSteps >= 1000) String.format("%.1fK", data.avgSteps / 1000f)
                        else data.avgSteps.toString()
                    } else "No Data",
                    backgroundColor = SkyBlue.copy(alpha = 0.4f),
                    accentColor = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sleep Analysis
        AnimatedVisibility(
            visible = showSleepAnalysis,
            enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(400)
            )
        ) {
            SleepAnalysisCard(sleepQuality = data.sleepQuality)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mood Distribution
        AnimatedVisibility(
            visible = showMoodDistribution,
            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(400)
            )
        ) {
            MoodDistributionCard(moodDistribution = data.moodDistribution)
        }
    }
        }
    }
}

// ==================== PERIOD SELECTOR ====================
@Composable
private fun PeriodSelectorChips(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val periods = listOf("Week", "Month", "Year")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { period ->
            val isSelected = period == selectedPeriod
            Surface(
                modifier = Modifier.clickable { onPeriodSelected(period) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) PrimaryOrange else WarmBeige
            ) {
                Text(
                    text = period,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = DeepBlack
                )
            }
        }
    }
}

// ==================== HEALTH SCORE CARD ====================
@Composable
private fun HealthScoreCard(
    score: Int,
    label: String,
    scoreChange: Int,
    selectedPeriod: TimePeriod = TimePeriod.WEEK
) {
    val periodTitle = when (selectedPeriod) {
        TimePeriod.WEEK -> "Weekly Health Score"
        TimePeriod.MONTH -> "Monthly Health Score"
        TimePeriod.YEAR -> "Yearly Health Score"
    }
    val periodChangeLabel = when (selectedPeriod) {
        TimePeriod.WEEK -> "from prev week"
        TimePeriod.MONTH -> "from prev month"
        TimePeriod.YEAR -> "from prev year"
    }
    val animatedScore by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(1500),
        label = "score"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Score Gauge
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Background arc - light beige
                    drawArc(
                        color = WarmBeige,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    
                    // Progress arc - lime green like Dashboard
                    drawArc(
                        color = SoftGreen,
                        startAngle = 135f,
                        sweepAngle = 270f * animatedScore,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = score.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = periodTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 15.sp,
                    color = DeepBlack,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (scoreChange >= 0) SoftGreen.copy(alpha = 0.5f) else CoralPink.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (scoreChange >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (scoreChange >= 0) SoftGreenDark else CoralPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (scoreChange == 0) "No change" else "${if (scoreChange > 0) "+" else ""}$scoreChange $periodChangeLabel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepBlack
                        )
                    }
                }
            }
        }
    }
}

// ==================== CHART MARKER (TOUCH TOOLTIP) ====================
@Composable
private fun rememberChartMarker(): Marker {
    val labelBackgroundColor = CardSurface
    val labelBackground = remember(labelBackgroundColor) {
        ShapeComponent(
            MarkerCorneredShape(Corner.FullyRounded),
            labelBackgroundColor.toArgb()
        ).setShadow(
            radius = 4f,
            dy = 2f,
            applyElevationOverlay = true,
        )
    }
    val label = textComponent(
        background = labelBackground,
        lineCount = 1,
        padding = dimensionsOf(8.dp, 4.dp),
        typeface = Typeface.DEFAULT_BOLD,
    )
    val indicatorInnerComponent = shapeComponent(Shapes.pillShape, PrimaryOrange)
    val indicatorCenterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicatorOuterComponent = shapeComponent(Shapes.pillShape, Color.White)
    val indicator = overlayingComponent(
        outer = indicatorOuterComponent,
        inner = overlayingComponent(
            outer = indicatorCenterComponent,
            inner = indicatorInnerComponent,
            innerPaddingAll = 5.dp,
        ),
        innerPaddingAll = 10.dp,
    )
    val guideline = lineComponent(
        DeepBlack.copy(alpha = 0.15f),
        2.dp,
        DashedShape(Shapes.pillShape, 8f, 4f),
    )
    return remember(label, indicator, guideline) {
        object : MarkerComponent(label, indicator, guideline) {
            init {
                indicatorSizeDp = 36f
                onApplyEntryColor = { entryColor ->
                    indicatorOuterComponent.color = entryColor.copyColor(alpha = 32)
                    with(indicatorCenterComponent) {
                        color = entryColor
                        setShadow(radius = 12f, color = entryColor)
                    }
                }
            }

            override fun getInsets(
                context: MeasureContext,
                outInsets: Insets,
                horizontalDimensions: HorizontalDimensions,
            ) = with(context) {
                outInsets.top = label.getHeight(context) +
                    MarkerCorneredShape(Corner.FullyRounded).tickSizeDp.pixels +
                    4f.pixels * 1.3f - 2f.pixels
            }
        }
    }
}

// ==================== TRENDS CARD (VICO CHART) ====================
@Composable
private fun WeeklyTrendsCard(
    trendData: List<Float>,
    labels: List<String>,
    periodLabel: String = "Weekly"
) {
    val displayData = if (trendData.isEmpty() || trendData.all { it == 0f }) {
        List(maxOf(labels.size, 7)) { 0f }
    } else {
        trendData
    }
    val displayLabels = if (labels.isEmpty()) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    } else {
        labels
    }

    val chartEntryModel = remember(displayData) {
        entryModelOf(displayData.mapIndexed { index, value -> entryOf(index.toFloat(), value) })
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        displayLabels.getOrElse(value.toInt()) { "" }
    }

    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        value.toInt().toString()
    }

    val marker = rememberChartMarker()

    // Calculate summary stats
    val activeData = displayData.filter { it > 0f }
    val avgScore = if (activeData.isNotEmpty()) activeData.average().toFloat() else 0f
    val maxScore = activeData.maxOrNull() ?: 0f
    val minScore = if (activeData.isNotEmpty()) activeData.min() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$periodLabel Trends",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarmBeige
                ) {
                    Text(
                        text = "Health Score",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tap on the chart to see score at each point",
                fontSize = 11.sp,
                color = DeepBlack.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            VicoLineChart.LineSpec(
                                lineColor = PrimaryOrangeDark.toArgb(),
                                lineThicknessDp = 3f,
                                pointSizeDp = 10f,
                                point = shapeComponent(
                                    shape = Shapes.pillShape,
                                    color = PrimaryOrangeDark
                                ),
                                lineBackgroundShader = com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders.fromBrush(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            PrimaryOrange.copy(alpha = 0.4f),
                                            PrimaryOrange.copy(alpha = 0.0f)
                                        )
                                    )
                                )
                            )
                        ),
                        axisValuesOverrider = AxisValuesOverrider.fixed(
                            minY = 0f,
                            maxY = 100f
                        )
                    ),
                    model = chartEntryModel,
                    startAxis = rememberStartAxis(
                        valueFormatter = startAxisValueFormatter,
                        itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = 5) }
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = bottomAxisValueFormatter
                    ),
                    marker = marker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            // Summary stats below the chart
            if (activeData.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TrendStatItem(label = "Avg", value = String.format("%.0f", avgScore), color = PrimaryOrangeDark)
                    TrendStatItem(label = "High", value = String.format("%.0f", maxScore), color = SoftGreenDark)
                    TrendStatItem(label = "Low", value = String.format("%.0f", minScore), color = CoralPink)
                }
            }
        }
    }
}

@Composable
private fun TrendStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlack.copy(alpha = 0.5f)
        )
    }
}

// ==================== KEY INSIGHTS ====================
@Composable
private fun KeyInsightsCard(insights: List<InsightData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftGreen.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(SoftGreenDark.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = SoftGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Key Insights",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (insights.isEmpty()) {
                Text(
                    text = "Log more data to see insights about your health patterns",
                    fontSize = 13.sp,
                    color = DeepBlack.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            } else {
                insights.forEachIndexed { index, insight ->
                    InsightItem(
                        icon = insight.icon,
                        text = insight.text,
                        isPositive = insight.isPositive
                    )
                    if (index < insights.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightItem(
    icon: Int,
    text: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(CardSurface, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            fontSize = 13.sp,
            color = DeepBlack,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    if (isPositive) SoftGreenDark else CoralPink,
                    CircleShape
                )
        )
    }
}

// ==================== QUICK STAT CARD ====================
@Composable
private fun QuickStatCard(
    icon: Int,
    title: String,
    value: String,
    backgroundColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(CardSurface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DeepBlack.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
            }
        }
    }
}

// ==================== SLEEP ANALYSIS ====================
@Composable
private fun SleepAnalysisCard(sleepQuality: SleepQualityData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SkyBlue.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sleepingg),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sleep Analysis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlack
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarmBeige
                ) {
                    Text(
                        text = sleepQuality.quality,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (sleepQuality.avgHours > 0) {
                // Summary - with background
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarmBeigeLight, RoundedCornerShape(12.dp))
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SleepStatItem("Average", String.format("%.1fh", sleepQuality.avgHours), SoftGreenDark)
                    SleepStatItem("Quality", "${sleepQuality.qualityPercent}%", 
                        if (sleepQuality.qualityPercent >= 80) MintGreen else PrimaryOrange)
                    SleepStatItem("Consistency", "${sleepQuality.consistency}%", SkyBlue)
                }
            } else {
                Text(
                    text = "No sleep data recorded yet",
                    fontSize = 14.sp,
                    color = DeepBlack.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun SleepStatItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlack.copy(alpha = 0.6f)
        )
    }
}

// ==================== MOOD DISTRIBUTION ====================
@Composable
private fun MoodDistributionCard(moodDistribution: Map<String, Int>) {
    val total = moodDistribution.values.sum().toFloat().coerceAtLeast(1f)
    
    val moodIcons = mapOf(
        "Happy" to R.drawable.happy,
        "Calm" to R.drawable.calm,
        "Tired" to R.drawable.tired,
        "Anxious" to R.drawable.anxious,
        "Sad" to R.drawable.sad,
        "Excited" to R.drawable.exited,
        "Grateful" to R.drawable.grateful,
        "Stressed" to R.drawable.confused
    )
    
    val moodColors = mapOf(
        "Happy" to SoftGreenDark,
        "Calm" to SkyBlue,
        "Tired" to PrimaryOrangeDark,
        "Anxious" to CoralPink,
        "Sad" to SkyBlue,
        "Excited" to PrimaryOrange,
        "Grateful" to SoftGreen,
        "Stressed" to CoralPink
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Distribution",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepBlack
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarmBeige
                ) {
                    Text(
                        text = "${total.toInt()} entries",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepBlack.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            if (moodDistribution.isEmpty()) {
                Text(
                    text = "No mood data recorded yet",
                    fontSize = 14.sp,
                    color = DeepBlack.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                // Mood bars - sorted by count
                moodDistribution
                    .entries
                    .sortedByDescending { it.value }
                    .take(4)
                    .forEachIndexed { index, (mood, count) ->
                        MoodBarItem(
                            label = mood,
                            percentage = count / total,
                            icon = moodIcons[mood] ?: R.drawable.happy,
                            color = moodColors[mood] ?: SoftGreenDark
                        )
                        if (index < minOf(moodDistribution.size - 1, 3)) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
            }
        }
    }
}

@Composable
private fun MoodBarItem(
    label: String,
    percentage: Float,
    icon: Int,
    color: Color
) {
    val animatedWidth by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(1000),
        label = "moodBar"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(WarmBeige, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = DeepBlack,
            modifier = Modifier.width(55.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .background(WarmBeigeDark, RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedWidth)
                    .background(color, RoundedCornerShape(6.dp))
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "${(percentage * 100).toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlack,
            modifier = Modifier.width(35.dp)
        )
    }
}

// ==================== PREVIEW ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InsightsScreenPreview() {
    MaterialTheme {
        InsightsScreen()
    }
}