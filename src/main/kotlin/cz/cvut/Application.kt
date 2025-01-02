package cz.cvut

import cz.cvut.service.StationService
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
    runBlocking {
        StationService.processAndSaveStations()
    }
}
