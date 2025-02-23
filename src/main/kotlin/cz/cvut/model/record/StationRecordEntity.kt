package cz.cvut.model.record

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.StationRecordTable
import cz.cvut.model.measurment.toMeasurement
import cz.cvut.model.measurment.toMeasurementEntity
import cz.cvut.model.station.StationEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class StationRecordEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StationRecordEntity>(StationRecordTable)

    var stationId by MeasurementDailyTable.stationId
    var element by StationRecordTable.element
    var recordType by StationRecordTable.recordType
    var value by StationRecordTable.value
    var recordDate by StationRecordTable.recordDate

    var station by StationEntity referencedOn MeasurementDailyTable.stationId

}

fun StationRecordEntity.toStationRecord(): StationRecord {
    return StationRecord(
        stationId = this.stationId,
        element = this.element,
        recordType = this.recordType,
        value = this.value,
        recordDate = this.recordDate
    )
}

fun StationRecord.toStationRecordEntity(): StationRecordEntity {
    return StationRecordEntity.new {
        stationId = this@toStationRecordEntity.stationId
        element = this@toStationRecordEntity.element
        recordType = this@toStationRecordEntity.recordType
        value = this@toStationRecordEntity.value
        recordDate = this@toStationRecordEntity.recordDate
    }
}
