package cz.cvut.repository.station

import cz.cvut.database.table.StationTable

import cz.cvut.model.station.Station
import cz.cvut.model.station.StationEntity
import cz.cvut.model.station.toStation
import cz.cvut.model.station.toStationEntity
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class StationRepository {

    fun saveStations(newStations: List<Station>) {
        transaction {
            newStations.forEach { it.toStationEntity() }
        }
    }

    fun getStationById(stationId: String): Station? {
        return transaction {
            StationEntity.find { StationTable.stationId eq stationId }
                .firstOrNull()?.toStation()
        }
    }

//    fun getStations(filters: Map<String, String>): List<Station> {
//        return transaction {
//            StationEntity.find {
//                (filters["elevationMin"]?.toDoubleOrNull()?.let { StationTable.elevation greaterEq it } ?: Op.TRUE) and
//                        (filters["elevationMax"]?.toDoubleOrNull()?.let { StationTable.elevation lessEq it } ?: Op.TRUE) and
//                        (if (filters["active"].toBooleanStrictOrNull() == true) {
//                            StationTable.endDate eq LocalDateTime.parse("3999-12-31T23:59:00.000000")
//                        } else Op.TRUE)
//            }.map { it.toStation() }
//        }
//    }

    fun getStationsFiltered(filters: Map<String, String>): List<Station> {
                return transaction {
            StationEntity.find {
                (filters["elevationMin"]?.toDoubleOrNull()?.let { StationTable.elevation greaterEq it } ?: Op.TRUE) and
                        (filters["elevationMax"]?.toDoubleOrNull()?.let { StationTable.elevation lessEq it } ?: Op.TRUE) and
                        (if (filters["active"]?.toBooleanStrictOrNull() == true) {
                            StationTable.endDate eq LocalDateTime.parse("3999-12-31T23:59:00.000000")
                        } else Op.TRUE)
            }.map { it.toStation() }
        }
    }

    fun getStationsList(): List<Station> {
        return transaction {
            StationEntity.all().map { it.toStation() }
        }
    }


}
