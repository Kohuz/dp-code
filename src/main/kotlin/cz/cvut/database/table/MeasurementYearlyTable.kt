package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object MeasurementYearlyTable : IntIdTable() {
    val stationId = varchar("station_id", 50)
    val observationType = varchar("observation_type", 10) // Matches "ELEMENT"
    val year = integer("year")
    val timeFunction = varchar("time_function", 20) // Matches "TIMEFUNCTION"
    val mdFunction = varchar("md_function", 20) // Matches "MDFUNCTION"
    val value = double("value").nullable()
    val flagRepeat = varchar("flag_repeat", 10).nullable()
    val flagInterrupted = varchar("flag_interrupted", 10).nullable()
}
