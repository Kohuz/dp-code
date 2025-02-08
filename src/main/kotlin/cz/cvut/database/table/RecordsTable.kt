package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date


object StationRecordTable : IntIdTable() {
    val station = reference("station_id", StationTable)
    val element = varchar("element", 50)
    val recordType = varchar("record_type", 20) // e.g., "max", "min", "avg"
    val value = double("value").nullable()
    val recordDate = date("record_date")
}