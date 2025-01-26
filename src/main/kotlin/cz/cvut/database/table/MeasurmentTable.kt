package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MeasurementTable : IntIdTable() {
    val stationId = varchar("station_id", 50)
    val element = varchar("element", 50)
    val date = date("date")
    val vtype = varchar("vtype", 50)
    val value = double("value").nullable()
    val flag = varchar("flag", 10).nullable()
    val quality = double("quality").nullable()
}
