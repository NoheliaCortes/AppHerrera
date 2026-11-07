package com.sistemas.herrerasystemmobile.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.ui.common.UiState
import com.sistemas.herrerasystemmobile.ui.theme.*
import com.sistemas.herrerasystemmobile.ui.viewmodel.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

private val ChartPalette = listOf(Color(0xFF00C296), Color(0xFF4C8BF5), Color(0xFFF3B63F), Color(0xFF9B72DD), Color(0xFFEF7779), Color(0xFF40A9C7), Color(0xFF66B86E))
private val MonthNames = listOf("Todo el año", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
private data class SummaryMetrics(val count: Int, val units: Double, val amount: Double, val unitLabel: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    state: UiState<ReportsUiData>, selectedType: ReportType, year: Int, month: Int?,
    onSelectType: (ReportType) -> Unit, onSelectYear: (Int) -> Unit, onSelectMonth: (Int?) -> Unit,
    onRefresh: () -> Unit, onBack: () -> Unit
) {
    val refreshing = state is UiState.Loading
    PullToRefreshBox(isRefreshing = refreshing, onRefresh = onRefresh, modifier = Modifier.fillMaxSize().background(ScreenBackground)) {
        LazyColumn(
            Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextDark) }
                    Column(Modifier.weight(1f)) {
                        Text("Reportes", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Análisis de ventas y clientes", fontSize = 11.sp, color = TextMuted)
                    }
                    IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, "Actualizar", tint = PrimaryGreenButton) }
                }
            }
            item { DateRangeSelector(year, month, onSelectYear, onSelectMonth) }
            item {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    ReportType.entries.forEach { type ->
                        FilterChip(selected = selectedType == type, onClick = { onSelectType(type) }, label = { Text(type.title, fontSize = 11.sp) })
                    }
                }
            }
            when (state) {
                UiState.Loading -> item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(2) {
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), modifier = Modifier.fillMaxWidth().height(92.dp)) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(Modifier.fillMaxWidth(0.42f).height(13.dp).background(InputBackground, RoundedCornerShape(8.dp)))
                                    Box(Modifier.fillMaxWidth().height(22.dp).background(InputBackground, RoundedCornerShape(8.dp)))
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> item {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No se pudo cargar el reporte", fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(Modifier.height(6.dp)); Text(state.message, color = TextMuted, fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp)); Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenButton)) { Text("Reintentar") }
                        }
                    }
                }
                is UiState.Success -> {
                    val data = state.data
                    val chartRows = data.content.toChartRows()
                    item { SummaryCards(data.content) }
                    item {
                        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Column {
                                    Text(data.reportType.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("${data.startDate} — ${data.endDate}", fontSize = 10.sp, color = TextMuted)
                                }
                                when {
                                    chartRows.isEmpty() -> Box(Modifier.fillMaxWidth().height(170.dp), contentAlignment = Alignment.Center) { Text("No hay datos para este período.", color = TextMuted, fontSize = 12.sp) }
                                    data.reportType == ReportType.TREND -> TrendChart(chartRows)
                                    data.reportType == ReportType.PRESENTATION -> DonutChart(chartRows)
                                    else -> BarChart(chartRows)
                                }
                            }
                        }
                    }
                    item { ReportDetails(data.content) }
                }
            }
        }
    }
}

@Composable
private fun DateRangeSelector(year: Int, month: Int?, onYear: (Int) -> Unit, onMonth: (Int?) -> Unit) {
    var yearExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    val years = (LocalYear() downTo 2020).toList()
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Box(Modifier.weight(1f)) {
            OutlinedButton(onClick = { yearExpanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp)) {
                Icon(Icons.Default.CalendarMonth, null, tint = PrimaryGreenButton, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(7.dp)); Text(year.toString(), color = TextDark); Spacer(Modifier.weight(1f)); Text("▾", color = TextMuted)
            }
            DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }, modifier = Modifier.heightIn(max = 280.dp)) {
                years.forEach { y -> DropdownMenuItem(text = { Text(y.toString()) }, onClick = { onYear(y); yearExpanded = false }) }
            }
        }
        Box(Modifier.weight(1.15f)) {
            OutlinedButton(onClick = { monthExpanded = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp)) {
                Text(month?.let { MonthNames[it] } ?: MonthNames[0], color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis); Spacer(Modifier.weight(1f)); Text("▾", color = TextMuted)
            }
            DropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                MonthNames.forEachIndexed { index, name -> DropdownMenuItem(text = { Text(name) }, onClick = { onMonth(index.takeIf { it > 0 }); monthExpanded = false }) }
            }
        }
    }
}

