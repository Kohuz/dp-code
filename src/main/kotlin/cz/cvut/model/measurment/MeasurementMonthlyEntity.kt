package cz.cvut.model.measurement

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.MeasurementMonthlyTable
import cz.cvut.model.station.StationEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementMonthlyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementMonthlyEntity>(MeasurementMonthlyTable)

    var stationId by MeasurementMonthlyTable.stationId
    var element by MeasurementMonthlyTable.element
    var year by MeasurementMonthlyTable.year
    var month by MeasurementMonthlyTable.month
    var timeFunction by MeasurementMonthlyTable.timeFunction
    var mdFunction by MeasurementMonthlyTable.mdFunction
    var value by MeasurementMonthlyTable.value
    var flagRepeat by MeasurementMonthlyTable.flagRepeat
    var flagInterrupted by MeasurementMonthlyTable.flagInterrupted

    var station by StationEntity referencedOn MeasurementMonthlyTable.stationId

}

fun MeasurementMonthlyEntity.toMeasurement(): MeasurementMonthly {
    return MeasurementMonthly(
        stationId = this.stationId,
        element = this.element,
        year = this.year,
        month = this.month,
        timeFunction = this.timeFunction,
        mdFunction = this.mdFunction,
        value = this.value,
        flagRepeat = this.flagRepeat,
        flagInterrupted = this.flagInterrupted
    )
}

fun MeasurementMonthly.toMeasurementEntity(): MeasurementMonthlyEntity {
    return MeasurementMonthlyEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        year = this@toMeasurementEntity.year
        month = this@toMeasurementEntity.month
        timeFunction = this@toMeasurementEntity.timeFunction
        mdFunction = this@toMeasurementEntity.mdFunction
        value = this@toMeasurementEntity.value
        flagRepeat = this@toMeasurementEntity.flagRepeat
        flagInterrupted = this@toMeasurementEntity.flagInterrupted
    }
}
