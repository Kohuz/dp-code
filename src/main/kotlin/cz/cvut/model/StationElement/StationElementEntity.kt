package cz.cvut.model.StationElement

import cz.cvut.database.StationElementTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class StationElementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StationElementEntity>(StationElementTable)

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

fun StationElementEntity.toStationElement(): StationElement {
    return StationElement(
        stationId = this.stationId,
        observationType = this.observationType,
        beginDate = this.beginDate,
        endDate = this.endDate,
        elementAbbreviation = this.elementAbbreviation,
        elementName = this.elementName,
        unitDescription = this.unitDescription,
        height = this.height,
        schedule = this.schedule
    )
}

fun StationElement.toStationElementEntity(): StationElementEntity {
    return StationElementEntity.new {
        stationId = this@toStationElementEntity.stationId
        observationType = this@toStationElementEntity.observationType
        beginDate = this@toStationElementEntity.beginDate
        endDate = this@toStationElementEntity.endDate
        elementAbbreviation = this@toStationElementEntity.elementAbbreviation
        elementName = this@toStationElementEntity.elementName
        unitDescription = this@toStationElementEntity.unitDescription
        height = this@toStationElementEntity.height
        schedule = this@toStationElementEntity.schedule
    }
}