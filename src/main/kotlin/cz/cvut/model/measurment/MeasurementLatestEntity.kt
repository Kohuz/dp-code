package cz.cvut.model.measurement

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.MeasurementLatestTable
import cz.cvut.model.measurment.MeasurementDailyEntity
import cz.cvut.model.station.StationEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementLatestEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementLatestEntity>(MeasurementLatestTable)

    var stationId by MeasurementLatestTable.stationId
    var element by MeasurementLatestTable.element
    var timestamp by MeasurementLatestTable.timestamp
    var value by MeasurementLatestTable.value
    var flag by MeasurementLatestTable.flag
    var quality by MeasurementLatestTable.quality
    var createdAt by MeasurementLatestTable.createdAt

    var station by StationEntity referencedOn MeasurementDailyTable.stationId
}

fun MeasurementLatestEntity.toMeasurement(): cz.cvut.model.measurement.MeasurementLatest {
    return MeasurementLatest(
        stationId = this.stationId,
        element = this.element,
        timestamp = this.timestamp,
        value = this.value,
        flag = this.flag,
        quality = this.quality,
        createdAt = this.createdAt
    )
}

fun MeasurementLatest.toMeasurementEntity(): MeasurementLatestEntity {
    return MeasurementLatestEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        timestamp = this@toMeasurementEntity.timestamp
        value = this@toMeasurementEntity.value
        flag = this@toMeasurementEntity.flag
        quality = this@toMeasurementEntity.quality
    }
}
