package cz.cvut.repository

import cz.cvut.model.Station
import cz.cvut.model.StationElement
import cz.cvut.model.StationEntity
import cz.cvut.model.StationElementEntity
import org.jetbrains.exposed.sql.transactions.transaction

object StationRepository {

    fun saveStations(newStations: List<Station>) {
        transaction {
            newStations.forEach { station ->
                StationEntity.new {
                    stationId = station.stationId
                    code = station.code
                    startDate = station.startDate
                    endDate = station.endDate
                    location = station.location
                    longitude = station.longitude
                    latitude = station.latitude
                    elevation = station.elevation
                }
            }
        }
    }

    fun getStationById(stationId: String): Station? {
        return transaction {
            StationEntity.find { cz.cvut.database.table.StationTable.stationId eq stationId }
                .firstOrNull()?.let { stationEntity ->
                    Station(
                        stationId = stationEntity.stationId,
                        code = stationEntity.code,
                        startDate = stationEntity.startDate,
                        endDate = stationEntity.endDate,
                        location = stationEntity.location,
                        longitude = stationEntity.longitude,
                        latitude = stationEntity.latitude,
                        elevation = stationEntity.elevation,
                        stationElements = stationEntity.stationElements.map { elementEntity ->
                            StationElement(
                                stationId = elementEntity.stationId,
                                observationType = elementEntity.observationType,
                                beginDate = elementEntity.beginDate,
                                endDate = elementEntity.endDate,
                                elementAbbreviation = elementEntity.elementAbbreviation,
                                elementName = elementEntity.elementName,
                                unitDescription = elementEntity.unitDescription,
                                height = elementEntity.height,
                                schedule = elementEntity.schedule
                            )
                        }
                    )
                }
        }
    }

    fun filterStations(filters: Map<String, String>): List<Station> {
        return transaction {
            StationEntity.all().map { stationEntity ->
                Station(
                    stationId = stationEntity.stationId,
                    code = stationEntity.code,
                    startDate = stationEntity.startDate,
                    endDate = stationEntity.endDate,
                    location = stationEntity.location,
                    longitude = stationEntity.longitude,
                    latitude = stationEntity.latitude,
                    elevation = stationEntity.elevation,
                    stationElements = stationEntity.stationElements.map { elementEntity ->
                        StationElement(
                            stationId = elementEntity.stationId,
                            observationType = elementEntity.observationType,
                            beginDate = elementEntity.beginDate,
                            endDate = elementEntity.endDate,
                            elementAbbreviation = elementEntity.elementAbbreviation,
                            elementName = elementEntity.elementName,
                            unitDescription = elementEntity.unitDescription,
                            height = elementEntity.height,
                            schedule = elementEntity.schedule
                        )
                    }
                )
            }.filter { station ->
                filters.all { (key, value) ->
                    when (key) {
                        "stationId" -> station.stationId == value
                        else -> true
                    }
                }
            }
        }
    }

    fun getStations(): List<Station> {
        return transaction {
            StationEntity.all().map { stationEntity ->
                Station(
                    stationId = stationEntity.stationId,
                    code = stationEntity.code,
                    startDate = stationEntity.startDate,
                    endDate = stationEntity.endDate,
                    location = stationEntity.location,
                    longitude = stationEntity.longitude,
                    latitude = stationEntity.latitude,
                    elevation = stationEntity.elevation,
                    stationElements = stationEntity.stationElements.map { elementEntity ->
                        StationElement(
                            stationId = elementEntity.stationId,
                            observationType = elementEntity.observationType,
                            beginDate = elementEntity.beginDate,
                            endDate = elementEntity.endDate,
                            elementAbbreviation = elementEntity.elementAbbreviation,
                            elementName = elementEntity.elementName,
                            unitDescription = elementEntity.unitDescription,
                            height = elementEntity.height,
                            schedule = elementEntity.schedule
                        )
                    }
                )
            }
        }
    }
    fun getStationsList(): List<Station> {
        return transaction {
            StationEntity.all().map { stationEntity ->
                Station(
                    stationId = stationEntity.stationId,
                    code = stationEntity.code,
                    startDate = stationEntity.startDate,
                    endDate = stationEntity.endDate,
                    location = stationEntity.location,
                    longitude = stationEntity.longitude,
                    latitude = stationEntity.latitude,
                    elevation = stationEntity.elevation,
                )
            }
        }
}
}
