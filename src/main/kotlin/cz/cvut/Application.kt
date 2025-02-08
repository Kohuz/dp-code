package cz.cvut

import cz.cvut.service.MeasurementService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import cz.cvut.service.di.serviceModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import kotlinx.coroutines.*
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(Koin) {
        modules(serviceModule)
    }
    install(ContentNegotiation) {
        json()
    }

    install(Resources)
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting(get<StationService>(), get<MeasurementService>())
    val stationService = get<StationService>()
    val stationElementService = get<StationElementService>()
    val measurementService = get<MeasurementService>()
    runBlocking {
        //stationService.processAndSaveStations()
        //stationElementService.processAndSaveStationElements()
        //stationElementService.downloadStationElementCodelist()
        val stations = stationService.getAllStations()
        val stationIds = stations.map { it.stationId }
        val activeStations = stationService.getAllStations(active = true)
        val activeStationIds = stations.map { it.stationId }
        stationIds.forEach {
//            measurementService.processHistoricalDailyJsonAndInsert(it)
//            measurementService.processHistoricalMonthlyJsonAndInsert(it)
//            measurementService.processHistoricalYearlyJsonAndInsert(it)
//
            //measurementService.processLatestJsonAndInsert(3, it)

        }
        activeStationIds.forEach {
            measurementService.processRecentDailyJsonAndInsert(it)
        }
        //measurementService.proccessLatestJsonAndInsert()

    }
}
//fun Application.schedulePeriodicTasks(measurementService: MeasurementService) {
//    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//
//    // Hourly task
//    scope.launch {
//        while (isActive) {
//            try {
//                measurementService.processLatestJsonAndInsert()
//            } catch (e: Exception) {
//                log.error("Error processing latest measurements", e)
//            }
//            delay(1.hours)
//        }
//    }
//
//    // Daily task
//    scope.launch {
//        while (isActive) {
//            try {
//                measurementService.processDailyStats() // Replace with actual function
//            } catch (e: Exception) {
//                log.error("Error processing daily stats", e)
//            }
//            delay(1.days)
//        }
//    }
//}