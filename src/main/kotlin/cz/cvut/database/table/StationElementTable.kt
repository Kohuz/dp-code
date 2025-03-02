package cz.cvut.database

import cz.cvut.database.table.StationTable
import cz.cvut.database.table.StationTable.nullable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StationElementTable : IntIdTable() {
    val stationId = reference("station_id", StationTable.stationId)
    val beginDate = datetime("begin_date").nullable()
    val endDate = datetime("end_date")
    val elementAbbreviation = varchar("element_abbreviation", 10)
    val elementName = varchar("element_name", 255)
    val unitDescription = varchar("unit_description", 255)
    val height = double("height").nullable()
    val schedule = varchar("schedule", 255)
}
