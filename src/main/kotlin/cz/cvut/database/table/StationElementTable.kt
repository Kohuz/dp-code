package cz.cvut.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StationElementTable : Table("station_element") {
    val observationType = varchar("observation_type", 10)
    val stationId = varchar("station_id", 50)
    val beginDate = datetime("begin_date")
    val endDate = datetime("end_date")
    val elementAbbreviation = varchar("element_abbreviation", 10)
    val elementName = varchar("element_name", 255)
    val unitDescription = varchar("unit_description", 255)
    val height = double("height")
    val schedule = varchar("schedule", 255)

    override val primaryKey = PrimaryKey(stationId, elementAbbreviation, observationType)
}
