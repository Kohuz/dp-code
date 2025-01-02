package cz.cvut.controller

import cz.cvut.service.StationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Route.stationRoutes(stationService: StationService)  {
    route("/stations") {
        get {
            val filters = call.request.queryParameters.toMap()
            //val stations = stationService.getAllStations(filters)
            //call.respond(stations)
        }

        get("/{id}") {
            val stationId = call.parameters["id"] ?: return@get call.respondText(
                "Missing ID", status = HttpStatusCode.BadRequest
            )
            val station = stationService.getStationById(stationId)
                ?: return@get call.respondText(
                    "Station not found", status = HttpStatusCode.NotFound
                )
            call.respond(station)
        }
    }
}
