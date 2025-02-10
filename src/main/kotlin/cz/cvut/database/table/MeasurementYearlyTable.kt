package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object MeasurementYearlyTable : IntIdTable() {
    val element = varchar("element", 50) // Matches "ELEMENT"
    val year = integer("year")
    val timeFunction = varchar("time_function", 20) // Matches "TIMEFUNCTION"
    val mdFunction = varchar("md_function", 20) // Matches "MDFUNCTION"
    val value = double("value").nullable()
    val flagRepeat = varchar("flag_repeat", 10).nullable()
    val flagInterrupted = varchar("flag_interrupted", 10).nullable()
    val stationId = varchar("station_id", 50)

}
