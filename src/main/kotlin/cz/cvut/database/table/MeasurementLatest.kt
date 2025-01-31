package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object MeasurementLatest : IntIdTable() {
    val stationId = varchar("station_id", 10)
    val element = varchar("element", 50)
    val vtype = varchar("vtype", 10)
    val timestamp = varchar("timestamp", 20)
    val value = varchar("value", 50).nullable()
    val flag = varchar("flag", 10).nullable()
    val quality = double("quality").nullable()

}
