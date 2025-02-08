package cz.cvut.model.measurement

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementLatest(
    val stationId: String,
    val element: String,
    val timestamp: LocalDate,
    val value: Double?,
    val flag: String? = null,
    val quality: Double?
)
