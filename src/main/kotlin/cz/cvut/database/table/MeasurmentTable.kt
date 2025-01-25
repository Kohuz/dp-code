package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Measurement2Table : IntIdTable() {
    val stationId = varchar("station_id", 50)
    val element = varchar("element", 50)
    val dateTime = datetime("date_time")
    val value = double("value")
    val flag = varchar("flag", 10).nullable()
    val quality = double("quality")
    val schedule = varchar("schedule", 50).nullable()
}
