package cz.cvut.service


import cz.cvut.model.measurement.MeasurementLatest
import cz.cvut.model.station.Station
import cz.cvut.model.station.isActive
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.station.StationRepository
import cz.cvut.repository.stationElement.StationElementRepository
import kotlin.math.sqrt

class StationService(private val stationRepository: StationRepository, private val measurementService: MeasurementService, private val stationElementRepository: StationElementRepository, private val measurementRepository: MeasurementRepository) {


    fun getAllStations(active: Boolean? = null): List<Station> {
        val stations =  stationRepository.getStationsFiltered(active)
        return stations
    }

    fun getStationById(stationId: String): Station? {
        val stationElements = stationElementRepository.getElementsForStation(stationId)
        val station = stationRepository.getStationById(stationId)
        if(station != null && station.isActive() && station.stationElements.isNotEmpty()) {
            val measurements: MutableList<MeasurementLatest> = mutableListOf()
            stationElements.forEach { element ->
                measurementRepository.getLatestMeasurement(element, stationId)?.let { measurements.add(it) }
            }
            station.stationLatestMeasurements = measurements
        }
        return station
    }

    fun getClosestStations(latitude: Double, longitude: Double, count: Int): List<Station> {
        val stations = stationRepository.getStationsList()
        if(count == 1) {
            val activeStations = stations.filter { station: Station -> station.isActive()  }
            val closestActive =  activeStations.sortedBy {
                calculateApproximateDistance(latitude, longitude, it.latitude, it.longitude)
            }.take(1)

            val stationElements = stationElementRepository.getElementsForStation(closestActive[0].stationId)
            val measurements: MutableList<MeasurementLatest> = mutableListOf()

            stationElements.forEach { element ->
                measurementRepository.getLatestMeasurement(element, closestActive[0].stationId)?.let { measurements.add(it) }
            }

            closestActive[0].stationLatestMeasurements = measurements
            return closestActive
        }
        val closestStations = stations.sortedBy {
            calculateApproximateDistance(latitude, longitude, it.latitude, it.longitude)
        }.take(count)

        closestStations.forEach { station ->
            if (station.isActive() && station.stationElements.isNotEmpty()) {
                val stationElements = stationElementRepository.getElementsForStation(station.stationId)
                val measurements: MutableList<MeasurementLatest> = mutableListOf()

                stationElements.forEach { element ->
                    measurementRepository.getLatestMeasurement(element, station.stationId)?.let { measurements.add(it) }
                }

                station.stationLatestMeasurements = measurements
            }
        }

        return closestStations
    }


    fun exists(stationId: String): Boolean {
        return getStationById(stationId) != null
    }


    // Approximate distance calculation using Pythagorean theorem (for short distances)
    private fun calculateApproximateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val latDiff = lat2 - lat1
        val lonDiff = lon2 - lon1
        return sqrt(latDiff * latDiff + lonDiff * lonDiff)
    }
}
