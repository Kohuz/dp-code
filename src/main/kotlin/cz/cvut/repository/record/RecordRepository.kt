package cz.cvut.repository.record

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.StationRecordTable
import cz.cvut.model.measurment.MeasurementDailyEntity
import cz.cvut.model.record.StationRecord
import cz.cvut.model.record.StationRecordEntity
import cz.cvut.model.record.toStationRecord
import cz.cvut.model.record.toStationRecordEntity
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    fun getRecord(stationId: String, element: String, order: SortOrder): Pair<Double?, String>? {
        return transaction {
            MeasurementDailyEntity
                .find { (MeasurementDailyTable.stationId eq stationId) and (MeasurementDailyTable.element eq element) }
                .orderBy(MeasurementDailyTable.value to order)
                .limit(1)
                .firstOrNull()?.let {
                    it.value to it.date.toString()
                }
        }
    }

    fun insertRecord(stationRecord: StationRecord) {
        transaction {
            stationRecord.toStationRecordEntity()
        }
    }


    fun getElementsForStation(stationId: String): List<String> {
        return transaction {
            MeasurementDailyEntity
                .find { MeasurementDailyTable.stationId eq stationId }
                .distinctBy { it.element }
                .map { it.element }
        }
    }
}
