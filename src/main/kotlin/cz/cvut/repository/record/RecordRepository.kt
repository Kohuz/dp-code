package cz.cvut.repository.record

import cz.cvut.database.table.StationRecordTable
import cz.cvut.model.record.StationRecord
import cz.cvut.model.record.StationRecordEntity
import cz.cvut.model.record.toStationRecord
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class RecordRepository {

    fun getAllTimeRecords(): List<StationRecord> = transaction {
        StationRecordEntity.all().map { it.toStationRecord() }
    }

    fun getDailyRecords(date: LocalDate): List<StationRecord> = transaction {
        StationRecordEntity.find { StationRecordTable.recordDate eq date }
            .map { it.toStationRecord() }
    }

    fun getAllTimeRecordsForStation(stationId: String): List<StationRecord> = transaction {
        StationRecordEntity.find { StationRecordTable.stationId eq stationId }
            .map { it.toStationRecord() }
    }

    fun getDailyRecordsForStation(stationId: String, date: LocalDate): List<StationRecord> = transaction {
        StationRecordEntity.find {
            (StationRecordTable.stationId eq stationId) and (StationRecordTable.recordDate eq date)
        }.map { it.toStationRecord() }
    }
}
