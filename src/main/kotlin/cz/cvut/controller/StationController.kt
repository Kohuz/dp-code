package cz.cvut.controller

import cz.cvut.service.StationService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Route.stationRoutes(stationService: StationService)  {
    route("/stations") {
        get {
            val filters = call.request.queryParameters.toMap()
            val stations = stationService.getAllStations(filters)
            call.respond(stations)
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

        get("/closest") {
            val lat = call.request.queryParameters["lat"]?.toDoubleOrNull()
            val long = call.request.queryParameters["long"]?.toDoubleOrNull()
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 1

            if (lat == null || long == null) {
                call.respondText("Missing or invalid lat/long query parameters", status = HttpStatusCode.BadRequest)
                return@get
            }

            val stations = stationService.getClosestStations(lat, long, count)
            if (stations.isEmpty()) {
                call.respondText("No stations found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(stations)
            }
        }

//        get("/records/{") {
//            val stationId = call.parameters["id"] ?: return@get call.respondText(
//                "Missing ID", status = HttpStatusCode.BadRequest
//            )
//            val station = stationService.getRecordsForStation(stationId)
//                ?: return@get call.respondText(
//                    "Station not found", status = HttpStatusCode.NotFound
//                )
//            call.respond(station)
//        }
    }
}
