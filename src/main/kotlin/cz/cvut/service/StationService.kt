package cz.cvut.service

import cz.cvut.database.table.StationTable
import cz.cvut.model.Station
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object StationService {
    private const val API_URL = "https://opendata.chmi.cz/meteorology/climate/historical/metadata/meta1.json"

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

    private fun parseStations(json: String): List<Station> {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val dataArray = jsonObject["data"]?.jsonObject?.get("data")?.jsonObject
        val values = dataArray?.get("values")?.jsonArray ?: return emptyList()

        return values.map { stationArray ->
            val stationData = stationArray.jsonArray
            Station(
                stationId = stationData[0].jsonPrimitive.content,
                code = stationData[1].jsonPrimitive.content,
                startDate = ZonedDateTime.parse(stationData[2].jsonPrimitive.content),
                endDate = ZonedDateTime.parse(stationData[3].jsonPrimitive.content),
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

    private fun saveStations(stations: List<Station>) {
        transaction {
            stations.forEach { station ->
                StationTable.insert {
                    it[stationId] = station.stationId
                    it[code] = station.code
                    it[startDate] = Timestamp.valueOf(station.startDate?.toLocalDateTime()).toLocalDateTime()
                    it[endDate] = Timestamp.valueOf(station.endDate.toLocalDateTime()).toLocalDateTime()
                    it[location] = station.location
                    it[longitude] = station.longitude
                    it[latitude] = station.latitude
                    it[elevation] = station.elevation
                }
            }
        }
    }

    suspend fun processAndSaveStations() {
        val stations = downloadStations()
        val deduplicatedStations = deduplicateStations(stations)
        saveStations(deduplicatedStations)
    }
}
