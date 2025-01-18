package cz.cvut.service

import cz.cvut.model.Station
import cz.cvut.repository.StationRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*

class StationService {
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

    fun getAllStations(filters: Map<String, String>): List<Station> {
        return StationRepository.filterStations(filters)
    }

    fun getStationById(stationId: String): Station? {
        return StationRepository.getStationById(stationId)
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
    private fun parseLocalDateTime(dateTimeString: String): LocalDateTime {
        val normalizedString = if (dateTimeString.endsWith("Z")) {
            dateTimeString.removeSuffix("Z")
        } else {
            dateTimeString
        }
        return LocalDateTime.parse(normalizedString)
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
        StationRepository.saveStations(deduplicatedStations)
    }
}
