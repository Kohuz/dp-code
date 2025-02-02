package cz.cvut

import cz.cvut.service.MeasurementService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import cz.cvut.service.di.serviceModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

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
    configureRouting(get<StationService>())
    val stationService = get<StationService>()
    val stationElementService = get<StationElementService>()
    val measurementService = get<MeasurementService>()
    runBlocking {
        //stationService.processAndSaveStations()
        stationElementService.processAndSaveStationElements()
        stationElementService.downloadStationElementsNow()
        val stations = stationService.getAllStations()
        val stationIds = stations.map { it.stationId }
        stationIds.forEach {
//            measurementService.processHistoricalDailyJsonAndInsert(it)
//            measurementService.processHistoricalMonthlyJsonAndInsert(it)
//            measurementService.processHistoricalYearlyJsonAndInsert(it)
            //
           // measurementService.processRecentDailyJsonAndInsert(it)
            //measurementService.processLatestJsonAndInsert(1, it)

        }
       // measurementService.processStationFiles()

    }


}