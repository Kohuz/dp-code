package cz.cvut.controller

import io.ktor.server.resources.get
import io.ktor.server.routing.*
import cz.cvut.service.StationService
import io.ktor.http.*
import io.ktor.server.response.*
import cz.cvut.resources.*

fun Route.stationRoutes(stationService: StationService) {
    get<StationsResource> { params ->
        val stations = stationService.getAllStations(params.elevationMin, params.elevationMax, params.active)
        call.respond(stations)
    }

    get<StationByIdResource> { params ->
        val station = stationService.getStationById(params.id)
            ?: return@get call.respondText("Station not found", status = HttpStatusCode.NotFound)

        call.respond(station)
    }

    get<ClosestStationsResource> { params ->
        val stations = stationService.getClosestStations(params.lat, params.long, params.count)
        if (stations.isEmpty()) {
            call.respondText("No stations found", status = HttpStatusCode.NotFound)
        } else {
            call.respond(stations)
        }
    }
}
