package com.example.healthloop.presentation.components

import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun LineChartView(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier,
    label: String = "Data",
    lineColor: Int = Color.BLUE
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val chart = remember {
        LineChart(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            // Customize X axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.GRAY
            }
            
            // Customize left Y axis
            axisLeft.apply {
                setDrawGridLines(true)
                textColor = Color.GRAY
            }
            
            // Customize right Y axis
            axisRight.isEnabled = false
            
            // Animate chart
            animateX(1500)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            chart.clear()
        }
    }

    AndroidView(
        factory = { chart },
        modifier = modifier,
        update = { view ->
            val entries = data.map { Entry(it.first, it.second) }
            val dataSet = LineDataSet(entries, label).apply {
                color = lineColor
                setDrawCircles(true)
                setDrawValues(false)
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(lineColor)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
            }
            
            view.data = LineData(dataSet)
            view.invalidate()
        }
    )
}

@Composable
fun BarChartView(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier,
    label: String = "Data",
    barColor: Int = ColorTemplate.COLORFUL_COLORS[0]
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val chart = remember {
        BarChart(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = Color.GRAY
            }
            axisLeft.apply {
                setDrawGridLines(true)
                textColor = Color.GRAY
            }
            axisRight.isEnabled = false
            animateY(1500)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            chart.clear()
        }
    }

    AndroidView(
        factory = { chart },
        modifier = modifier,
        update = { view ->
            val entries = data.map { BarEntry(it.first, it.second) }
            val dataSet = BarDataSet(entries, label).apply {
                color = barColor
                setDrawValues(true)
            }
            view.data = BarData(dataSet)
            view.invalidate()
        }
    )
}

@Composable
fun PieChartView(
    data: Map<String, Float>,
    modifier: Modifier = Modifier,
    label: String = "Data"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val chart = remember {
        PieChart(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            description.isEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            animateY(1400)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            chart.clear()
        }
    }

    AndroidView(
        factory = { chart },
        modifier = modifier,
        update = { view ->
            val entries = data.map { PieEntry(it.value, it.key) }
            val dataSet = PieDataSet(entries, label).apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                setDrawValues(true)
                valueTextColor = Color.BLACK
                valueTextSize = 14f
            }
            view.data = PieData(dataSet)
            view.invalidate()
        }
    )
} 