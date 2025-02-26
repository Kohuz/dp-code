package cz.cvut.model.station

import cz.cvut.model.stationElement.StationElement
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Station(
    val stationId: String,
    val code: String,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime,
    val location: String,
    val longitude: Double,
    val latitude: Double,
    val elevation: Double,
    val stationElements: List<StationElement> = emptyList()
)

@Serializable
data class GeoJSONFeatureCollection(
    val type: String = "FeatureCollection",
    val features: List<GeoJSONFeature>
)

@Serializable
data class GeoJSONFeature(
    val type: String = "Feature",
    val geometry: GeoJSONPoint,
    val properties: Map<String, JsonElement>
)

@Serializable
data class GeoJSONPoint(
    val type: String = "Point",
    val coordinates: List<Double>
)