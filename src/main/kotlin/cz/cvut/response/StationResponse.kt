package cz.cvut.data

import kotlinx.serialization.Serializable

@Serializable
data class StationResponse(val id: String, val name: String, val latitude: Double, val longitude: Double)

@Serializable
data class ClosestStationsResponse(val stations: List<StationResponse>)
