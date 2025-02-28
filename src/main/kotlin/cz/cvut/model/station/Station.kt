package cz.cvut.model.station

import cz.cvut.model.measurement.MeasurementLatest
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
    val stationElements: List<StationElement> = emptyList(),
    var stationLatestMeasurements: List<MeasurementLatest> = emptyList()
)