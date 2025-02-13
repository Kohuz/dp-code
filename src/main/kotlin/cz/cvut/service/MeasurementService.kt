package cz.cvut.service

import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.repository.measurment.MeasurementRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MeasurementService(private val repository: MeasurementRepository) {
    fun getMeasurements(stationId: String, dateFrom: String, dateTo: String, element: String, resolution: String) { //TODO: resolution
        repository.getMeasurementsByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element, resolution)
    }

    fun getActualMeasurements(stationId: String) {
        repository.getLatestMeasurement(stationId)
    }

    fun getRecentMeasurements(stationId: String) {
        repository.getRecentMeasurements(stationId)
    }

    fun getStatsDayLongTerm(stationId: String, day: Int): List<Pair<MeasurementDaily?, MeasurementDaily?>> {
        val records = recordRepository.getStatsDayLongTerm(stationId)
        val filteredRecords = records.filter {
            it.recordDate.dayOfMonth == day
        }

        return filteredRecords
            .groupBy { it.element }
            .map { (_, elementRecords) ->
                val highest = elementRecords.maxByOrNull { it.value ?: Double.MIN_VALUE }
                val lowest = elementRecords.minByOrNull { it.value ?: Double.MAX_VALUE }
                Pair(highest, lowest)
            }
    }

    // Filters and processes day-specific stats for a given date and station (date filtering done in the service)
    fun getStatsDay(stationId: String, date: LocalDate): List<Pair<StationRecord?, StationRecord?>> {
        val records = recordRepository.getStatsDay(stationId, date)

        return records
            .groupBy { it.element }
            .map { (_, elementRecords) ->
                val highest = elementRecords.maxByOrNull { it.value ?: Double.MIN_VALUE }
                val lowest = elementRecords.minByOrNull { it.value ?: Double.MAX_VALUE }
                Pair(highest, lowest)
            }
    }

}
