package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MeasurementLatestTable : IntIdTable() {
    val stationId = reference("station_id", StationTable.stationId)
    val element = varchar("element", 50)
    val timestamp = date("timestamp")
    val value = double("value").nullable()
    val flag = varchar("flag", 10).nullable()
    val quality = double("quality").nullable()
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)

}
