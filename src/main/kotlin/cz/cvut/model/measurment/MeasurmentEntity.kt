package cz.cvut.model.measurment

import cz.cvut.database.table.Measurement2Table
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementEntity>(Measurement2Table)

    var stationId by Measurement2Table.stationId
    var element by Measurement2Table.element
    var dateTime by Measurement2Table.dateTime
    var value by Measurement2Table.value
    var flag by Measurement2Table.flag
    var quality by Measurement2Table.quality
    var schedule by Measurement2Table.schedule
}
fun MeasurementEntity.toMeasurement(): Measurement {
    return Measurement(
        stationId = this.stationId,
        element = this.element,
        dateTime = this.dateTime,
        value = this.value,
        flag = this.flag,
        quality = this.quality,
        schedule = this.schedule
    )
}

fun Measurement.toMeasurementEntity(): MeasurementEntity {
    return MeasurementEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        dateTime = this@toMeasurementEntity.dateTime
        value = this@toMeasurementEntity.value
        flag = this@toMeasurementEntity.flag
        quality = this@toMeasurementEntity.quality
        schedule = this@toMeasurementEntity.schedule
    }
}