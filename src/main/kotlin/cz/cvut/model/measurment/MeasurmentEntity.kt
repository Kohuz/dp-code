package cz.cvut.model.measurment

import cz.cvut.database.table.MeasurementDailyTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementEntity>(MeasurementDailyTable)

    var stationId by MeasurementDailyTable.stationId
    var element by MeasurementDailyTable.element
    var date by MeasurementDailyTable.date
    var value by MeasurementDailyTable.value
    var flag by MeasurementDailyTable.flag
    var quality by MeasurementDailyTable.quality
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