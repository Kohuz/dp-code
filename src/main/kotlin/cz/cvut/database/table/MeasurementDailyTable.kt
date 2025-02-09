package cz.cvut.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object MeasurementDailyTable : IntIdTable() {
    val element = varchar("element", 50)
    val date = date("date")
    val vtype = varchar("vtype", 50)
    val value = double("value").nullable()
    val flag = varchar("flag", 10).nullable()
    val quality = double("quality").nullable()
    val station = reference("stationId", StationTable.stationId)

}
