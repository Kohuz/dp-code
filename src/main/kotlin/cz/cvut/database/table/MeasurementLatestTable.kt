package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object MeasurementLatestTable : IntIdTable() {
    val stationId = varchar("station_id", 50)
    val element = varchar("element", 50)
    val timestamp = date("timestamp")
    val value = double("value").nullable()
    val flag = varchar("flag", 10).nullable()
    val quality = double("quality").nullable()

}
