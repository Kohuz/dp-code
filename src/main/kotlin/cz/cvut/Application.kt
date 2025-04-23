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
    import org.jetbrains.exposed.sql.transactions.transaction
    import org.koin.ktor.ext.get
    import org.koin.ktor.plugin.Koin
    import java.time.Duration
    import java.time.LocalDateTime
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
        schedulePeriodicTasks(measurementDownloadService, stationService, recordService, measurementService)
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
        configureDatabases(environment.config)
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

    private fun processStationsAndMeasurements(
        stationService: StationService,
        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService
    ) = runBlocking {
        val stationIds = stationService.getAllStations().map { it.stationId }
        val activeStationIds = stationService.getAllStations(active = true).map { it.stationId }

        coroutineScope {
            val historicalJob = launch {
                processHistoricalMeasurements(stationIds, measurementDownloadService, recordService)
            }
            val recentJob = launch {
                processRecentMeasurements(activeStationIds, measurementDownloadService, recordService)
            }
            historicalJob.join()
            recentJob.join()
        }

        measurementDownloadService.proccessLatestJsonAndInsert()

        stationIds.forEach {
            recordService.calculateAndInsertRecords(it)
        }

        createIndexes()
    }

    fun createIndexes() {
        transaction {
            exec(
                """
                CREATE INDEX IF NOT EXISTS idx_measurementlatest_id_element
                ON measurementlatest (station_id, element, timestamp DESC);
            """.trimIndent()
            )

            // Date-specific index
            exec(
                """
        CREATE INDEX idx_measurementdaily_station_date_element ON measurementdaily 
            (station_id, date, element);
        """.trimIndent()
            )
            exec(
                """
                    CREATE INDEX idx_measurementdaily_element_date_value 
                    ON measurementdaily(element, date, value DESC);
                """.trimIndent()
            )
            exec(
                """
                    CREATE INDEX idx_measurementdaily_element_date_value 
                    ON measurementdaily(element, date, value ASC);
                """.trimIndent()
            )
        }
    }

    private suspend fun processHistoricalMeasurements(
        stationIds: List<String>,
        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService
    ) {

        stationIds.forEach { stationId ->
            coroutineScope {
                val daily = async { measurementDownloadService.processHistoricalDailyJsonAndInsert(stationId) }
                val monthly = async { measurementDownloadService.processHistoricalMonthlyJsonAndInsert(stationId) }
                val yearly = async { measurementDownloadService.processHistoricalYearlyJsonAndInsert(stationId) }

                // Wait for all measurement processing to complete before calculating records
                daily.await()
                monthly.await()
                yearly.await()

            }
        }


    }

    private suspend fun processRecentMeasurements(
        activeStationIds: List<String>,
        measurementDownloadService: MeasurementDownloadService,
        recordService: RecordService,
        onlyYesterday: Boolean = false
    ) {
        activeStationIds.forEach { stationId ->
           measurementDownloadService.processRecentDailyJsonAndInsert(stationId, onlyYesterday)
        }
    }

    fun Application.schedulePeriodicTasks(
        measurementDownloadService: MeasurementDownloadService,
        stationService: StationService,
        recordService: RecordService,
        measurementService: MeasurementService
    ) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {
            while (isActive) {
                try {
                    // Calculate the delay until the next xx:05
                    val now = LocalDateTime.now()
                    val nextRun = now.withMinute(5).withSecond(0).withNano(0)
                    val delayMillis = if (now.isBefore(nextRun)) {
                        Duration.between(now, nextRun).toMillis()
                    } else {
                        Duration.between(now, nextRun.plusHours(1)).toMillis()
                    }

                    // Delay until the next xx:05
                    delay(delayMillis)

                    // Execute the task
                    measurementDownloadService.proccessLatestJsonAndInsert(true)
                } catch (e: Exception) {
                    log.error("Error processing latest measurements", e)
                }
            }
        }

        scope.launch {
            while (isActive) {
                try {
                    // Calculate the delay until the next 3:00
                    val now = LocalDateTime.now()
                    val nextRun = now.withHour(3).withMinute(0).withSecond(0).withNano(0)
                    val delayMillis = if (now.isBefore(nextRun)) {
                        Duration.between(now, nextRun).toMillis()
                    } else {
                        Duration.between(now, nextRun.plusDays(1)).toMillis()
                    }

                    // Delay until the next 3:00
                    delay(delayMillis)

                    // Execute the task
                    val activeStationIds = stationService.getAllStations(active = true).map { it.stationId }
                    processRecentMeasurements(activeStationIds, measurementDownloadService, recordService, true)

                    try {
                        measurementService.deleteOldLatest()
                    } catch (e: Exception) {
                        log.error("Error deleting measurements")
                    }
                } catch (e: Exception) {
                    log.error("Error processing daily stats", e)
                }


            }
        }
    }
