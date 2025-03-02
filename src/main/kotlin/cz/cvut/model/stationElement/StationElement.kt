package cz.cvut.model.stationElement

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID

@Serializable
data class StationElement(
    val stationId: String,
    val beginDate: LocalDateTime?,
    val endDate: LocalDateTime,
    val elementAbbreviation: String,
    val elementName: String,
    val unitDescription: String,
    val height: Double?,
    val schedule: String
)
