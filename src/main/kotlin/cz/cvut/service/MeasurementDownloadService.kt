package cz.cvut.service

import cz.cvut.repository.measurement.MeasurementRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MeasurementDownloadService (private val repository: MeasurementRepository) {

    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }
    val allowedElements = setOf("TMA", "TMI", "T", "Fmax","F", "SNO", "SCE", "SVH")


    suspend fun processHistoricalDailyJsonAndInsert(stationId: String) {
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

            val csvData = buildString {
                append("station_id,element,vtype,date,value,flag,quality\n") // Ensure column names match the table
                for (entry in valuesArray) {
                    val row = entry.jsonArray
                    val station = row[0].jsonPrimitive.content
                    val element = row[1].jsonPrimitive.content
                    val vtype = row[2].jsonPrimitive.content
                    val dt = row[3].jsonPrimitive.content
                    val valCol = row[4].jsonPrimitive.contentOrNull ?: ""
                    val flag = row[5].jsonPrimitive.contentOrNull ?: ""
                    val quality = row[6].jsonPrimitive.doubleOrNull ?: 0.0

                    // Only append if the element is in the allowed set
                    if (element in allowedElements) {
                        append("$station,$element,$vtype,$dt,$valCol,$flag,$quality\n")
                    }
                }
            }

            // Save the filtered data
            repository.saveHistoricalDaily(csvData)

        } catch (e: Exception) {
            println("Error fetching or saving measurements for station $stationId: ${e.message}")
        }
    }

    suspend fun processHistoricalMonthlyJsonAndInsert(stationId: String) {
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

            val csvData = buildString {
                append("station_id,element,year,month,time_function,md_function,value,flag_repeat,flag_interrupted\n")
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

                    if (element in allowedElements) {
                        append("$station,$element,$year,$month,$timeFunction,$mdFunction,$value,$flagRepeat,$flagInterrupted\n")
                    }
                }
            }

            repository.saveHistoricalMonthly(csvData)

        } catch (e: Exception) {
            println("Error fetching or saving monthly measurements for station $stationId: ${e.message}")
        }
    }

    suspend fun processHistoricalYearlyJsonAndInsert(stationId: String) {
        val HISTORICAL_YEARLY_BASE_URL = "https://opendata.chmi.cz/meteorology/climate/historical/data/yearly/yrs-"
        val url = "$HISTORICAL_YEARLY_BASE_URL$stationId.json"

        try {
            val response: HttpResponse = client.get(url)
            val rawData = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(rawData).jsonObject

            val valuesArray = jsonObject["data"]
                ?.jsonObject?.get("data")
                ?.jsonObject?.get("values")
                ?.jsonArray ?: error("Invalid JSON structure")

            val csvData = buildString {
                append("station_id,element,year,time_function,md_function,value,flag_repeat,flag_interrupted\n")
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

                    if (element in allowedElements) {
                        append("$station,$element,$year,$timeFunction,$mdFunction,$value,$flagRepeat,$flagInterrupted\n")
                    }
                }
            }

            repository.saveHistoricalYearly(csvData)

        } catch (e: Exception) {
            println("Error fetching or saving yearly measurements for station $stationId: ${e.message}")
        }
    }


    suspend fun processRecentDailyJsonAndInsert(stationId: String) {
        val BASE_URL_RECENT = "https://opendata.chmi.cz/meteorology/climate/recent/data/daily/"

        val now = YearMonth.now()
        val currentYear = now.year
        val previousYear = currentYear - 1
        val currentMonth = now.monthValue

        val filePatterns = mutableListOf<String>()

        // Add the current month's file (located in the root)
        filePatterns.add("$BASE_URL_RECENT/dly-$stationId-$currentYear${"%02d".format(currentMonth)}.json")

        // Add past months (in their respective folders, but for last year)
        (1..12).forEach { month ->
            if (month != currentMonth) { // Skip the current month (already added above)
                val formattedMonth = "%02d".format(month)
                val fileUrl = "$BASE_URL_RECENT$formattedMonth/dly-$stationId-$previousYear$formattedMonth.json"
                filePatterns.add(fileUrl)
            }
        }

        for (url in filePatterns) {
            try {
                val response: HttpResponse = client.get(url)
                val rawData = response.bodyAsText()
                val jsonObject = Json.parseToJsonElement(rawData).jsonObject

                val valuesArray = jsonObject["data"]
                    ?.jsonObject?.get("data")
                    ?.jsonObject?.get("values")
                    ?.jsonArray ?: error("Invalid JSON structure")

                val csvData = buildString {
                    append("station_id,element,vtype,date,value,flag,quality\n")
                    for (entry in valuesArray) {
                        val row = entry.jsonArray
                        val station = row[0].jsonPrimitive.content
                        val element = row[1].jsonPrimitive.content
                        val vtype = row[2].jsonPrimitive.content
                        val dt = row[3].jsonPrimitive.content
                        val valCol = row[4].jsonPrimitive.contentOrNull ?: ""
                        val flag = row[5].jsonPrimitive.contentOrNull ?: ""
                        val quality = row[6].jsonPrimitive.doubleOrNull ?: 0.0

                        if (element in allowedElements) {
                            append("$station,$element,$vtype,$dt,$valCol,$flag,$quality\n")
                        }
                    }
                }

                repository.saveHistoricalDaily(csvData)

            } catch (e: Exception) {
                println("Error fetching or saving measurements for $url: ${e.message}")
            }
        }
    }







    suspend fun proccessLatestJsonAndInsert(forTodayOnly: Boolean = false) {
        val BASE_URL = "https://opendata.chmi.cz/meteorology/climate/now/data/"


            val response: HttpResponse = client.get(BASE_URL)
            val htmlContent = response.bodyAsText()

            // Extract all filenames
            val regex = Regex("""10m-[^"]+\.json""")
            val allFiles = regex.findAll(htmlContent)
                .map { it.value }

            // Get today's date in the desired format
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val todayDate = java.time.LocalDate.now().format(formatter)

            // Filter files based on todayDate if the flag is set
            val filteredFiles = if (forTodayOnly) {
                allFiles.filter { it.contains(todayDate) }
            } else {
                allFiles
            }

            // Group files by station ID
            val stationFiles = filteredFiles.distinct().groupBy { filename -> filename.split("-")[4] }


            for ((stationId, files) in stationFiles) {
                for (fileName in files) {
                    val url = "$BASE_URL$fileName" // Construct the correct URL

                    try {
                    val fileResponse: HttpResponse = client.get(url)
                    val rawData = fileResponse.bodyAsText()
                    val jsonObject = Json.parseToJsonElement(rawData).jsonObject

                    val valuesArray = jsonObject["data"]
                        ?.jsonObject?.get("data")
                        ?.jsonObject?.get("values")
                        ?.jsonArray ?: error("Invalid JSON structure")

                    val csvData = buildString {
                        append("station_id,element,timestamp,value,flag,quality\n")
                        for (entry in valuesArray) {
                            val row = entry.jsonArray
                            val station = row[0].jsonPrimitive.content
                            val element = row[1].jsonPrimitive.content
                            val timestamp = row[2].jsonPrimitive.content
                            val value = row[3].jsonPrimitive.contentOrNull ?: ""
                            val flag = row[4].jsonPrimitive.contentOrNull ?: ""
                            val quality = row[5].jsonPrimitive.doubleOrNull ?: 0.0

                            if (element in allowedElements) {
                                append("$station,$element,$timestamp,$value,$flag,$quality\n")
                            }

                        }
                    }

                    repository.saveLatestMeasurements(csvData)
                    }
                    catch (e: Exception) {
                        println("Error processing station files: ${e.message}")
                    }

                }

            }


    }

}