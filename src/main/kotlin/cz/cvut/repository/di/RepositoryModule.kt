package cz.cvut.service.di

import cz.cvut.repository.station.StationRepository
import cz.cvut.repository.measurment.MeasurementRepository
import cz.cvut.repository.record.RecordRepository
import cz.cvut.repository.stationElement.StationElementRepository
import cz.cvut.service.MeasurementDownloadService
import cz.cvut.service.MeasurementService
import cz.cvut.service.StationElementService
import cz.cvut.service.StationService
import org.koin.dsl.module

val RepositoryModule = module {
    single { StationRepository()}
    single { MeasurementRepository() }
    single { StationElementRepository()}
    single { RecordRepository()}
}