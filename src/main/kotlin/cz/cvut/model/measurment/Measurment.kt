package cz.cvut.model.measurment

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Measurement(
    val stationId: String,
    val element: String,
    val dateTime: LocalDateTime,
    val value: Double,
    val flag: String? = null,
    val quality: Double,
    val observationType: String? = null,
    val schedule: String? = null,
)
