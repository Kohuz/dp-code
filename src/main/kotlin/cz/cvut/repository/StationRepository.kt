package cz.cvut.repository

import cz.cvut.database.table.StationTable
import cz.cvut.model.Station
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StationRepository {

    fun filterStations(filters: Map<String, String>): List<Station> {
        return transaction {
            val query = StationTable.selectAll()

            filters.forEach { (key, value) ->
                query.andWhere {
                    when (key) {
                        "minElevation" -> StationTable.elevation greaterEq value.toDouble()
                        "maxElevation" -> StationTable.elevation lessEq value.toDouble()
                        "startDateAfter" -> StationTable.startDate greaterEq LocalDateTime.parse(value)
                        "endDateBefore" -> StationTable.endDate lessEq LocalDateTime.parse(value)
                        else -> Op.TRUE
                    }
                }
            }

            query.map {
                Station(
                    stationId = it[StationTable.stationId],
                    code = it[StationTable.code],
                    startDate = it[StationTable.startDate],
                    endDate = it[StationTable.endDate],
                    location = it[StationTable.location],
                    longitude = it[StationTable.longitude],
                    latitude = it[StationTable.latitude],
                    elevation = it[StationTable.elevation]
                )
            }
        }
    }

    fun getStationById(stationId: String): Station? {
        return transaction {
            StationTable.selectAll().where { StationTable.stationId eq stationId }
                .map {
                    Station(
                        stationId = it[StationTable.stationId],
                        code = it[StationTable.code],
                        startDate = it[StationTable.startDate],
                        endDate = it[StationTable.endDate],
                        location = it[StationTable.location],
                        longitude = it[StationTable.longitude],
                        latitude = it[StationTable.latitude],
                        elevation = it[StationTable.elevation]
                    )
                }
                .singleOrNull()
        }
    }

    fun saveStations(stations: List<Station>) {
        transaction {
            stations.forEach { station ->
                StationTable.insert {
                    it[stationId] = station.stationId
                    it[code] = station.code
                    it[startDate] = station.startDate
                    it[endDate] = station.endDate
                    it[location] = station.location
                    it[longitude] = station.longitude
                    it[latitude] = station.latitude
                    it[elevation] = station.elevation
                }
            }
        }
    }

}
