package cz.cvut.repository.measurement

import cz.cvut.database.table.*
import cz.cvut.model.measurement.*
import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.model.measurment.MeasurementDailyEntity
import cz.cvut.model.measurment.toMeasurement
import cz.cvut.model.stationElement.StationElement
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import java.io.StringReader
import java.util.*


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


    fun getMeasurementsByStationandDateandElement(
        stationId: String,
        dateFrom: LocalDate,
        dateTo: LocalDate,
        element: String,
        resolution: String) {
        return transaction {
            if(resolution == "daily"){
            MeasurementDailyEntity
                    .find {
                MeasurementDailyTable.stationId eq stationId and
                        (MeasurementDailyTable.date greaterEq dateFrom) and
                        (MeasurementDailyTable.date lessEq dateTo) and
                        (MeasurementDailyTable.element eq element)
            }
                .map { it.toMeasurement() }
        }
            else if (resolution == "monthly") {
                MeasurementMonthlyEntity
                    .find {
                        MeasurementMonthlyTable.stationId eq stationId and
                                (MeasurementMonthlyTable.year greaterEq dateFrom.year) and
                                (MeasurementMonthlyTable.month greaterEq dateFrom.monthNumber) and
                                (MeasurementMonthlyTable.year lessEq dateTo.year) and
                                (MeasurementMonthlyTable.month lessEq dateTo.monthNumber) and
                                (MeasurementMonthlyTable.element eq element)
                    }
                    .map { it.toMeasurement() }
            }
            else if (resolution == "yearly") {
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

    }


    fun getRecentMeasurements(stationId: String) {
        return transaction {
            MeasurementLatestEntity.find { MeasurementLatestTable.stationId eq stationId }
                .orderBy(MeasurementLatestTable.timestamp to SortOrder.DESC)
                .limit(24)
                .toList()
        }
    }

    fun getLatestMeasurement(element: StationElement, stationId: String): MeasurementLatest? {
        return transaction {
            MeasurementLatestEntity.find {
                        (MeasurementLatestTable.stationId eq stationId) and
                                (MeasurementLatestTable.element eq element.elementAbbreviation)
                    }
                        .orderBy(MeasurementLatestTable.timestamp to SortOrder.DESC)
                        .limit(1)
                        .firstOrNull()?.toMeasurement()
        }
    }



    fun getStatsDayLongTerm(stationId: String): List<MeasurementDaily> {
        return MeasurementDailyEntity
            .find { MeasurementDailyTable.stationId eq stationId }
            .map { it.toMeasurement()}
    }

    // Fetches all measurements for a specific station on a given date (day-specific) without filtering by element
    fun getStatsDay(stationId: String, date: LocalDate): List<MeasurementDaily> {
        return MeasurementDailyEntity
            .find {
                (MeasurementDailyTable.stationId eq stationId) and (MeasurementDailyTable.date eq date) }
            .map { it.toMeasurement()}

    }

//    fun deleteOldLatest(threshold: Int) {
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DATE, -threshold)
//        val today: LocalDate = LocalDate()
//
//        transaction {
//            MeasurementLatestTable.deleteWhere {
//                MeasurementLatestTable.timestamp lessEq calendar.time
//            }
//        }
//    }





}
