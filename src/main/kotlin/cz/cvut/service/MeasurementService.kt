package cz.cvut.service

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

    fun getStatsLongTerm(date: String, stationId: String) {
        val parsedDate = LocalDate.parse(date)
    }

    fun getActualMeasurements(stationId: String) {
        repository.getLatestMeasurement(stationId)
    }

    fun getRecentMeasurements(stationId: String) {
        repository.getRecentMeasurements(stationId)
    }

}
