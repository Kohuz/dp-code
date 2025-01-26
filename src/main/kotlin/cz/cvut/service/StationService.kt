package cz.cvut.service

import cz.cvut.model.station.Station
import cz.cvut.repository.station.StationRepository
import cz.cvut.utils.StationUtils.parseLocalDateTime
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import kotlin.math.sqrt

class StationService(private val stationRepository: StationRepository) {
    private val API_URL = "https://opendata.chmi.cz/meteorology/climate/historical/metadata/meta1.json"

    private val jsonConfig = Json { ignoreUnknownKeys = true }

    private suspend fun downloadStations(): List<Station> {
        val client = HttpClient {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
        }
        val response: HttpResponse = client.get(API_URL)
        val rawData = response.bodyAsText()
        client.close()

        return parseStations(rawData)
    }

    fun getAllStations(filters: Map<String, List<String>> = emptyMap()): List<Station> {
        return stationRepository.getStationsList()
    }

    fun getStationById(stationId: String): Station? {
        return stationRepository.getStationById(stationId)
    }

    fun getClosestStations(latitude: Double, longitude: Double, count: Int): List<Station> {
        val stations = stationRepository.getStationsList()

        return stations.sortedBy {
            calculateApproximateDistance(latitude, longitude, it.latitude, it.longitude)
        }.take(count)
    }

    private fun parseStations(json: String): List<Station> {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val dataArray = jsonObject["data"]?.jsonObject?.get("data")?.jsonObject
        val values = dataArray?.get("values")?.jsonArray ?: return emptyList()

        return values.map { stationArray ->
            val stationData = stationArray.jsonArray
            Station(
                stationId = stationData[0].jsonPrimitive.content,
                code = stationData[1].jsonPrimitive.content,
                startDate = stationData[2].jsonPrimitive.content.takeIf { it.isNotEmpty() }?.let { parseLocalDateTime(it) },
                endDate = parseLocalDateTime(stationData[3].jsonPrimitive.content),
                location = stationData[4].jsonPrimitive.content,
                longitude = stationData[5].jsonPrimitive.double,
                latitude = stationData[6].jsonPrimitive.doubleOrNull ?: -1.0,
                elevation = stationData[7].jsonPrimitive.doubleOrNull ?: -1.0
            )
        }
    }

    private fun deduplicateStations(stations: List<Station>): List<Station> {
        return stations
            .groupBy { it.stationId }
            .mapValues { (_, records) -> records.maxByOrNull { it.endDate } }
            .values
            .filterNotNull()
    }

    suspend fun processAndSaveStations() {
        val stations = downloadStations()
        val deduplicatedStations = deduplicateStations(stations)
        stationRepository.saveStations(deduplicatedStations)
    }

    // Approximate distance calculation using Pythagorean theorem (for short distances)
    private fun calculateApproximateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val latDiff = lat2 - lat1
        val lonDiff = lon2 - lon1
        return sqrt(latDiff * latDiff + lonDiff * lonDiff)
    }
}
