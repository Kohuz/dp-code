package cz.cvut.repository.stationElement

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.ElementCodelistTable
import cz.cvut.model.measurement.ElementCodelistEntity
import cz.cvut.model.measurement.toElementCodelist
import cz.cvut.model.stationElement.ElementCodelist
import cz.cvut.model.stationElement.StationElement
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

class StationElementRepository {
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

    fun saveUniqueElements(elements: List<ElementCodelist>) {
        transaction {
            elements.forEach { element ->
                    ElementCodelistTable.insertIgnore {
                        it[abbreviation] = element.abbreviation
                        it[name] = element.name
                        it[unit] = element.unit
                    }
            }
        }
    }

    fun getElementsCodelist(): List<ElementCodelist> {
        return transaction {
            ElementCodelistEntity.all().map { it.toElementCodelist() }
        }
    }

    fun getElementsForStation(stationId: String): List<String> {
        return transaction {
            StationElementTable
                .select(StationElementTable.elementAbbreviation) // Select only the elementName column
                .where { StationElementTable.stationId eq stationId } // Filter by stationId
                .distinctBy { it[StationElementTable.elementAbbreviation] } // Ensure distinct element names
                .map { it[StationElementTable.elementAbbreviation] } // Map to a list of element names
        }
    }

}
