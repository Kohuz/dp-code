package cz.cvut.model.measurement

import cz.cvut.database.table.MeasurementLatest
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementLatestEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementLatestEntity>(MeasurementLatest)

    var stationId by MeasurementLatest.stationId
    var element by MeasurementLatest.element
    var timestamp by MeasurementLatest.timestamp
    var value by MeasurementLatest.value
    var flag by MeasurementLatest.flag
    var quality by MeasurementLatest.quality
}

fun MeasurementLatestEntity.toMeasurement(): cz.cvut.model.measurement.MeasurementLatest {
    return MeasurementLatest(
        stationId = this.stationId,
        element = this.element,
        timestamp = this.timestamp,
        value = this.value,
        flag = this.flag,
        quality = this.quality
    )
}

fun MeasurementLatest.toMeasurementEntity(): MeasurementLatestEntity {
    return MeasurementLatestEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        timestamp = this@toMeasurementEntity.timestamp.toString()
        value = this@toMeasurementEntity.value
        flag = this@toMeasurementEntity.flag
        quality = this@toMeasurementEntity.quality
    }
}
