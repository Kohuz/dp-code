package cz.cvut.model.measurment

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementDaily(
    val stationId: String,
    val element: String,
    val vtype: String,
    val date: LocalDate,
    val value: Double?,
    val flag: String? = null,
    val quality: Double?,
    val schedule: String? = null,
)
