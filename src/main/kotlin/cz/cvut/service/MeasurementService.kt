package cz.cvut.service

import cz.cvut.model.measurement.MeasurementLatest
import cz.cvut.model.measurement.MeasurementMonthly
import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.stationElement.StationElementRepository
import cz.cvut.service.RecordService.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

data class ValueStats(
    val highest: Double?, // Highest value
    val lowest: Double?,  // Lowest value
    val average: Double?  // Average value
)

class MeasurementService(private val measurementRepository: MeasurementRepository, private val stationElementRepository: StationElementRepository) {
    fun getMeasurementsDaily(stationId: String, dateFrom: String, dateTo: String, element: String) {
        return measurementRepository.getMeasurementsDailyByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    fun getMeasurementsMonthly(stationId: String, dateFrom: String, dateTo: String, element: String) {
        return measurementRepository.getMeasurementsMonthlyByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    fun getMeasurementsYearly(stationId: String, dateFrom: String, dateTo: String, element: String) {
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
    fun getActualTemperatures(stationId: String): Double? {
        val measurement = measurementRepository.getLatestMeasurement("T", stationId)
        if (measurement != null) {
            return measurement.value
        }
        return null
    }

    fun getRecentMeasurements(stationId: String) {
        return measurementRepository.getRecentMeasurements(stationId)
    }

    fun getStatsDayLongTerm(stationId: String, date: String): ValueStats {
        val parsedDate = LocalDate.parse(date)
        val records = measurementRepository.getLongTermMeasurementsDaily(stationId)

        // Filter records for the same day and month across different years
        val filteredRecords = records.filter {
            it.date.dayOfMonth == parsedDate.dayOfMonth &&
                    it.date.month == parsedDate.month
        }

        // Calculate highest, lowest, and average values
        val highest = filteredRecords.maxByOrNull { it.value ?: Double.MIN_VALUE }
        val lowest = filteredRecords.minByOrNull { it.value ?: Double.MAX_VALUE }
        val average = filteredRecords.mapNotNull { it.value }.average()

        return ValueStats(
            highest = highest?.value,
            lowest = lowest?.value,
            average = if (filteredRecords.isNotEmpty()) average else null
        )
    }

    fun getStatsDay(stationId: String, date: String): List<MeasurementDaily> {
        val parsedDate = LocalDate.parse(date)
        return measurementRepository.getStatsDay(stationId, parsedDate)

    }

    fun getMeasurementsForDayAndMonth(stationId: String, date: String): List<MeasurementDaily> {
        // Fetch all long-term records for the station
        val records = measurementRepository.getLongTermMeasurementsDaily(stationId)

        val parsedDate = LocalDate.parse(date)

        // Filter records for the same day and month across different years
        val filteredRecords = records.filter {
            it.date.dayOfMonth == parsedDate.dayOfMonth &&
                    it.date.month == parsedDate.month
        }

        return filteredRecords
    }

    fun getMeasurementsForMonth(stationId: String, date: String): List<MeasurementMonthly> {
        // Fetch all long-term records for the station
        val records = measurementRepository.getLongTermMeasurementsMonthly(stationId)

        val parsedDate = LocalDate.parse(date)

        // Filter records for the same day and month across different years
        val filteredRecords = records.filter {
            Month.of(it.month) == parsedDate.month
        }

        return filteredRecords
    }

//    fun deleteOldLatest() {
//        measurementRepository.deleteOldLatest()
//    }

}
