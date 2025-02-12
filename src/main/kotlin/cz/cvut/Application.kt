package cz.cvut

import cz.cvut.service.*
import cz.cvut.service.di.RepositoryModule
import cz.cvut.service.di.ServiceModule
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
    installDependencies()
    configureServer()

    val stationService = get<StationService>()
    val stationElementService = get<StationElementService>()
    val measurementDownloadService = get<MeasurementDownloadService>()

    launchBackgroundProcessing(stationService, stationElementService, measurementDownloadService)
    schedulePeriodicTasks(measurementDownloadService, stationService)
}

private fun Application.installDependencies() {
    install(Koin) {
        modules(RepositoryModule, ServiceModule)
    }
    install(ContentNegotiation) {
        json()
    }
    install(Resources)
}

private fun Application.configureServer() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting(get(), get(), get(), get())
}

private fun CoroutineScope.launchBackgroundProcessing(
    stationService: StationService,
    stationElementService: StationElementService,
    measurementDownloadService: MeasurementDownloadService
) {
    launch {
        processStationsAndMeasurements(stationService, stationElementService, measurementDownloadService)
    }
}

private suspend fun processStationsAndMeasurements(
    stationService: StationService,
    stationElementService: StationElementService,
    measurementDownloadService: MeasurementDownloadService
) {
    stationService.processAndSaveStations()
    stationElementService.processAndSaveStationElements()
    stationElementService.downloadElementCodelist()

    val stationIds = stationService.getAllStations().map { it.stationId }
    val activeStationIds = stationService.getAllStations(active = true).map { it.stationId }

    processHistoricalMeasurements(stationIds, measurementDownloadService)
    processRecentMeasurements(activeStationIds, measurementDownloadService)

    measurementDownloadService.proccessLatestJsonAndInsert()
}

private suspend fun processHistoricalMeasurements(
    stationIds: List<String>,
    measurementDownloadService: MeasurementDownloadService
) {
    stationIds.forEach { stationId ->
        measurementDownloadService.processHistoricalDailyJsonAndInsert(stationId)
        measurementDownloadService.processHistoricalMonthlyJsonAndInsert(stationId)
        measurementDownloadService.processHistoricalYearlyJsonAndInsert(stationId)
        measurementDownloadService.processLatestJsonAndInsert(3, stationId)
    }
}

private suspend fun processRecentMeasurements(
    activeStationIds: List<String>,
    measurementDownloadService: MeasurementDownloadService
) {
    activeStationIds.forEach { stationId ->
        measurementDownloadService.processRecentDailyJsonAndInsert(stationId)
    }
}

fun Application.schedulePeriodicTasks(
    measurementDownloadService: MeasurementDownloadService,
    stationService: StationService
) {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    scope.launch {
        while (isActive) {
            try {
                measurementDownloadService.proccessLatestJsonAndInsert()
            } catch (e: Exception) {
                log.error("Error processing latest measurements", e)
            }
            delay(1.hours)
        }
    }

    scope.launch {
        while (isActive) {
            try {
                val activeStationIds = stationService.getAllStations(active = true).map { it.stationId }
                processRecentMeasurements(activeStationIds, measurementDownloadService)
            } catch (e: Exception) {
                log.error("Error processing daily stats", e)
            }
            delay(1.days)
        }
    }
}
