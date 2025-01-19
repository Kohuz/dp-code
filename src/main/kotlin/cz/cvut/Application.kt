package cz.cvut

import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import cz.cvut.service.di.serviceModule
import io.ktor.server.application.*
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

    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting(get<StationService>())

    runBlocking {
        val stationService = get<StationService>()
        val stationElementService = get<StationElementService>()

        stationService.processAndSaveStations()
        stationElementService.processAndSaveStationElements()
    }


}