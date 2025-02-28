package cz.cvut.service


import cz.cvut.model.station.Station
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.station.StationRepository
import kotlin.math.sqrt

class StationService(private val stationRepository: StationRepository, private val measurementService: MeasurementService) {


    fun getAllStations(active: Boolean? = null): List<Station> {
        val stations =  stationRepository.getStationsFiltered(active)
        stations.forEach{ station ->
            station.stationLatestMeasurements = measurementService.getActualMeasurements(station.stationId)
        }
        return stations
    }

    fun getStationById(stationId: String): Station? {
        return stationRepository.getStationById(stationId)
    }

    fun getClosestStations(latitude: Double, longitude: Double, count: Int): List<Station> {
        val stations = stationRepository.getStationsList();
        return stations.sortedBy {
            calculateApproximateDistance(latitude, longitude, it.latitude, it.longitude)
        }.take(count)
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
