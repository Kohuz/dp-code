package cz.cvut.service

import cz.cvut.model.stationElement.ElementCodelist
import cz.cvut.model.stationElement.StationElement
import cz.cvut.repository.stationElement.StationElementRepository
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.*

class StationElementService {
    private val jsonConfig = Json { ignoreUnknownKeys = true }

    suspend fun processAndSaveStationElements() {
        val stationElements = downloadStationElements()
        val deduplicatedStationElements = deduplicateStationElements(stationElements)
        StationElementRepository.saveStationElements(deduplicatedStationElements)
        saveUniqueElements(deduplicatedStationElements)
    }


    suspend fun downloadElementCodelist() {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        val API_URL = "https://opendata.chmi.cz/meteorology/climate/now/metadata/meta2-${currentDate.year}${"%02d".format(currentDate.monthNumber)}${"%02d".format(currentDate.dayOfMonth)}.json"

        val client = HttpClient {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
        }

        val response: HttpResponse = client.get(API_URL)
        val rawData = response.bodyAsText()
        client.close()

        return parseAndSaveElementCodelist(rawData)
    }

    private suspend fun downloadStationElements(): List<StationElement> {
        val API_URL = "https://opendata.chmi.cz/meteorology/climate/historical/metadata/meta2.json"

        val client = HttpClient {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
        }

        val response: HttpResponse = client.get(API_URL)
        val rawData = response.bodyAsText()
        client.close()

        return parseStationElements(rawData)
    }

    private fun parseStationElements(json: String): List<StationElement> {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val valuesArray = jsonObject["data"]?.jsonObject?.get("data")?.jsonObject?.get("values")?.jsonArray ?: return emptyList()

        return valuesArray.map { recordArray ->
            val record = recordArray.jsonArray
            StationElement(
                observationType = record[0].jsonPrimitive.content,
                stationId = record[1].jsonPrimitive.content,
                beginDate = parseLocalDateTime(record[2].jsonPrimitive.content),
                endDate = parseLocalDateTime(record[3].jsonPrimitive.content),
                elementAbbreviation = record[4].jsonPrimitive.content,
                elementName = record[5].jsonPrimitive.content,
                unitDescription = record[6].jsonPrimitive.content,
                height = record[7].jsonPrimitive.doubleOrNull ?: -1.0,
                schedule = record[8].jsonPrimitive.content
            )
        }
    }

    private fun parseAndSaveElementCodelist(json: String) {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val valuesArray = jsonObject["data"]?.jsonObject?.get("values")?.jsonArray ?: return

        val uniqueCodelist = valuesArray.map { recordArray ->
            val record = recordArray.jsonArray
            ElementCodelist(
                abbreviation = record[2].jsonPrimitive.content,
                name = record[3].jsonPrimitive.content,
                unit = record[4].jsonPrimitive.content
            )
        }.distinctBy { it.abbreviation }

        StationElementRepository.saveUniqueElements(uniqueCodelist)
    }


    private fun deduplicateStationElements(elements: List<StationElement>): List<StationElement> {
        return elements
            .groupBy { Triple(it.stationId, it.elementAbbreviation, it.observationType) }
            .mapValues { (_, records) -> records.maxByOrNull { it.endDate } }
            .values
            .filterNotNull()
    }

    private fun saveUniqueElements(elements: List<StationElement>) {
        val uniqueElements = elements.map {
            ElementCodelist(it.elementAbbreviation, it.elementName, it.unitDescription)
        }.distinctBy { Triple(it.abbreviation, it.name, it.unit) }

        StationElementRepository.saveUniqueElements(uniqueElements)
    }


    private fun parseLocalDateTime(dateTimeString: String): LocalDateTime {
        val normalizedString = if (dateTimeString.endsWith("Z")) {
            dateTimeString.removeSuffix("Z")
        } else {
            dateTimeString
        }
        return LocalDateTime.parse(normalizedString)
    }
}
