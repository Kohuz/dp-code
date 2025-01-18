package cz.cvut.repository

import cz.cvut.database.StationElementTable
import cz.cvut.model.StationElement
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

object StationElementRepository {
    fun saveStationElements(elements: List<StationElement>) {
        transaction {
            elements.forEach { element ->
                StationElementTable.insertIgnore {
                    it[observationType] = element.observationType
                    it[stationId] = element.stationId
                    it[beginDate] = element.beginDate
                    it[endDate] = element.endDate
                    it[elementAbbreviation] = element.elementAbbreviation
                    it[elementName] = element.elementName
                    it[unitDescription] = element.unitDescription
                    it[height] = element.height
                    it[schedule] = element.schedule
                }
            }
        }
    }
}
