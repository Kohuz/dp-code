package cz.cvut.service

import cz.cvut.repository.measurment.MeasurementRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class MeasurementService(private val repository: MeasurementRepository) {
    private val BASE_URL = "https://opendata.chmi.cz/meteorology/climate/historical/data/daily/dly-"
    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    suspend fun processJsonAndInsert(stationId: String, csvFilePath: String) {
        val url = "$BASE_URL$stationId.json"

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

        transaction {
            val sql = """
            COPY measurement2 (station_id, element, vtype, date_time, value, flag, quality)
            FROM '${csvFilePath}'
            WITH (FORMAT csv, HEADER true, DELIMITER ',');
        """.trimIndent()
            exec(sql)
        }
        }
        catch (e: Exception) {
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
