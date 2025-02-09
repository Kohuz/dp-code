package cz.cvut.model.record

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class StationRecord(
    val stationId: String,
    val element: String,
    val recordType: String,
    val value: Double?,
    val recordDate: LocalDate
)
