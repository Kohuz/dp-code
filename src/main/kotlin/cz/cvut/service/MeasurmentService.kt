package cz.cvut.service

import cz.cvut.repository.measurment.MeasurementRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class MeasurementService(private val repository: MeasurementRepository) {
    private val BASE_URL = "https://opendata.chmi.cz/meteorology/climate/historical/data/daily/"
    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    suspend fun fetchAndSaveMeasurementsForMultipleStations(stationIds: List<String>) {
        for (stationId in stationIds) {
            fetchAndSaveMeasurements(stationId)
        }
    }

    suspend fun fetchAndSaveMeasurements(stationId: String) {
        val fileName = "dly-$stationId.json"
        val url = "$BASE_URL$fileName"

        try {
            val response: HttpResponse = client.get(url)
            val rawData = response.bodyAsText()

            val measurements = parseMeasurements(rawData)

            repository.saveAllMeasurements(measurements)

            println("Successfully fetched and saved measurements for station $stationId")
        } catch (e: Exception) {
            println("Error fetching or saving measurements for station $stationId: ${e.message}")
        }
    }

    private fun parseMeasurements(json: String): List<List<String>> {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val values = jsonObject["data"]?.jsonObject
            ?.get("data")?.jsonObject?.get("values")?.jsonArray ?: return emptyList()

        return values.map { array ->
            array.jsonArray.map { it.jsonPrimitive.contentOrNull ?: "" }
        }
    }
}
