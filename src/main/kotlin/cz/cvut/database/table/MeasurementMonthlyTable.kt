package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object MeasurementMonthlyTable : IntIdTable() {
    val element = varchar("element", 10)
    val year = integer("year")
    val month = integer("month")
    val timeFunction = varchar("time_function", 20)
    val mdFunction = varchar("md_function", 20)
    val value = double("value").nullable()
    val flagRepeat = varchar("flag_repeat", 10).nullable()
    val flagInterrupted = varchar("flag_interrupted", 10).nullable()
    val stationId = reference("station_id", StationTable.stationId)

}