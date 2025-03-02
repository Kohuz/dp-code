package cz.cvut.model.station

import cz.cvut.model.measurement.MeasurementLatest
import cz.cvut.model.stationElement.StationElement
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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

fun Station.isActive(): Boolean {
    val activeEndDate = LocalDateTime.parse("3999-12-31T23:59:00.000000")
        .toInstant(TimeZone.UTC)
    val stationEndDate = this.endDate.toInstant(TimeZone.UTC)
    return stationEndDate == activeEndDate
}