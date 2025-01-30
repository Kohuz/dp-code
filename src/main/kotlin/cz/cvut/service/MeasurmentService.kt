package cz.cvut.service

import cz.cvut.repository.measurment.MeasurementRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MeasurementService(private val repository: MeasurementRepository) {
    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    suspend fun processHistoricalDailyJsonAndInsert(stationId: String, csvFilePath: String) {
        val HISTORICAL_DAILY_BASE_URL = "https://opendata.chmi.cz/meteorology/climate/historical/data/daily/dly-"
        val url = "$HISTORICAL_DAILY_BASE_URL$stationId.json"

        try {
            val response: HttpResponse = client.get(url)
            val rawData = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(rawData).jsonObject


            val valuesArray = jsonObject["data"]
                ?.jsonObject?.get("data")
                ?.jsonObject?.get("values")
                ?.jsonArray ?: error("Invalid JSON structure")

            val header = "STATION,ELEMENT,VTYPE,DT,VAL,FLAG,QUALITY\n"

            val csvData = buildString {
                append(header)
                for (entry in valuesArray) {
                    val row = entry.jsonArray
                    val station = row[0].jsonPrimitive.content
                    val element = row[1].jsonPrimitive.content
                    val vtype = row[2].jsonPrimitive.content
                    val dt = row[3].jsonPrimitive.content
                    val valCol = row[4].jsonPrimitive.contentOrNull ?: ""
                    val flag = row[5].jsonPrimitive.contentOrNull ?: ""
                    val quality = row[6].jsonPrimitive.double

                    append("$station,$element,$vtype,$dt,$valCol,$flag,$quality\n")
                }
            }


            File(csvFilePath).writeText(csvData)

            repository.saveHistoricalDaily(csvFilePath)
        } catch (e: Exception) {
            println("Error fetching or saving measurements for station $stationId: ${e.message}")
        }
    }
    suspend fun processHistoricalMonthlyJsonAndInsert(stationId: String, csvFilePath: String) {
        val HISTORICAL_MONTHLY_BASE_URL = "https://opendata.chmi.cz/meteorology/climate/historical/data/monthly/mly-"
        val url = "$HISTORICAL_MONTHLY_BASE_URL$stationId.json"

        try {
            val response: HttpResponse = client.get(url)
            val rawData = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(rawData).jsonObject

            val valuesArray = jsonObject["data"]
                ?.jsonObject?.get("data")
                ?.jsonObject?.get("values")
                ?.jsonArray ?: error("Invalid JSON structure")

            val header = "STATION,ELEMENT,YEAR,MONTH,TIMEFUNCTION,MDFUNCTION,VALUE,FLAG_REPEAT,FLAG_INTERRUPTED\n"

            val csvData = buildString {
                append(header)
                for (entry in valuesArray) {
                    val row = entry.jsonArray
                    val station = row[0].jsonPrimitive.content
                    val element = row[1].jsonPrimitive.content
                    val year = row[2].jsonPrimitive.content
                    val month = row[3].jsonPrimitive.content
                    val timeFunction = row[4].jsonPrimitive.content
                    val mdFunction = row[5].jsonPrimitive.content
                    val value = row[6].jsonPrimitive.contentOrNull ?: ""
                    val flagRepeat = row[7].jsonPrimitive.contentOrNull ?: ""
                    val flagInterrupted = row[8].jsonPrimitive.contentOrNull ?: ""

                    append("$station,$element,$year,$month,$timeFunction,$mdFunction,$value,$flagRepeat,$flagInterrupted\n")
                }
            }

            File(csvFilePath).writeText(csvData)

            repository.saveHistoricalMonthly(csvFilePath)
        } catch (e: Exception) {
            println("Error fetching or saving monthly measurements for station $stationId: ${e.message}")
        }
    }

    suspend fun processHistoricalYearlyJsonAndInsert(stationId: String, csvFilePath: String) {
        val HISTORICAL_YEARLY_BASE_URL = "https://opendata.chmi.cz/meteorology/climate/historical/data/yearly/roky-"

        val url = "$HISTORICAL_YEARLY_BASE_URL$stationId.json"

        try {
            val response: HttpResponse = client.get(url)
            val rawData = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(rawData).jsonObject

            val valuesArray = jsonObject["data"]
                ?.jsonObject?.get("data")
                ?.jsonObject?.get("values")
                ?.jsonArray ?: error("Invalid JSON structure")

            val header = "STATION,ELEMENT,YEAR,TIMEFUNCTION,MDFUNCTION,VALUE,FLAG_REPEAT,FLAG_INTERRUPTED\n"

            val csvData = buildString {
                append(header)
                for (entry in valuesArray) {
                    val row = entry.jsonArray
                    val station = row[0].jsonPrimitive.content
                    val element = row[1].jsonPrimitive.content
                    val year = row[2].jsonPrimitive.content
                    val timeFunction = row[3].jsonPrimitive.content
                    val mdFunction = row[4].jsonPrimitive.content
                    val value = row[5].jsonPrimitive.contentOrNull ?: ""
                    val flagRepeat = row[6].jsonPrimitive.contentOrNull ?: ""
                    val flagInterrupted = row[7].jsonPrimitive.contentOrNull ?: ""

                    append("$station,$element,$year,$timeFunction,$mdFunction,$value,$flagRepeat,$flagInterrupted\n")
                }
            }

            File(csvFilePath).writeText(csvData)

            repository.saveHistoricalYearly(csvFilePath)
        } catch (e: Exception) {
            println("Error fetching or saving yearly measurements for station $stationId: ${e.message}")
        }
    }


//    suspend fun processRecentDailyJsonAndInsert(stationId: String, csvFilePath: String) {
//        val url = ""
//
//        try {
//            val response: HttpResponse = client.get(url)
//            val rawData = response.bodyAsText()
//            val jsonObject = Json.parseToJsonElement(rawData).jsonObject
//
//
//            val valuesArray = jsonObject["data"]
//                ?.jsonObject?.get("data")
//                ?.jsonObject?.get("values")
//                ?.jsonArray ?: error("Invalid JSON structure")
//
//            val header = "STATION,ELEMENT,VTYPE,DT,VAL,FLAG,QUALITY\n"
//
//            val csvData = buildString {
//                append(header)
//                for (entry in valuesArray) {
//                    val row = entry.jsonArray
//                    val station = row[0].jsonPrimitive.content
//                    val element = row[1].jsonPrimitive.content
//                    val vtype = row[2].jsonPrimitive.content
//                    val dt = row[3].jsonPrimitive.content
//                    val valCol = row[4].jsonPrimitive.contentOrNull ?: ""
//                    val flag = row[5].jsonPrimitive.contentOrNull ?: ""
//                    val quality = row[6].jsonPrimitive.double
//
//                    append("$station,$element,$vtype,$dt,$valCol,$flag,$quality\n")
//                }
//            }
//
//
//            File(csvFilePath).writeText(csvData)
//
//            repository.saveAllMeasurements(csvFilePath)
//        } catch (e: Exception) {
//            println("Error fetching or saving measurements for station $stationId: ${e.message}")
//        }
//    }


    fun getMeasurements(stationId: String, dateFrom: String, dateTo: String, element: String) {

        repository.getMeasurementsByStationandDateandElement(stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    fun getStatsLongTerm(date: String, stationId: String) {
        val parsedDate = LocalDate.parse(date)
//        val temperatureStats = repository.getTemperatureStats(date, stationId)
//        val precipitationStats = repository.getPrecipitationStats(date, stationId)
//        val windStats = repository.getWindStats(date, stationId)
//        val snowStats = repository.getSnowStats(date, stationId)

    }

    fun getStats(date: String, stationId: String) {

        repository.getStats(LocalDate.parse(date), stationId)

    }
}
