package cz.cvut.controller

import cz.cvut.resources.MeasurementResource
import cz.cvut.service.MeasurementService
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.resources.*

fun Route.measurementRoutes(measurementService: MeasurementService) {
    get<MeasurementResource> { resource ->
        val dateFrom = call.request.queryParameters["dateFrom"]
        val dateTo = call.request.queryParameters["dateTo"]
        val element = call.request.queryParameters["element"]
        val resolution = call.request.queryParameters["resolution"]

        if (dateFrom == null || dateTo == null || element.isNullOrBlank() || resolution.isNullOrBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required query parameters: dateFrom, dateTo, resolution and element"
            )
        }
        val measurements = measurementService.getMeasurements(resource.stationId, dateFrom, dateTo, element,resolution)
        call.respond(measurements)
    }


//
//    get<MeasurementResource.AllTimeRecordsStation> { resource ->
//        val stationId = resource.parent.stationId
//        if (stationId.isBlank()) {
//            return@get call.respond(
//                HttpStatusCode.BadRequest,
//                "Missing required parameter: stationId"
//            )
//        }
//        val records = measurementService.getAllTimeRecordsStation(stationId)
//        call.respond(records)
//    }

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
