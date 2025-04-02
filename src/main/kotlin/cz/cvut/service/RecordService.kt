package cz.cvut.service

import cz.cvut.model.record.StationRecord
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.record.RecordRepository
import cz.cvut.repository.stationElement.StationElementRepository
import kotlinx.serialization.Serializable

class RecordService(private val recordRepository: RecordRepository, private val stationElementRepository: StationElementRepository, private val measurementRepository: MeasurementRepository) {
    @Serializable
    data class RecordStats(
        val highest: StationRecord?,
        val lowest: StationRecord?,
        val average: Double?,
        val element: String
    )



    fun getStats(records: List<StationRecord>, element: String): RecordStats {
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

    fun calculateAndInsertRecords(stationId: String) {
        val elements = stationElementRepository.getElementsForStation(stationId)
        val allowedElements = setOf("TMA", "TMI", "Fmax", "SNO", "SCE", "SRA")
        // Process each element
        elements.filter { it in allowedElements }.forEach { element ->
            // Retrieve measurements for the current element
            val measurements = measurementRepository.getMeasurementsDailyByStationandandElement(stationId, element)


            when (element) {
                "TMI" -> {
                    // Only insert MIN record for TMI
                    measurements.minByOrNull { it.value!! }?.let { record ->
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

                else -> {
                    // Only insert MAX record for other elements
                    measurements.maxByOrNull { it.value!! }?.let { record ->
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
                }
            }

            }
        }
}
