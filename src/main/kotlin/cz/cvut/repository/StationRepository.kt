package cz.cvut.repository

import cz.cvut.model.Station
import cz.cvut.model.StationElement

object StationRepository {
    private val stations = mutableListOf<Station>()
    private val stationElements = mutableListOf<StationElement>()

    fun saveStations(newStations: List<Station>) {
        stations.clear()
        stations.addAll(newStations)
    }

    fun getStationById(stationId: String): Station? {
        return stations.find { it.stationId == stationId }?.copy(
            stationElements = stationElements.filter { it.stationId == stationId }
        )
    }

    fun filterStations(filters: Map<String, String>): List<Station> {
        return stations.map { station ->
            station.copy(stationElements = stationElements.filter { it.stationId == station.stationId })
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
