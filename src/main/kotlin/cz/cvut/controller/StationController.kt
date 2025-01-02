//package cz.cvut.controller
//
//import cz.cvut.service.StationService
//import io.ktor.application.*
//import io.ktor.response.*
//import io.ktor.request.*
//import io.ktor.routing.*
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import io.ktor.util.*
//import kotlin.text.get
//
//fun Application.configureRouting(stationService: StationService) {
//    routing {
//        route("/stations") {
//            // GET all stations with optional query parameters
//            get {
//                val filters = call.request.queryParameters.toMap()
//                val stations = stationService.getAllStations(filters)
//                call.respond(stations)
//            }
//
//            // GET station by ID
//            get("/{id}") {
//                val stationId = call.parameters["id"] ?: return@get call.respondText(
//                    "Missing ID", status = HttpStatusCode.BadRequest
//                )
//                val station = stationService.getStationById(stationId)
//                    ?: return@get call.respondText(
//                        "Station not found", status = HttpStatusCode.NotFound
//                    )
//                call.respond(station)
//            }
//        }
//    }
//}
