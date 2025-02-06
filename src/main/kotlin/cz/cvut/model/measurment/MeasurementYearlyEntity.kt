package cz.cvut.model.measurement

import cz.cvut.database.table.MeasurementYearlyTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasurementYearlyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MeasurementYearlyEntity>(MeasurementYearlyTable)

    var stationId by MeasurementYearlyTable.stationId
    var element by MeasurementYearlyTable.element
    var year by MeasurementYearlyTable.year
    var timeFunction by MeasurementYearlyTable.timeFunction
    var mdFunction by MeasurementYearlyTable.mdFunction
    var value by MeasurementYearlyTable.value
    var flagRepeat by MeasurementYearlyTable.flagRepeat
    var flagInterrupted by MeasurementYearlyTable.flagInterrupted
}

fun MeasurementYearlyEntity.toMeasurement(): MeasurementYearly {
    return MeasurementYearly(
        stationId = this.stationId,
        element = this.element,
        year = this.year,
        timeFunction = this.timeFunction,
        mdFunction = this.mdFunction,
        value = this.value,
        flagRepeat = this.flagRepeat,
        flagInterrupted = this.flagInterrupted
    )
}

fun MeasurementYearly.toMeasurementEntity(): MeasurementYearlyEntity {
    return MeasurementYearlyEntity.new {
        stationId = this@toMeasurementEntity.stationId
        element = this@toMeasurementEntity.element
        year = this@toMeasurementEntity.year
        timeFunction = this@toMeasurementEntity.timeFunction
        mdFunction = this@toMeasurementEntity.mdFunction
        value = this@toMeasurementEntity.value
        flagRepeat = this@toMeasurementEntity.flagRepeat
        flagInterrupted = this@toMeasurementEntity.flagInterrupted
    }
}
