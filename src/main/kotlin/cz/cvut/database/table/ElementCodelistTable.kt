package cz.cvut.database.table

import cz.cvut.database.StationElementTable.varchar
import org.jetbrains.exposed.dao.id.IntIdTable

object ElementCodelistTable: IntIdTable() {
    val abbreviation = varchar("abbreviation", 255)
    val name = varchar("name", 50)
    val unit = varchar("unit", 10)
}