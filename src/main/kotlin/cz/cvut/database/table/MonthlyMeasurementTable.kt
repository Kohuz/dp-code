package cz.cvut.database.table

import cz.cvut.database.table.DailyMeasurementTable.double
import cz.cvut.database.table.DailyMeasurementTable.nullable
import cz.cvut.database.table.DailyMeasurementTable.varchar
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object MonthlyMeasurementTable : IntIdTable() {
    val stationId = varchar("station_id", 50)
    val element = varchar("element", 10)
    val year = integer("year")
    val month = integer("month")
    val timeFunction = varchar("time_function", 20)
    val mdFunction = varchar("md_function", 20)
    val value = double("value").nullable()
    val flagRepeat = varchar("flag_repeat", 10).nullable()
    val flagInterrupted = varchar("flag_interrupted", 10).nullable()
}