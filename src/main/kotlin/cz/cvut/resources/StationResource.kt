package cz.cvut.resources

import io.ktor.resources.*

@Resource("/stations")
class StationsResource(val filters: Map<String, String>? = null)

@Resource("/stations/{id}")
class StationByIdResource(val id: String)

@Resource("/stations/closest")
class ClosestStationsResource(val lat: Double, val long: Double, val count: Int = 1)
