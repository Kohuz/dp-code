package cz.cvut.controller


import cz.cvut.resources.AllTimeRecordsResource
import cz.cvut.resources.DayRecordsResource
import cz.cvut.resources.StationAllTimeRecords
import cz.cvut.resources.StationDayRecords
import cz.cvut.service.RecordService
import cz.cvut.service.StationService
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.recordRoutes(recordService: RecordService, stationService: StationService) {

    get<AllTimeRecordsResource> {
        val records = recordService.getAllTimeRecords()
        call.respond(records)
    }

    get<StationAllTimeRecords> { resource ->
        if (resource.stationId.isBlank()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Missing required parameter: stationId")
        }

        stationService.getStationById(resource.stationId)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Station with ID '${resource.stationId}' not found")

        val records = recordService.getAllTimeRecordsForStation(resource.stationId)
        call.respond(records)
    }

}


