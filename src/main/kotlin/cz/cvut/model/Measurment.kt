package cz.cvut.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StationElement(
    val observationType: String,
    val stationId: String,
    val beginDate: LocalDateTime,
    val endDate: LocalDateTime,
    val elementAbbreviation: String,
    val elementName: String,
    val unitDescription: String,
    val height: Double,
    val schedule: String
)
