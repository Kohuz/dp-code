package cz.cvut.resources

import io.ktor.resources.*

@Resource("/stations")
data class StationsResource(
    val elevationMin: Double? = null,
    val elevationMax: Double? = null,
    val active: Boolean? = null,
    val name: String? = null
)

@Resource("/stations/{id}")
class StationByIdResource(val id: String)

@Resource("/stations/closest")
class ClosestStationsResource(val lat: Double, val long: Double, val count: Int = 1)
