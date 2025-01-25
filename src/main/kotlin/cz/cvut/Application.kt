package cz.cvut

import cz.cvut.service.MeasurementService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import cz.cvut.service.di.serviceModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
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

    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting(get<StationService>())
    val stationService = get<StationService>()
    val stationElementService = get<StationElementService>()
    val measurementService = get<MeasurementService>()
    runBlocking {


        stationService.processAndSaveStations()
        //stationElementService.processAndSaveStationElements()
        val stations = stationService.getAllStations()
        val stationIds = stations.map { it.stationId }
        measurementService.fetchAndSaveMeasurementsForMultipleStations(stationIds)
    }


}