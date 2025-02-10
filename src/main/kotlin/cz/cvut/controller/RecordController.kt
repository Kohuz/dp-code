//package cz.cvut.controller
//
//import cz.cvut.resources.AllTimeRecordsResource
//import cz.cvut.resources.DayRecordsResource
//import cz.cvut.resources.StationAllTimeRecords
//import cz.cvut.resources.StationDayRecords
//import cz.cvut.service.RecordService
//import io.ktor.http.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import kotlin.text.get
//
//fun Route.measurementRoutes(recordService: RecordService) {
//    get<DayRecordsResource> { params ->
//        val stats = recordService.getRecords(resource.date, resource.parent.stationId)
//        call.respond(stats)
//    }
//
//    get<AllTimeRecordsResource> { resource ->
//        val records = recordService.getAllTimeRecords()
//        call.respond(records)
//    }
//
//    get<StationAllTimeRecords> { resource ->
//        val records = recordService.getAllTimeRecords()
//        call.respond(records)
//    }
//
//    get<StationDayRecords> { resource ->
//        val records = recordService.getAllTimeRecords()
//        call.respond(records)
//    }
//}