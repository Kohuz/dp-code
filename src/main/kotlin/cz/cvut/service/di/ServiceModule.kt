package cz.cvut.service.di

import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import org.koin.dsl.module

val serviceModule = module {
    single { StationService() }
    single { StationElementService() }
}