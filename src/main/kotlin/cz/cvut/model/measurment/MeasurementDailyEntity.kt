package cz.cvut.model.measurment

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.StationTable
import cz.cvut.model.station.StationEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID


class MeasurementDailyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementDailyEntity>(MeasurementDailyTable)

    var stationId by MeasurementDailyTable.stationId
    var element by MeasurementDailyTable.element
    var date by MeasurementDailyTable.date
    var value by MeasurementDailyTable.value
    var flag by MeasurementDailyTable.flag
    var quality by MeasurementDailyTable.quality
    var schedule by MeasurementDailyTable.schedule

    var station by StationEntity referencedOn MeasurementDailyTable.stationId

}

fun MeasurementDailyEntity.toMeasurement(): MeasurementDaily {
    return MeasurementDaily(
        stationId = this.stationId,
        element = this.element,
        date = this.date,
        value = this.value,
        flag = this.flag,
        quality = this.quality,
        schedule = this.schedule
    )
}


fun MeasurementDaily.toMeasurementEntity(): MeasurementDailyEntity {
    return MeasurementDailyEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        date = this@toMeasurementEntity.date
        value = this@toMeasurementEntity.value
        flag = this@toMeasurementEntity.flag
        quality = this@toMeasurementEntity.quality
        schedule = this@toMeasurementEntity.schedule
    }
}