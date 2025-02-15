package cz.cvut.service.di

import cz.cvut.service.*
import org.koin.dsl.module

val ServiceModule = module {
    single { StationElementService(get()) }
    single { StationService(get()) }
    single { MeasurementService(get()) }
    single { MeasurementDownloadService(get())}
    single { RecordService(get())}
    single { StationDownloadService(get())}
}