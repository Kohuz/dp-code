package cz.cvut.repository.measurement

import cz.cvut.database.table.*
import cz.cvut.model.measurement.*
import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.model.measurment.MeasurementDailyEntity
import cz.cvut.model.measurment.toMeasurement
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import java.io.StringReader
import java.time.Instant
import java.time.temporal.ChronoUnit


class MeasurementRepository {
    data class StationStat (val record: Double, val average: Double)

    fun saveHistoricalDaily(csvData: String) {
        transaction {
            val connection = this.connection.connection as BaseConnection
            val copyManager = CopyManager(connection)
            val sql = "COPY measurementdaily (station_id, element, vtype, date, value, flag, quality) FROM STDIN WITH (FORMAT csv, HEADER true, DELIMITER ',')"

            copyManager.copyIn(sql, StringReader(csvData))
        }
    }

    fun saveHistoricalMonthly(csvData: String) {
        transaction {
            val connection = this.connection.connection as BaseConnection
            val copyManager = CopyManager(connection)
            val sql = """
                COPY measurementmonthly (station_id, element, year, month, time_function, md_function, value, flag_repeat, flag_interrupted)
                FROM STDIN WITH (FORMAT csv, HEADER true, DELIMITER ',')
            """.trimIndent()

            copyManager.copyIn(sql, StringReader(csvData))
        }
    }

    fun saveHistoricalYearly(csvData: String) {
        transaction {
            val connection = this.connection.connection as BaseConnection
            val copyManager = CopyManager(connection)
            val sql = """
                COPY measurementyearly (station_id, element, year, time_function, md_function, value, flag_repeat, flag_interrupted)
                FROM STDIN WITH (FORMAT csv, HEADER true, DELIMITER ',')
            """.trimIndent()

            copyManager.copyIn(sql, StringReader(csvData))
        }
    }

    fun saveLatestMeasurements(csvData: String) {
        transaction {
            val connection = this.connection.connection as BaseConnection
            val copyManager = CopyManager(connection)
            val sql = """
                COPY measurementlatest (station_id, element, timestamp, value, flag, quality)
                FROM STDIN WITH (FORMAT csv, HEADER true, DELIMITER ',')
            """.trimIndent()

            copyManager.copyIn(sql, StringReader(csvData))
        }
    }


    fun getMeasurementsDailyByStationandDateandElement(
        stationId: String,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        element: String): List<MeasurementDaily> {
        return transaction {
            MeasurementDailyEntity
                    .find {
                MeasurementDailyTable.stationId eq stationId and
                        (MeasurementDailyTable.date greaterEq dateFrom) and
                        (MeasurementDailyTable.date lessEq dateTo) and
                        (MeasurementDailyTable.element eq element)
            }
                .map { it.toMeasurement() }
        }
    }

    fun getMeasurementsDailyByStationandandElement(
        stationId: String,
        element: String): List<MeasurementDaily> {
        return transaction {
            MeasurementDailyEntity
                .find {
                    MeasurementDailyTable.stationId eq stationId and
                            (MeasurementDailyTable.element eq element)
                }
                .map { it.toMeasurement() }
        }
    }

    fun getMeasurementsMonthlyByStationandDateandElement(
        stationId: String,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        element: String
    ): List<MeasurementMonthly> {
        return transaction {
            MeasurementMonthlyEntity
                .find {
                    (MeasurementMonthlyTable.stationId eq stationId) and
                            (MeasurementMonthlyTable.element eq element) and
                            (
                                    (MeasurementMonthlyTable.year greaterEq dateFrom.year) and
                                            (MeasurementMonthlyTable.month greaterEq dateFrom.monthNumber) or
                                            (MeasurementMonthlyTable.year greater dateFrom.year)
                                    ) and
                            (
                                    (MeasurementMonthlyTable.year lessEq dateTo.year) and
                                            (MeasurementMonthlyTable.month lessEq dateTo.monthNumber) or
                                            (MeasurementMonthlyTable.year less dateTo.year)
                                    )
                }
                .map { it.toMeasurement() }
        }
    }

