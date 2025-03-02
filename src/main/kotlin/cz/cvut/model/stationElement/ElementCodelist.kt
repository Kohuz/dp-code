package cz.cvut.model.stationElement

import kotlinx.serialization.Serializable

@Serializable
data class ElementCodelist(
    val abbreviation: String,
    val name: String,
    val unit: String
)