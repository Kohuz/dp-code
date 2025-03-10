package cz.cvut.service

import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.model.record.StationRecord
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.record.RecordRepository
import cz.cvut.repository.stationElement.StationElementRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder

class RecordService(private val recordRepository: RecordRepository, private val stationElementRepository: StationElementRepository, private val measurementRepository: MeasurementRepository) {
    @Serializable
    data class RecordStats(
        val highest: StationRecord?,
        val lowest: StationRecord?,
        val average: Double?,
        val element: String
    )



    private fun getStats(records: List<StationRecord>, element: String): RecordStats {
        val highest = records.maxByOrNull { it.value ?: Double.MIN_VALUE }
        val lowest = records.minByOrNull { it.value ?: Double.MAX_VALUE }
        val average = records.mapNotNull { it.value }.average().takeIf { !it.isNaN() }
        return RecordStats(highest, lowest, average,element)
    }

    fun getAllTimeRecords(): List<RecordStats> {
        val allRecords = recordRepository.getAllTimeRecords()
        return allRecords.groupBy { it.element }
            .map { (element, records) -> getStats(records, element) }
    }

    fun getAllTimeRecordsForStation(stationId: String): List<RecordStats> {
        val stationRecords = recordRepository.getAllTimeRecordsForStation(stationId)
        return stationRecords.groupBy { it.element }
            .map { (element, records) -> getStats(records,element) }
    }

    fun getDailyRecords(date: LocalDate): List<RecordStats> {
        val dailyRecords = recordRepository.getDailyRecords(date)
        return dailyRecords.groupBy { it.element }
            .map { (element, records) -> getStats(records, element) }
    }

    fun getDailyRecordsForStation(stationId: String, date: LocalDate): List<RecordStats> {
        val stationRecords = recordRepository.getDailyRecordsForStation(stationId, date)
        return stationRecords.groupBy { it.element }
            .map { (element, records) -> getStats(records, element) }
    }

    fun calculateAndInsertRecords(stationId: String) {
        val elements = stationElementRepository.getElementsForStation(stationId)

        // Process each element
        elements.forEach { element ->
            // Retrieve measurements for the current element
            val measurements = measurementRepository.getMeasurementsDailyByStationandandElement(stationId, element)


                // Process non-temperature elements
                val maxRecord = measurements.maxByOrNull { it.value ?: Double.MIN_VALUE }
                val minRecord = measurements.minByOrNull { it.value ?: Double.MAX_VALUE }

                // Insert max record
                maxRecord?.let { record ->
                    recordRepository.insertRecord(
                        StationRecord(
                            stationId = stationId,
                            element = element,
                            recordType = "max",
                            value = record.value,
                            recordDate = record.date
                        )
                    )
                }

                // Insert min record
                minRecord?.let { record ->
                    recordRepository.insertRecord(
                        StationRecord(
                            stationId = stationId,
                            element = element,
                            recordType = "min",
                            value = record.value,
                            recordDate = record.date
                        )
                    )
                }
            }
        }
}