    fun getMeasurementsYearlyByStationandDateandElement(
        stationId: String,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        element: String): List<MeasurementYearly> {
        return transaction {
                MeasurementYearlyEntity
                    .find {
                        MeasurementYearlyTable.stationId eq stationId and
                                (MeasurementYearlyTable.year greaterEq dateFrom.year) and
                                (MeasurementYearlyTable.year lessEq dateTo.year) and
                                (MeasurementYearlyTable.element eq element)
                    }
                    .map { it.toMeasurement() }
            }
        }



    fun getLatestMeasurement(elementAbbreviation: String, stationId: String): MeasurementLatest? {
        return transaction {
            MeasurementLatestEntity.find {
                (MeasurementLatestTable.stationId eq stationId) and
                        (MeasurementLatestTable.element eq elementAbbreviation)
            }
                .orderBy(MeasurementLatestTable.timestamp to SortOrder.DESC)
                .limit(1)
                .firstOrNull()?.toMeasurement()
        }
    }


    fun getLongTermMeasurementsDaily(stationId: String, element: String?): List<MeasurementDaily> {
        return transaction {
            val query = MeasurementDailyTable.stationId eq stationId

            val finalQuery = if (element != null) {
                query and (MeasurementDailyTable.element eq element)
            } else {
                query
            }

            MeasurementDailyEntity
                .find { finalQuery }
                .map { it.toMeasurement() }
        }
    }

    fun getLongTermMeasurementsMonthly(stationId: String, element: String?): List<MeasurementMonthly> {
        return transaction {
            val query = MeasurementMonthlyTable.stationId eq stationId
            val finalQuery = if (element != null) {
                query and (MeasurementMonthlyTable.element eq element)
            } else {
                query
            }
            MeasurementMonthlyEntity
                .find { finalQuery }
                .map { it.toMeasurement() }
        }
    }

    // Fetches all measurements for a specific station on a given date (day-specific) without filtering by element
    fun getStatsDay(stationId: String, date: LocalDate): List<MeasurementDaily> {
        return transaction {
            MeasurementDailyEntity
                .find {
                    (MeasurementDailyTable.stationId eq stationId) and (MeasurementDailyTable.date eq date)
                }
                .map { it.toMeasurement() }
        }
    }


    fun deleteOldLatest(threshold: Int = 2) {
        val twoDaysAgo = Instant.now().minus(threshold.toLong(), ChronoUnit.DAYS)
        val kotlinInstant = twoDaysAgo.toKotlinInstant()
        transaction {
            MeasurementLatestTable.deleteWhere {
                MeasurementLatestTable.timestamp lessEq kotlinInstant.toLocalDateTime(TimeZone.UTC)
            }
        }
    }

    fun getTopMeasurementsDailyByElementAndStationOrDate(
        element: String,
        stationId: String?,
        date: LocalDate?,
        count: Int? = 10
    ): List<MeasurementDaily> {
        return transaction {
            val query = when {
                stationId != null && date != null -> {
                    (MeasurementDailyTable.element eq element) and
                            (MeasurementDailyTable.stationId eq stationId) and
                            (MeasurementDailyTable.date eq date)
                }

                stationId != null -> {
                    (MeasurementDailyTable.element eq element) and
                            (MeasurementDailyTable.stationId eq stationId)
                }

                date != null -> {
                    (MeasurementDailyTable.element eq element) and
                            (MeasurementDailyTable.date eq date)
                }

                else -> {
                    MeasurementDailyTable.element eq element
                }
            }

            val sortOrder = if (element == "TMI") SortOrder.ASC else SortOrder.DESC

            MeasurementDailyEntity
                .find { query }
                .orderBy(MeasurementDailyTable.value to sortOrder)
                .limit(count ?: 10)
                .map { it.toMeasurement() }
        }
    }
}
