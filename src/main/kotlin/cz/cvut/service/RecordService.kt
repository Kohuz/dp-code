package cz.cvut.service

import cz.cvut.model.record.StationRecord
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.record.RecordRepository
import cz.cvut.repository.stationElement.StationElementRepository
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SortOrder

class RecordService(private val recordRepository: RecordRepository, private val stationElementRepository: StationElementRepository) {
    data class RecordStats(
        val highest: StationRecord?,
        val lowest: StationRecord?,
        val average: Double?
    )

    private fun getHighestAndLowest(records: List<StationRecord>): Pair<StationRecord?, StationRecord?> {
        val highest = records.maxByOrNull { it.value ?: Double.MIN_VALUE }
        val lowest = records.minByOrNull { it.value ?: Double.MAX_VALUE }
        return Pair(highest, lowest)
    }

    private fun getStats(records: List<StationRecord>): RecordStats {
        val highest = records.maxByOrNull { it.value ?: Double.MIN_VALUE }
        val lowest = records.minByOrNull { it.value ?: Double.MAX_VALUE }
        val average = records.mapNotNull { it.value }.average().takeIf { !it.isNaN() }
        return RecordStats(highest, lowest, average)
    }

    fun getAllTimeRecords(): List<RecordStats> {
        val allRecords = recordRepository.getAllTimeRecords()
        return allRecords.groupBy { it.element }
            .map { (_, records) -> getStats(records) }
    }

    fun getAllTimeRecordsForStation(stationId: String): List<RecordStats> {
        val stationRecords = recordRepository.getAllTimeRecordsForStation(stationId)
        return stationRecords.groupBy { it.element }
            .map { (_, records) -> getStats(records) }
    }

    fun getDailyRecords(date: LocalDate): List<Pair<StationRecord?, StationRecord?>> {
        val dailyRecords = recordRepository.getDailyRecords(date)
        return dailyRecords.groupBy { it.element }
            .map { (_, records) -> getHighestAndLowest(records) }
    }

    fun getDailyRecordsForStation(stationId: String, date: LocalDate): List<Pair<StationRecord?, StationRecord?>> {
        val stationRecords = recordRepository.getDailyRecordsForStation(stationId, date)
        return stationRecords.groupBy { it.element }
            .map { (_, records) -> getHighestAndLowest(records) }
    }

    fun calculateAndInsertRecords(stationId: String) {
        val elements = stationElementRepository.getElementsForStation(stationId)

        elements.forEach { element ->
            val maxRecord = recordRepository.getRecord(stationId, element, SortOrder.DESC)
            maxRecord?.let { (value, date) ->
                recordRepository.insertRecord(
                    StationRecord(
                        stationId = stationId,
                        element = element,
                        recordType = "max",
                        value = value,
                        recordDate = LocalDate.parse(date)
                    )
                )
            }
            if(element == "T" || element == "TMI" || element == "TMA") {
                val minRecord = recordRepository.getRecord(stationId, element, SortOrder.ASC)


                minRecord?.let { (value, date) ->
                    recordRepository.insertRecord(
                        StationRecord(
                            stationId = stationId,
                            element = element,
                            recordType = "min",
                            value = value,
                            recordDate = LocalDate.parse(date)
                        )
                    )
            }
            }
        }
    }

}
