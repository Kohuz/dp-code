package cz.cvut.controller

import cz.cvut.resources.*
import cz.cvut.service.MeasurementService
import cz.cvut.service.StationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.measurementRoutes(measurementService: MeasurementService, stationService: StationService) {

    get<MeasurementResource> { params ->
        if (params.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(params.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.stationId} not found")
        }

        val dateFrom = call.request.queryParameters["dateFrom"]
        val dateTo = call.request.queryParameters["dateTo"]
        val element = call.request.queryParameters["element"]
        val resolution = call.request.queryParameters["resolution"]

        if (dateFrom.isNullOrBlank() || dateTo.isNullOrBlank() || element.isNullOrBlank() || resolution.isNullOrBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required query parameters: dateFrom, dateTo, resolution, and element"
            )
        }

        val measurements = measurementService.getMeasurements(params.stationId, dateFrom, dateTo, element, resolution)
        call.respond(measurements)
    }

    get<MeasurementStatsDayLongTermResource> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(resource.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${resource.stationId} not found")
        }

        val statsLongTerm = measurementService.getStatsLongTerm(resource.date, resource.stationId)
        call.respond(statsLongTerm)
    }

    get<MeasurementActualResource> { params ->
        if (params.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(params.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.stationId} not found")
        }

        val actualMeasurements = measurementService.getActualMeasurements(params.stationId)
        call.respond(actualMeasurements)
    }

    get<MeasurementRecentResource> { params ->
        if (params.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(params.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.stationId} not found")
        }

        val recentMeasurements = measurementService.getRecentMeasurements(params.stationId)
        call.respond(recentMeasurements)
    }
}
