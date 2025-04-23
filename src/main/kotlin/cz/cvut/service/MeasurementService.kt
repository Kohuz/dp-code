package cz.cvut.service

import cz.cvut.model.measurement.MeasurementLatest
import cz.cvut.model.measurement.MeasurementMonthly
import cz.cvut.model.measurement.MeasurementYearly
import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.stationElement.StationElementRepository
import cz.cvut.service.RecordService.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
data class ValueStats(
    val element: String,
    val highest: Double?,
    val lowest: Double?,
    val average: Double?
)

class MeasurementService(private val measurementRepository: MeasurementRepository, private val stationElementRepository: StationElementRepository) {
    fun getMeasurementsDaily(stationId: String, dateFrom: String, dateTo: String, element: String): List<MeasurementDaily> {
        return measurementRepository.getMeasurementsDailyByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    fun getMeasurementsMonthly(stationId: String, dateFrom: String, dateTo: String, element: String): List<MeasurementMonthly> {
        return measurementRepository.getMeasurementsMonthlyByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    fun getMeasurementsYearly(stationId: String, dateFrom: String, dateTo: String, element: String): List<MeasurementYearly> {
        return measurementRepository.getMeasurementsYearlyByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    fun getActualMeasurements(stationId: String): List<MeasurementLatest> {
        val elements = stationElementRepository.getElementsForStation(stationId)
        val measurements: MutableList<MeasurementLatest> = mutableListOf()
        elements.forEach{ element ->
            val measurement = measurementRepository.getLatestMeasurement(element, stationId)
            if (measurement != null) {
                measurements.add(measurement)
            }
        }
        return measurements
    }

    fun getStatsDayLongTerm(stationId: String, date: String): List<ValueStats> {
        val parsedDate = LocalDate.parse(date)
        val records = measurementRepository.getLongTermMeasurementsDaily(stationId, null)
        val filteredRecords = records.filter {
            it.date.dayOfMonth == parsedDate.dayOfMonth &&
                    it.date.month == parsedDate.month
        }

        val groupedRecords = filteredRecords.groupBy { it.element }

        val statsList = groupedRecords.map { (element, recordsForType) ->
            val highest = recordsForType.maxByOrNull { it.value ?: Double.MIN_VALUE }
            val lowest = recordsForType.minByOrNull { it.value ?: Double.MAX_VALUE }
            val average = recordsForType.mapNotNull { it.value }.average()

            ValueStats(
                element = element,
                highest = highest?.value,
                lowest = lowest?.value,
                average = if (recordsForType.isNotEmpty()) average else null
            )
        }

        return statsList
    }

    fun getStatsMonthLongTerm(stationId: String, date: String): List<ValueStats> {
        val parsedDate = LocalDate.parse(date)
        val records = measurementRepository.getLongTermMeasurementsMonthly(stationId, null)

        val filteredRecords = records.filter {
            Month.of(it.month) == parsedDate.month && it.timeFunction == "AVG"
        }

        val groupedRecords = filteredRecords.groupBy { it.element }

        val statsList = groupedRecords.map { (element, recordsForType) ->
            val highest = recordsForType
                .filter { it.mdFunction == "MAX" }
                .maxByOrNull { it.value ?: Double.MIN_VALUE }
                ?.value

            val lowest = recordsForType
                .filter { it.mdFunction == "MIN" }
                .minByOrNull { it.value ?: Double.MAX_VALUE }
                ?.value

            val average = recordsForType
                .filter { it.mdFunction == "AVG" }
                .mapNotNull { it.value }
                .takeIf { it.isNotEmpty() }
                ?.average()

            // Return ValueStats for this element
            ValueStats(
                element = element,
                highest = highest,
                lowest = lowest,
                average = average
            )
        }

        return statsList
    }

    fun getStatsDay(stationId: String, date: String): List<MeasurementDaily> {
        val parsedDate = LocalDate.parse(date)
        return measurementRepository.getStatsDay(stationId, parsedDate)
    }

    fun getMeasurementsForDayAndMonth(stationId: String, date: String, element: String): List<MeasurementDaily> {
        val records = measurementRepository.getLongTermMeasurementsDaily(stationId, element)
        val parsedDate = LocalDate.parse(date)

        val filteredRecords = records.filter {
            it.date.dayOfMonth == parsedDate.dayOfMonth &&
                    it.date.month == parsedDate.month
        }

        return filteredRecords
    }

    fun getMeasurementsForMonth(stationId: String, date: String, element: String): List<MeasurementMonthly> {
        val records = measurementRepository.getLongTermMeasurementsMonthly(stationId, element)

        val parsedDate = LocalDate.parse(date)

        val filteredRecords = records.filter {
            Month.of(it.month) == parsedDate.month && it.mdFunction == "AVG"
        }

        return filteredRecords
    }

    fun deleteOldLatest() {
        measurementRepository.deleteOldLatest()
    }

    fun getTopMeasurements(element: String, date: String? = null): List<MeasurementDaily> {
        return measurementRepository.getTopMeasurementsDailyByElementAndStationOrDate(element,
            date?.let { LocalDate.parse(it) })
    }
}
