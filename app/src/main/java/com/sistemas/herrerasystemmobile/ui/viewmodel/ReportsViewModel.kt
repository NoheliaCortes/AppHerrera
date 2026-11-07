package com.sistemas.herrerasystemmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import com.sistemas.herrerasystemmobile.data.repository.ReportRepository
import com.sistemas.herrerasystemmobile.ui.common.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class ReportType(val title: String) {
    LINE_FLAVOR("Ventas por línea"), TREND("Variación mensual"), GEOGRAPHY("Ventas por municipio"), TOP_CUSTOMERS("Top clientes"), PRESENTATION("Por presentación")
}

sealed class ReportContent {
    data class LineFlavor(val rows: List<SalesByLineFlavorDto>) : ReportContent()
    data class Trend(val rows: List<MonthlySalesTrendDto>) : ReportContent()
    data class Geography(val rows: List<SalesByGeographyDto>) : ReportContent()
    data class TopCustomers(val rows: List<TopCustomerDto>) : ReportContent()
    data class Presentation(val rows: List<SalesByPresentationDto>) : ReportContent()
}

data class ReportsUiData(val reportType: ReportType, val content: ReportContent, val startDate: String, val endDate: String)

class ReportsViewModel(private val repository: ReportRepository) : ViewModel() {
    private val today = LocalDate.now()
    private val _uiState = MutableStateFlow<UiState<ReportsUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ReportsUiData>> = _uiState.asStateFlow()
    private val _reportType = MutableStateFlow(ReportType.LINE_FLAVOR)
    val reportType: StateFlow<ReportType> = _reportType.asStateFlow()
    private val _year = MutableStateFlow(today.year)
    val year: StateFlow<Int> = _year.asStateFlow()
    private val _month = MutableStateFlow<Int?>(null)
    val month: StateFlow<Int?> = _month.asStateFlow()
    private var loadJob: Job? = null

    init { refresh() }

    fun selectReport(value: ReportType) { if (_reportType.value != value) { _reportType.value = value; refresh() } }
    fun selectYear(value: Int) { if (_year.value != value) { _year.value = value; refresh() } }
    fun selectMonth(value: Int?) { if (_month.value != value) { _month.value = value; refresh() } }

    fun refresh() {
        loadJob?.cancel()
        val selectedType = _reportType.value
        val selectedYear = _year.value
        val selectedMonth = _month.value
        val start = if (selectedMonth == null) LocalDate.of(selectedYear, 1, 1) else LocalDate.of(selectedYear, selectedMonth, 1)
        val end = if (selectedMonth == null) LocalDate.of(selectedYear, 12, 31) else start.withDayOfMonth(start.lengthOfMonth())
        loadJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result: Result<ReportContent> = when (selectedType) {
                ReportType.LINE_FLAVOR -> repository.salesByLineFlavor(start.toString(), end.toString()).map { ReportContent.LineFlavor(it) }
                ReportType.TREND -> repository.monthlyTrend(start.toString(), end.toString()).map { ReportContent.Trend(it) }
                ReportType.GEOGRAPHY -> repository.geography(start.toString(), end.toString()).map { ReportContent.Geography(it) }
                ReportType.TOP_CUSTOMERS -> repository.topCustomers(start.toString(), end.toString()).map { ReportContent.TopCustomers(it) }
                ReportType.PRESENTATION -> repository.presentations(start.toString(), end.toString()).map { ReportContent.Presentation(it) }
            }
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(ReportsUiData(selectedType, it, start.toString(), end.toString())) },
                onFailure = { UiState.Error(it.message ?: "No se pudo cargar el reporte") }
            )
        }
    }
}
