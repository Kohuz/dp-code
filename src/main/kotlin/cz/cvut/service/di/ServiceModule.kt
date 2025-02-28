package cz.cvut.service.di

import cz.cvut.service.*
import org.koin.dsl.module

val ServiceModule = module {
    single { StationElementService(get()) }
    single { StationService(get(), get()) }
    single { MeasurementService(get(), get()) }
    single { MeasurementDownloadService(get())}
    single { RecordService(get(), get())}
    single { StationDownloadService(get())}
}