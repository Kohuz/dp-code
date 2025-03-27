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
import org.locationtech.proj4j.*


class StationDownloadService(private val stationRepository: StationRepository) {
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

    private fun parseStations(json: String): List<Station> {
        val jsonObject = Json.parseToJsonElement(json).jsonObject
        val dataArray = jsonObject["data"]?.jsonObject?.get("data")?.jsonObject
        val values = dataArray?.get("values")?.jsonArray ?: return emptyList()

        return values.map { stationArray ->
            val stationData = stationArray.jsonArray
            Station(
                stationId = stationData[0].jsonPrimitive.content,
                code = stationData[1].jsonPrimitive.content,
                startDate = parseLocalDateTime(stationData[2].jsonPrimitive.content),
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
            .mapValues { (_, records) ->
                val earliestStartDate = records.minByOrNull { it.startDate }!!.startDate
                val latestEndDate = records.maxByOrNull { it.endDate }!!.endDate
                records.first().copy(
                    startDate = earliestStartDate,
                    endDate = latestEndDate
                )
            }
            .values
            .toList()
    }

    suspend fun processAndSaveStations() {
        val stations = downloadStations()


//        val transformedStations = stations.map { station ->
//            val (latitude, longitude) = transformSJTSKtoWGS84(station.latitude, station.longitude)
//            station.copy(latitude = latitude, longitude = longitude)
//        }
        val deduplicatedStations = deduplicateStations(stations)
        val stationsWithoutReykjavik = deduplicatedStations.filter { it.code != "ZIS04030" }
        stationRepository.saveStations(stationsWithoutReykjavik)
    }

    fun transformSJTSKtoWGS84(x: Double, y: Double): Pair<Double, Double> {
        val crsFactory = CRSFactory()

        val destCrs = crsFactory.createFromParameters(
            "krovak","+proj=krovak"
        )
        val srcCrs = crsFactory.createFromParameters(
            "WGS84",
            "+proj=longlat +datum=WGS84 +no_defs"
        )


        val transform = CoordinateTransformFactory().createTransform(srcCrs, destCrs)

        val srcCoord = ProjCoordinate(x, y)
        val destCoord = ProjCoordinate()
        transform.transform(srcCoord, destCoord)
        return Pair(destCoord.x, destCoord.y)
    }

}


