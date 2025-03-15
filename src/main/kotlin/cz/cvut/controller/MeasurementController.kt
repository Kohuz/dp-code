package cz.cvut.controller

import cz.cvut.resources.*
import cz.cvut.service.MeasurementService
import cz.cvut.service.StationService
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.measurementRoutes(measurementService: MeasurementService, stationService: StationService) {

    get<MeasurementResourceDaily> { params ->
        if (params.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(params.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.stationId} not found")
        }

        val dateFrom = params.dateFrom
        val dateTo = params.dateTo
        val element = params.element

        if (dateFrom.isBlank() || dateTo.isBlank() || element.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required query parameters: dateFrom, dateTo, and element"
            )
        }

        val measurements = measurementService.getMeasurementsDaily(params.stationId, dateFrom, dateTo, element)
        call.respond(measurements)
    }
    get<MeasurementResourceMonthly> { params ->
        if (params.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(params.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.stationId} not found")
        }

        val dateFrom = params.dateFrom
        val dateTo = params.dateTo
        val element = params.element

        if (dateFrom.isBlank() || dateTo.isBlank() || element.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required query parameters: dateFrom, dateTo, and element"
            )
        }

        val measurements = measurementService.getMeasurementsMonthly(params.stationId, dateFrom, dateTo, element)
        call.respond(measurements)
    }
    get<MeasurementResourceYearly> { params ->
        if (params.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(params.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${params.stationId} not found")
        }

        val dateFrom = params.dateFrom
        val dateTo = params.dateTo
        val element = params.element

        if (dateFrom.isBlank() || dateTo.isBlank() || element.isBlank()) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing required query parameters: dateFrom, dateTo, and element"
            )
        }

        val measurements = measurementService.getMeasurementsYearly(params.stationId, dateFrom, dateTo, element)
        call.respond(measurements)
    }

    get<MeasurementStatsDayLongTermResource> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(resource.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${resource.stationId} not found")
        }

        val statsLongTerm = measurementService.getStatsDayLongTerm(resource.stationId, resource.date)
        call.respond(statsLongTerm)
    }

    get<MeasurementStatsMonthLongTermResource> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(resource.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${resource.stationId} not found")
        }

        val statsLongTerm = measurementService.getStatsMonthLongTerm(resource.stationId, resource.date)
        call.respond(statsLongTerm)
    }

    get<MeasurementResourceDayAndMonth> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(resource.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${resource.stationId} not found")
        }

        val statsLongTerm = measurementService.getMeasurementsForDayAndMonth(resource.stationId, resource.date, resource.element)
        call.respond(statsLongTerm)
    }

    get<MeasurementResourceMonth> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(resource.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${resource.stationId} not found")
        }

        val statsLongTerm = measurementService.getMeasurementsForMonth(resource.stationId, resource.date, resource.element)
        call.respond(statsLongTerm)
    }



    get<MeasurementStatsDayResource> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }
        if (!stationService.exists(resource.stationId)) {
            return@get call.respond(HttpStatusCode.NotFound, "Station with ID ${resource.stationId} not found")
        }

        val statsLongTerm = measurementService.getStatsDay(resource.stationId, resource.date)
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
