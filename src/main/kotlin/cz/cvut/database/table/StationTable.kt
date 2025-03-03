package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StationTable : IntIdTable() {
    val stationId = varchar("station_id", 255).uniqueIndex()
    val code = varchar("code", 255)
    val startDate = datetime("start_date")
    val endDate = datetime("end_date")
    val location = varchar("location", 255)
    val longitude = double("longitude")
    val latitude = double("latitude")
    val elevation = double("elevation")
}
