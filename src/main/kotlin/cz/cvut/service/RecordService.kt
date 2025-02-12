package cz.cvut.service

import cz.cvut.model.record.StationRecord
import cz.cvut.repository.record.RecordRepository
import kotlinx.datetime.LocalDate

class RecordService(private val recordRepository: RecordRepository) {

    private fun getHighestAndLowest(records: List<StationRecord>): Pair<StationRecord?, StationRecord?> {
        val highest = records.maxByOrNull { it.value ?: Double.MIN_VALUE }
        val lowest = records.minByOrNull { it.value ?: Double.MAX_VALUE }
        return Pair(highest, lowest)
    }

    fun getAllTimeRecords(): List<Pair<StationRecord?, StationRecord?>> {
        val allRecords = recordRepository.getAllTimeRecords()
        return allRecords.groupBy { it.element }
            .map { (_, records) -> getHighestAndLowest(records) }
    }

    fun getAllTimeRecordsForStation(stationId: String): List<Pair<StationRecord?, StationRecord?>> {
        val stationRecords = recordRepository.getAllTimeRecordsForStation(stationId)
        return stationRecords.groupBy { it.element }
            .map { (_, records) -> getHighestAndLowest(records) }
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
}
