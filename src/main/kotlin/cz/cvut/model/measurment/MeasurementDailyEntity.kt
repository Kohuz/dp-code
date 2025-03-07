package cz.cvut.model.measurment

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.MeasurementLatestTable
import cz.cvut.model.station.StationEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID


class MeasurementDailyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementDailyEntity>(MeasurementDailyTable)

    var stationId by MeasurementDailyTable.stationId
    var element by MeasurementDailyTable.element
    var date by MeasurementDailyTable.date
    var vtype by MeasurementDailyTable.vtype
    var value by MeasurementDailyTable.value
    var flag by MeasurementDailyTable.flag
    var quality by MeasurementDailyTable.quality

    var station by StationEntity referencedOn MeasurementDailyTable.stationId

}

fun MeasurementDailyEntity.toMeasurement(): MeasurementDaily {
    return MeasurementDaily(
        stationId = this.stationId,
        element = this.element,
        date = this.date,
        vtype = this.vtype,
        value = this.value,
        flag = this.flag,
        quality = this.quality,
    )
}


fun MeasurementDaily.toMeasurementEntity(): MeasurementDailyEntity {
    return MeasurementDailyEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        date = this@toMeasurementEntity.date
        vtype = this@toMeasurementEntity.vtype
        value = this@toMeasurementEntity.value
        flag = this@toMeasurementEntity.flag
        quality = this@toMeasurementEntity.quality
    }
}