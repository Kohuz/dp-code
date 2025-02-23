    package cz.cvut

    import cz.cvut.database.table.MeasurementDailyTable
    import cz.cvut.service.*
    import cz.cvut.service.di.RepositoryModule
    import cz.cvut.service.di.ServiceModule
    import io.ktor.serialization.kotlinx.json.*
    import io.ktor.server.application.*
    import io.ktor.server.plugins.contentnegotiation.*
    import io.ktor.server.resources.*
    import kotlinx.coroutines.*
    import org.jetbrains.exposed.sql.SchemaUtils
    import org.koin.ktor.ext.get
    import org.koin.ktor.plugin.Koin
    import kotlin.time.Duration.Companion.days
    import kotlin.time.Duration.Companion.hours

    fun main(args: Array<String>) {
        io.ktor.server.netty.EngineMain.main(args)
    }

    fun Application.module() {
        installDependencies()
        configureServer()

        val stationService = get<StationService>()
        val stationElementService = get<StationElementService>()
        val measurementDownloadService = get<MeasurementDownloadService>()
        val measurementService = get<MeasurementService>()
        val recordService = get<RecordService>()
        val stationDownloadService = get<StationDownloadService>()

        launchBackgroundProcessing(stationService, stationDownloadService, stationElementService, measurementDownloadService, recordService)
       // schedulePeriodicTasks(measurementDownloadService, stationService, recordService)
    }

    private fun Application.installDependencies() {
        install(Koin) {
            modules(RepositoryModule, ServiceModule)
        }
        install(ContentNegotiation) {
            json()
        }
        install(Resources)
    }

    private fun Application.configureServer() {
        configureHTTP()
        configureSerialization()
        configureDatabases()
        configureRouting(get(), get(), get(), get())
    }

    private fun CoroutineScope.launchBackgroundProcessing(
        stationService: StationService,
        stationDownloadService: StationDownloadService,
        stationElementService: StationElementService,
        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService
    ) {
        runBlocking {
            stationDownloadService.processAndSaveStations()
            stationElementService.processAndSaveStationElements()
            stationElementService.downloadElementCodelist()
        }
        launch {
            processStationsAndMeasurements(stationService, measurementDownloadService, recordService)
        }



    }

    private suspend fun processStationsAndMeasurements(
        stationService: StationService,

        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService
    ) {


        val stationIds = stationService.getAllStations().map { it.stationId }
        val activeStationIds = stationService.getAllStations(active = true).map { it.stationId }

        runBlocking {

            processHistoricalMeasurements(stationIds, measurementDownloadService, recordService)
            processRecentMeasurements(activeStationIds, measurementDownloadService, recordService)

        }

        stationIds.forEach { stationId ->
            recordService.calculateAndInsertRecords(stationId)
        }

        measurementDownloadService.proccessLatestJsonAndInsert()
    }

    private suspend fun processHistoricalMeasurements(
        stationIds: List<String>,
        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService
    ) {

        stationIds.forEach { stationId ->
            measurementDownloadService.processHistoricalDailyJsonAndInsert(stationId)
            measurementDownloadService.processHistoricalMonthlyJsonAndInsert(stationId)
            measurementDownloadService.processHistoricalYearlyJsonAndInsert(stationId)
        }


    }

    private suspend fun processRecentMeasurements(
        activeStationIds: List<String>,
        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService
    ) {
        activeStationIds.forEach { stationId ->
           measurementDownloadService.processRecentDailyJsonAndInsert(stationId)

           recordService.calculateAndInsertRecords(stationId)
        }
    }

//    private suspend fun deleteOldLatestMeasurements(measurementService: MeasurementService) {
//        measurementService.deleteOldLatest()
//    }


    fun Application.schedulePeriodicTasks(
        measurementDownloadService: MeasurementDownloadService,
        stationService: StationService,
        recordService: RecordService
    ) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {
            while (isActive) {
                try {
                    measurementDownloadService.proccessLatestJsonAndInsert(true)
                } catch (e: Exception) {
                    log.error("Error processing latest measurements", e)
                }
                delay(1.hours)
            }
        }

        scope.launch {
            while (isActive) {
                try {
                    val activeStationIds = stationService.getAllStations(active = true).map { it.stationId }
                    processRecentMeasurements(activeStationIds, measurementDownloadService, recordService)
                } catch (e: Exception) {
                    log.error("Error processing daily stats", e)
                }
                delay(1.days)
            }
        }
    }
