package cz.cvut.repository.stationElement

import cz.cvut.database.StationElementTable
import cz.cvut.model.stationElement.StationElement
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
    fun getStationElementsByStationId(stationId: String): List<StationElement> {
        return transaction {
            StationElementTable
                .select ( StationElementTable.stationId eq stationId )
                .map { row ->
                    StationElement(
                        observationType = row[StationElementTable.observationType],
                        stationId = row[StationElementTable.stationId],
                        beginDate = row[StationElementTable.beginDate],
                        endDate = row[StationElementTable.endDate],
                        elementAbbreviation = row[StationElementTable.elementAbbreviation],
                        elementName = row[StationElementTable.elementName],
                        unitDescription = row[StationElementTable.unitDescription],
                        height = row[StationElementTable.height],
                        schedule = row[StationElementTable.schedule]
                    )
                }
        }
    }
}
