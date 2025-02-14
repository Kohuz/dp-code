package cz.cvut.service.di

import cz.cvut.repository.station.StationRepository
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.record.RecordRepository
import cz.cvut.repository.stationElement.StationElementRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single { StationRepository()}
    single { MeasurementRepository() }
    single { StationElementRepository()}
    single { RecordRepository()}
}