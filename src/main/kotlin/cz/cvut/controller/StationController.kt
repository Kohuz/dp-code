package cz.cvut.controller

import io.ktor.server.resources.get
import io.ktor.server.routing.*
import cz.cvut.service.StationService
import io.ktor.http.*
import io.ktor.server.response.*
import cz.cvut.resources.*

fun Route.stationRoutes(stationService: StationService) {

    get<StationsResource> { params ->
        val stations = stationService.getAllStations(params.elevationMin, params.elevationMax, params.active, params.name)
        call.respond(stations)
    }

    get<StationByIdResource> { params ->
        if (params.id.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: id")
        }
        val station = stationService.getStationById(params.id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.id} not found")

        call.respond(station)
    }

    get<ClosestStationsResource> { params ->
        if (params.lat.isNaN() || params.long.isNaN()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Invalid latitude or longitude")
        }
        if (params.count <= 0) {
            return@get call.respond(HttpStatusCode.BadRequest, "Count must be greater than 0")
        }

        val stations = stationService.getClosestStations(params.lat, params.long, params.count)
        if (stations.isEmpty()) {
            return@get call.respond(HttpStatusCode.NotFound, "No stations found near (${params.lat}, ${params.long})")
        }

        call.respond(stations)
    }
}
