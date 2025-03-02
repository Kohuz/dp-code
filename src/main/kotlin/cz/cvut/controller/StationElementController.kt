package cz.cvut.controller

import cz.cvut.resources.ElementCodelistResource
import cz.cvut.resources.StationsResource
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stationElementCodelist(stationElementService: StationElementService) {
    get<ElementCodelistResource> {
        val elements = stationElementService.getElementsCodelist()
        call.respond(elements)
    }
}