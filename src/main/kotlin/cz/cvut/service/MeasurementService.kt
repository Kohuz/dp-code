package cz.cvut.service

import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.repository.measurement.MeasurementRepository
import kotlinx.datetime.LocalDate

class MeasurementService(private val measurementRepository: MeasurementRepository) {
    fun getMeasurements(stationId: String, dateFrom: String, dateTo: String, element: String, resolution: String) { //TODO: resolution
        measurementRepository.getMeasurementsByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element, resolution)
    }

    fun getActualMeasurements(stationId: String) {
        measurementRepository.getLatestMeasurement(stationId)
    }

    fun getRecentMeasurements(stationId: String) {
        measurementRepository.getRecentMeasurements(stationId)
    }

    fun getStatsDayLongTerm(stationId: String, date: String): List<Pair<MeasurementDaily?, MeasurementDaily?>> {
        val parsedDate = LocalDate.parse(date)
        val records = measurementRepository.getStatsDayLongTerm(stationId)
        val filteredRecords = records.filter {
            it.date.dayOfMonth == parsedDate.dayOfMonth &&
            it.date.month == parsedDate.month
        }

        return filteredRecords
            .groupBy { it.element }
            .map { (_, elementRecords) ->
                val highest = elementRecords.maxByOrNull { it.value ?: Double.MIN_VALUE }
                val lowest = elementRecords.minByOrNull { it.value ?: Double.MAX_VALUE }
                Pair(highest, lowest)
            }
    }

    fun getStatsDay(stationId: String, date: String): List<Pair<MeasurementDaily?, MeasurementDaily?>> {
        val parsedDate = LocalDate.parse(date)
        val records = measurementRepository.getStatsDay(stationId, parsedDate)

        return records
            .groupBy { it.element }
            .map { (_, elementRecords) ->
                val highest = elementRecords.maxByOrNull { it.value ?: Double.MIN_VALUE }
                val lowest = elementRecords.minByOrNull { it.value ?: Double.MAX_VALUE }
                Pair(highest, lowest)
            }
    }

//    fun deleteOldLatest() {
//        measurementRepository.deleteOldLatest()
//    }

}
