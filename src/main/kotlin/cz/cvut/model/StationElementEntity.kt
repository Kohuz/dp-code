package cz.cvut.model

import cz.cvut.database.StationElementTable
import cz.cvut.database.StationElementTable.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class StationElementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass< StationElementEntity>(StationElementTable)

    var stationId by StationElementTable.stationId
    var observationType by StationElementTable.observationType
    var beginDate by StationElementTable.beginDate
    var endDate by StationElementTable.endDate
    var elementAbbreviation by StationElementTable.elementAbbreviation
    var elementName by StationElementTable.elementName
    var unitDescription by StationElementTable.unitDescription
    var height by StationElementTable.height
    var schedule by StationElementTable.schedule
}
