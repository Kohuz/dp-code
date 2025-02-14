package cz.cvut

import cz.cvut.controller.measurementRoutes
import cz.cvut.controller.recordRoutes
import cz.cvut.controller.stationElementCodelist
import cz.cvut.controller.stationRoutes
import cz.cvut.service.MeasurementService
import cz.cvut.service.RecordService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    stationService: StationService,
    measurementService: MeasurementService,
    recordService: RecordService,
    stationElementService: StationElementService) {
    routing {
        stationRoutes(stationService)
        measurementRoutes(measurementService, stationService)
        recordRoutes(recordService, stationService)
        stationElementCodelist(stationElementService)
    }
}