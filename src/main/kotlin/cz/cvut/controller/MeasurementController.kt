package cz.cvut.controller

import cz.cvut.service.MeasurementService
import cz.cvut.utils.StationUtils.parseLocalDateTime
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Route.measuremenRoutes(measurmentService: MeasurementService) {
//    route("/measurements/{id}") {
//        get {
//            val dateFrom = call.request.queryParameters["dateFrom"]
//            val dateTo = call.request.queryParameters["dateTo"]
//            val element = call.request.queryParameters["element"]
//
//            val stationId = call.parameters["id"] ?: return@get call.respondText(
//                "Missing ID", status = HttpStatusCode.BadRequest
//            )
//
//            if (dateFrom == null || dateTo == null || element.isNullOrBlank()) {
//                return@get call.respond(
//                    HttpStatusCode.BadRequest,
//                    "Missing required query parameters: dateFrom, dateTo, and element"
//                )
//            }
//            val measurements = measurmentService.getMeasurements(stationId, dateFrom, dateTo, element)
//            call.respond(measurements)
//        }

        get("statsDay/{id}") {
            val date = call.parameters["date"] ?: return@get call.respondText(
                "Missing date", status = HttpStatusCode.BadRequest
            )
            val stationId = call.parameters["id"] ?: return@get call.respondText(
                "Missing ID", status = HttpStatusCode.BadRequest
            )
            val stats = measurmentService.getStats(date, stationId)
            call.respond(stats)
        }
        get("statsDayLongTerm/{id}") {
            val date = call.request.queryParameters["date"]
            val stationId = call.parameters["id"] ?: return@get call.respondText(
                "Missing ID", status = HttpStatusCode.BadRequest
            )

            if (date == null) {
                return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Missing required query parameters: date"
                )
            }
            val statsLongTerm = measurmentService.getStatsLongTerm(date, stationId)
            call.respond(statsLongTerm)


        }
//        get("actual/{id}") {
//            val stationId = call.parameters["id"] ?: return@get call.respondText(
//                "Missing ID", status = HttpStatusCode.BadRequest
//            )
//            val actualMeasurements = measurmentService.getActualMeasurements( stationId)
//            call.respond(actualMeasurements)
//        }
    //}
}