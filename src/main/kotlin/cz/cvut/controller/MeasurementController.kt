package cz.cvut.controller

import cz.cvut.resources.MeasurementResource
import cz.cvut.service.MeasurementService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.routing.get

fun Route.measurementRoutes(measurementService: MeasurementService) {
    get<MeasurementResource> { resource ->
        val dateFrom = call.request.queryParameters["dateFrom"]
        val dateTo = call.request.queryParameters["dateTo"]
        val element = call.request.queryParameters["element"]

        if (dateFrom == null || dateTo == null || element.isNullOrBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required query parameters: dateFrom, dateTo, and element"
            )
        }
        val measurements = measurementService.getMeasurements(resource.stationId, dateFrom, dateTo, element)
        call.respond(measurements)
    }

    get<MeasurementResource.StatsDay> { resource ->
        if (resource.date.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required parameter: date"
            )
        }
        val stats = measurementService.getStats(resource.date, resource.parent.stationId)
        call.respond(stats)
    }

    get<MeasurementResource.StatsDayLongTerm> { resource ->
        if (resource.date.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required parameter: date"
            )
        }
        val statsLongTerm = measurementService.getStatsLongTerm(resource.date, resource.parent.stationId)
        call.respond(statsLongTerm)
    }

    get<MeasurementResource.Actual> { resource ->
        val stationId = resource.parent.stationId
        if (stationId.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required parameter: stationId"
            )
        }
        val actualMeasurements = measurementService.getActualMeasurements(stationId)
        call.respond(actualMeasurements)
    }
    get<MeasurementResource.Recent> { resource ->
        val stationId = resource.parent.stationId
        if (stationId.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required parameter: stationId"
            )
        }
        val actualMeasurements = measurementService.getRecentMeasurements(stationId)
        call.respond(actualMeasurements)
    }
}
