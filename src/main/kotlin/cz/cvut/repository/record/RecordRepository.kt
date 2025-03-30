package cz.cvut.repository.record

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.StationRecordTable
import cz.cvut.model.measurment.MeasurementDailyEntity
import cz.cvut.model.record.StationRecord
import cz.cvut.model.record.StationRecordEntity
import cz.cvut.model.record.toStationRecord
import cz.cvut.model.record.toStationRecordEntity
import cz.cvut.model.stationElement.StationElementEntity
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class RecordRepository {

    fun getAllTimeRecords(): List<StationRecord> = transaction {
        StationRecordEntity.all().map { it.toStationRecord() }
    }

    fun getAllTimeRecordsForStation(stationId: String): List<StationRecord> = transaction {
        StationRecordEntity.find { StationRecordTable.stationId eq stationId }
            .map { it.toStationRecord() }
    }

    fun insertRecord(stationRecord: StationRecord) {
        transaction {
            StationRecordTable.insert {
                it[stationId] = stationRecord.stationId
                it[element] = stationRecord.element
                it[recordType] = stationRecord.recordType
                it[value] = stationRecord.value
                it[recordDate] = stationRecord.recordDate
            }
        }
    }



}
