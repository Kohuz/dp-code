package cz.cvut

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleCache.cacheOutput
import com.ucasoft.ktor.simpleMemoryCache.*
import cz.cvut.controller.measurementRoutes
import cz.cvut.controller.recordRoutes
import cz.cvut.controller.stationElementCodelist
import cz.cvut.controller.stationRoutes
import cz.cvut.service.MeasurementService
import cz.cvut.service.RecordService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection
import java.sql.DriverManager
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

fun Application.configureRouting(
    stationService: StationService,
    measurementService: MeasurementService,
    recordService: RecordService,
    stationElementService: StationElementService) {
    routing {
        get("/") {
            call.respondText("Hello World!")
         }
        stationRoutes(stationService)
        measurementRoutes(measurementService, stationService)
        recordRoutes(recordService, stationService)
        stationElementCodelist(stationElementService)

    }
}