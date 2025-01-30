package cz.cvut.model.measurment

import cz.cvut.database.table.DailyMeasurementTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementEntity>(DailyMeasurementTable)

    var stationId by DailyMeasurementTable.stationId
    var element by DailyMeasurementTable.element
    var date by DailyMeasurementTable.date
    var value by DailyMeasurementTable.value
    var flag by DailyMeasurementTable.flag
    var quality by DailyMeasurementTable.quality
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