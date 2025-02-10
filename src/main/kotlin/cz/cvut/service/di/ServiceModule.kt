package cz.cvut.service.di

import cz.cvut.repository.station.StationRepository
import cz.cvut.repository.measurment.MeasurementRepository
import cz.cvut.service.MeasurementDownloadService
import cz.cvut.service.MeasurementService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import org.koin.dsl.module

val serviceModule = module {
    single { StationElementService() }
    single { StationService(get()) }
    single { StationRepository()}
    single { MeasurementService(get()) }
    single { MeasurementRepository() }
    single { MeasurementDownloadService(get())}
}