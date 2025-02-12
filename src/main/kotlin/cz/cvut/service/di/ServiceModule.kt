package cz.cvut.service.di

import cz.cvut.repository.station.StationRepository
import cz.cvut.repository.measurment.MeasurementRepository
import cz.cvut.service.*
import org.koin.core.scope.get
import org.koin.dsl.module

val ServiceModule = module {
    single { StationElementService(get()) }
    single { StationService(get()) }
    single { MeasurementService(get()) }
    single { MeasurementDownloadService(get())}
    single { RecordService(get())}
}