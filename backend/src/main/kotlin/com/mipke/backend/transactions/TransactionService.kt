package com.mipke.backend.chart

import com.mipke.exceptions.DataNotFoundException
import com.mipke.exceptions.assert.isTrue
import com.mipke.tangibleinsight.chart.converter.ChartModelConverter
import com.mipke.tangibleinsight.chart.model.ChartEntity
import com.mipke.backend.common.search.SortMapper
import com.mipke.backend.common.search.SpecificationFactory
import com.mipke.tangibleinsight.common.service.BaseService
import com.mipke.tangibleinsight.models.chart.Chart
import com.mipke.tangibleinsight.models.chart.request.CreateChartRequestPayload
import com.mipke.tangibleinsight.models.chart.request.UpdateChartRequestPayload
import com.mipke.tangibleinsight.models.chart.types.ChartType
import com.mipke.tangibleinsight.models.common.TiObjectType
import com.mipke.tangibleinsight.models.series.summary.Series
import com.mipke.tangibleinsight.models.series.types.SeriesType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChartService(
        private val specificationFactory: com.mipke.backend.common.search.SpecificationFactory<ChartEntity>,
        private val sortMapper: com.mipke.backend.common.search.SortMapper,
        chartRepository: ChartRepository,
        chartModelConverter: ChartModelConverter
) : BaseService<ChartRepository, ChartEntity, Chart>(
    chartRepository,
    chartModelConverter
) {

    override val name: String = "Chart"

    fun loadByChartId(chartId: String): Chart {
        logger.debug { "Loading chart by id: $chartId" }
        return modelConverter.toData(loadChartEntityByChartId(chartId))
    }

    private fun loadChartEntityByChartId(chartId: String): ChartEntity =
        repository.findChartEntityByChartId(chartId).let { opt ->
            return@let if (opt.isPresent) {
                opt.get()
            } else {
                throw DataNotFoundException("Chart with chart id: $chartId not found")
            }
        }

    fun loadAllChartsContainingSeries(seriesId: String) =
        findAllBySeriesListIn(seriesId).map { entity -> modelConverter.toData(entity) }

    fun searchCharts(searchExpression: String?, page: Pageable): Page<Chart> {
        val chartSpecification = specificationFactory.fromQuery(searchExpression)
        return repository.findAll(chartSpecification, sortMapper.map(page))
            .map { entity -> modelConverter.toData(entity) }
    }

    fun loadAllCharts(): List<Chart> =
        repository.findAll().map { entity -> modelConverter.toData(entity) }

    fun createNewChart(payload: CreateChartRequestPayload): Chart {
        payload.validateCreateChartRequestPayload()
        verifyChartAndSeriesTypesAlign(
            payload.type,
            serviceCoordinator.seriesSummaryService.loadMultipleBySeriesIds(payload.seriesList)
        )

        val newChart = ChartEntity(
            payload.chartId,
            payload.title,
            payload.groupId,
            payload.yaxisLabel,
            payload.xaxisLabel,
            payload.sigDecimalPlaces,
            payload.dataPrefix,
            payload.dataSuffix,
            payload.description,
            payload.seriesList,
            payload.type,
            0L
        )
        val savedEntity = repository.save(newChart)
        logger.debug { "Created new chart (${payload.chartId}): $savedEntity" }
        return modelConverter.toData(savedEntity)
    }

    fun updateChartByChartId(chartId: String, payload: UpdateChartRequestPayload): Chart {
        payload.validateUpdateChartRequestPayload()

        val existingChart = loadChartEntityByChartId(chartId)
        verifyChartAndSeriesTypesAlign(
            existingChart.type,
            serviceCoordinator.seriesSummaryService.loadMultipleBySeriesIds(payload.seriesList)
        )

        existingChart.title = payload.title
        existingChart.groupId = payload.groupId
        existingChart.yAxisLabel = payload.yaxisLabel
        existingChart.xAxisLabel = payload.xaxisLabel
        existingChart.sigDecimalPlaces = payload.sigDecimalPlaces
        existingChart.dataPrefix = payload.dataPrefix
        existingChart.dataSuffix = payload.dataSuffix
        existingChart.description = payload.description
        existingChart.seriesList = payload.seriesList
        val updatedEntity = repository.save(existingChart)
        logger.debug { "Updated chart (${existingChart.chartId}): $updatedEntity" }
        return loadByChartId(chartId)
    }

    fun updateDataLastUpdatedTimestampBySeriesId(seriesId: String) {
        val rightNowInSeconds = Date().time / 1000L
        val chartsContainingSeries = findAllBySeriesListIn(seriesId)
        chartsContainingSeries.forEach { chart ->
            logger.debug { "Updated chart last updated timestamp for chart ${chart.chartId}" }
            chart.dataLastUpdated = rightNowInSeconds
        }

        repository.saveAll(chartsContainingSeries)
    }

    private fun verifyChartAndSeriesTypesAlign(chartType: ChartType, seriesList: List<Series>) {
        val matchingSeriesTyp = when (chartType) {
            ChartType.CROSS_SECTIONAL_COMBINED, ChartType.CROSS_SECTIONAL_SCATTER, ChartType.CROSS_SECTIONAL_LINE -> SeriesType.GENERAL_CS
            ChartType.TIME -> SeriesType.GENERAL_TS
            ChartType.FUTURE -> SeriesType.FUTURE
            ChartType.COT_FF -> SeriesType.COT_FF
            ChartType.COT_DISAGGREGATED -> SeriesType.COT_DISAGGREGATED
        }
        isTrue(
            seriesList.all { series -> series.type == matchingSeriesTyp },
            "One or more selected series is not the required series type $matchingSeriesTyp for chart type $chartType"
        )
    }

    private fun CreateChartRequestPayload.validateCreateChartRequestPayload() {
        isTrue(this.chartId.isNotEmpty(), "Chart Id must not be empty")
        isTrue(
                this.chartId.matches("^[A-Z0-9-]+\$".toRegex()),
                "Chart Id must consist of uppercase letters, numbers or dashes"
        )
        this.validateUpdateChartRequestPayload()
    }

    private fun UpdateChartRequestPayload.validateUpdateChartRequestPayload() {
        isTrue(this.title.isNotEmpty(), "Chart title must not be left empty")
        isTrue(this.yaxisLabel.isNotEmpty(), "Chart X-Axis label must not be left empty")
        isTrue(this.xaxisLabel.isNotEmpty(), "Chart Y-Axis label must not be left empty")
        isTrue(this.sigDecimalPlaces >= 0, "Chart significant decimal places must be greater than or equal to 0")
        isTrue(this.description.isNotEmpty(), "Chart description must not be left empty")
        isTrue(this.seriesList.isNotEmpty(), "Chart series list must not be empty")
        this.groupId.validateGroupId()
    }

    private fun String.validateGroupId() {
        val group = serviceCoordinator.groupService.loadById(this)
        isTrue(group.objectType == TiObjectType.CHART, "Chart must be associated to a 'CHART' group")
    }

    private fun findAllBySeriesListIn(seriesId: String) =
            repository.findAllBySeriesListIn(listOf(seriesId))
}