@Composable
private fun SummaryCards(content: ReportContent) {
    val metrics = when (content) {
        is ReportContent.LineFlavor -> SummaryMetrics(content.rows.size, content.rows.sumOf { it.unitsSold }, content.rows.sumOf { it.amount }, "combinaciones")
        is ReportContent.Trend -> SummaryMetrics(content.rows.size, content.rows.sumOf { it.unitsSold }, 0.0, "períodos")
        is ReportContent.Geography -> SummaryMetrics(content.rows.size, content.rows.sumOf { it.unitsSold }, content.rows.sumOf { it.amount }, "municipios")
        is ReportContent.TopCustomers -> SummaryMetrics(content.rows.size, content.rows.sumOf { it.unitsBought }, content.rows.sumOf { it.amount }, "clientes")
        is ReportContent.Presentation -> SummaryMetrics(content.rows.size, content.rows.sumOf { it.unitsSold }, 0.0, "presentaciones")
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryCard("Registros", metrics.count.toString(), metrics.unitLabel, KpiGreenBg, Modifier.weight(1f))
        SummaryCard("Unidades", metrics.units.compactNumber(), "vendidas", KpiBlueBg, Modifier.weight(1f))
        SummaryCard(if (metrics.amount > 0) "Importe" else "Período", if (metrics.amount > 0) metrics.amount.asCurrency() else "Activo", if (metrics.amount > 0) "C$" else "seleccionado", KpiYellowBg, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(title: String, value: String, caption: String, tint: Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.fillMaxWidth().padding(10.dp)) {
            Text(title, fontSize = 9.sp, color = TextMuted)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(caption, fontSize = 8.sp, color = PrimaryGreenButton, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private data class ChartRow(val label: String, val value: Double, val detail: String)

private fun ReportContent.toChartRows(): List<ChartRow> = when (this) {
    is ReportContent.LineFlavor -> rows.sortedByDescending { it.amount }.take(8).map { ChartRow("${it.lineName} · ${it.flavorName}", it.amount, "${it.unitsSold.compactNumber()} u · ${it.amount.asCurrency()}") }
    is ReportContent.Trend -> rows.groupBy { it.year to it.month }.map { (period, values) -> ChartRow("${period.second.toString().padStart(2, '0')}/${period.first}", values.sumOf { it.unitsSold }, "${values.sumOf { it.unitsSold }.compactNumber()} unidades") }.sortedBy { it.label }
    is ReportContent.Geography -> rows.sortedByDescending { it.amount }.take(8).map { ChartRow(it.municipalityName, it.amount, "${it.departmentName} · ${it.unitsSold.compactNumber()} u · ${it.amount.asCurrency()}") }
    is ReportContent.TopCustomers -> rows.sortedByDescending { it.unitsBought }.take(10).map { ChartRow(it.customerName, it.unitsBought, "${it.unitsBought.compactNumber()} unidades · ${it.amount.asCurrency()}") }
    is ReportContent.Presentation -> rows.sortedByDescending { it.unitsSold }.take(8).map { ChartRow("${it.presentationName} · ${it.lineName}", it.unitsSold, "${it.unitsSold.compactNumber()} u · ${it.participationPercentage.compactNumber()}%") }
}

@Composable
private fun BarChart(rows: List<ChartRow>) {
    val maxValue = rows.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1.0
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEachIndexed { index, row ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(row.label, modifier = Modifier.weight(1f), fontSize = 10.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(row.value.compactNumber(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                }
                Box(Modifier.fillMaxWidth().height(9.dp).background(InputBackground, RoundedCornerShape(8.dp))) {
                    Box(Modifier.fillMaxWidth((row.value / maxValue).toFloat().coerceIn(0.03f, 1f)).fillMaxHeight().background(ChartPalette[index % ChartPalette.size], RoundedCornerShape(8.dp)))
                }
            }
        }
        Text("Importe / unidades según reporte", fontSize = 9.sp, color = TextMuted)
    }
}

@Composable
private fun TrendChart(rows: List<ChartRow>) {
    val maxValue = rows.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1.0
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Canvas(Modifier.fillMaxWidth().height(170.dp)) {
            val left = 10.dp.toPx(); val right = size.width - 8.dp.toPx(); val top = 10.dp.toPx(); val bottom = size.height - 14.dp.toPx()
            repeat(4) { i ->
                val y = top + (bottom - top) * i / 3f
                drawLine(Color(0xFFE8ECF2), Offset(left, y), Offset(right, y), strokeWidth = 1.dp.toPx())
            }
            val points = rows.mapIndexed { index, row ->
                val x = if (rows.size <= 1) (left + right) / 2 else left + (right - left) * index / (rows.size - 1)
                val y = bottom - (bottom - top) * (row.value / maxValue).toFloat()
                Offset(x, y)
            }
            if (points.size > 1) {
                val path = Path().apply { moveTo(points.first().x, points.first().y); points.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(path, PrimaryGreenButton, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
            }
            points.forEach { drawCircle(Color.White, radius = 5.dp.toPx(), center = it); drawCircle(PrimaryGreenButton, radius = 3.dp.toPx(), center = it) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            rows.take(6).mapIndexed { index, row -> if (rows.size > 6 && index == 5) "…" else row.label.substringBefore('/') }.forEach { Text(it, fontSize = 8.sp, color = TextMuted) }
        }
        rows.maxByOrNull { it.value }?.let { Text("Máximo: ${it.label} · ${it.detail}", fontSize = 10.sp, color = TextMuted) }
    }
}

@Composable
private fun DonutChart(rows: List<ChartRow>) {
    val values = rows.map { max(0.0, it.value) }
    val total = values.sum().takeIf { it > 0 } ?: 1.0
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(145.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                var start = -90f
                values.forEachIndexed { index, value ->
                    val sweep = (value / total * 360).toFloat()
                    drawArc(ChartPalette[index % ChartPalette.size], start, sweep, false, style = Stroke(width = 22.dp.toPx()))
                    start += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(total.toInt().toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("unidades", fontSize = 9.sp, color = TextMuted)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            rows.take(7).forEachIndexed { index, row ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(8.dp).background(ChartPalette[index % ChartPalette.size], RoundedCornerShape(3.dp)))
                    Text(row.label, Modifier.weight(1f), fontSize = 9.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${(row.value / total * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun ReportDetails(content: ReportContent) {
    val rows = content.toChartRows()
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text("Detalle del reporte", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(7.dp))
            rows.forEachIndexed { index, row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1}.", fontSize = 10.sp, color = PrimaryGreenButton, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
                    Column(Modifier.weight(1f)) {
                        Text(row.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(row.detail, fontSize = 9.sp, color = TextMuted)
                    }
                }
                if (index < rows.lastIndex) HorizontalDivider(color = InputBackground)
            }
        }
    }
}

private fun Number.compactNumber(): String = NumberFormat.getNumberInstance(Locale("es", "NI")).apply { maximumFractionDigits = 1 }.format(toDouble())
private fun Number.asCurrency(): String = NumberFormat.getNumberInstance(Locale("es", "NI")).apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }.format(toDouble())
private fun LocalYear() = java.time.LocalDate.now().year
