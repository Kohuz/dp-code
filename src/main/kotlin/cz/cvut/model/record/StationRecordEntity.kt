//package cz.cvut.model.record
//
//import cz.cvut.database.table.StationRecordTable
//import cz.cvut.model.station.StationEntity
//import org.jetbrains.exposed.dao.IntEntity
//import org.jetbrains.exposed.dao.IntEntityClass
//import org.jetbrains.exposed.dao.id.EntityID
//
//class StationRecordEntity(id: EntityID<Int>) : IntEntity(id) {
//    companion object : IntEntityClass<StationRecordEntity>(StationRecordTable)
//
//    var station by StationEntity referencedOn StationRecordTable.station
//    var stationId by StationRecordTable.station
//    var element by StationRecordTable.element
//    var recordType by StationRecordTable.recordType
//    var value by StationRecordTable.value
//    var recordDate by StationRecordTable.recordDate
//}
//
//fun StationRecordEntity.toStationRecord(): StationRecord {
//    return StationRecord(
//        stationId = this.station.stationId,
//        element = this.element,
//        recordType = this.recordType,
//        value = this.value,
//        recordDate = this.recordDate
//    )
//}
//
//fun StationRecord.toStationRecordEntity(): StationRecordEntity {
//    return StationRecordEntity.new {
//        station = this@toStationRecordEntity.stationId
//        element = this@toStationRecordEntity.element
//        recordType = this@toStationRecordEntity.recordType
//        value = this@toStationRecordEntity.value
//        recordDate = this@toStationRecordEntity.recordDate
//    }
//}
