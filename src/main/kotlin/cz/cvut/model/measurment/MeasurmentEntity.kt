package cz.cvut.model.measurment

import cz.cvut.database.table.MeasurementTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementEntity>(MeasurementTable)

    var stationId by MeasurementTable.stationId
    var element by MeasurementTable.element
    var date by MeasurementTable.date
    var value by MeasurementTable.value
    var flag by MeasurementTable.flag
    var quality by MeasurementTable.quality
}
fun MeasurementEntity.toMeasurement(): Measurement {
    return Measurement(
        stationId = this.stationId,
        element = this.element,
        date = this.date,
        value = this.value,
        flag = this.flag,
        quality = this.quality,
    )
}

fun Measurement.toMeasurementEntity(): MeasurementEntity {
    return MeasurementEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        date = this@toMeasurementEntity.date
        value = this@toMeasurementEntity.value
        flag = this@toMeasurementEntity.flag
        quality = this@toMeasurementEntity.quality
    }
}