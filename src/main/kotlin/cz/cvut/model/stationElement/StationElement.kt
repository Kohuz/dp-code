package cz.cvut.model.stationElement

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StationElement(
    val stationId: String,
    val observationType: String,
    val beginDate: LocalDateTime?,
    val endDate: LocalDateTime,
    val elementAbbreviation: String,
    val elementName: String,
    val unitDescription: String,
    val height: Double?,
    val schedule: String
)
