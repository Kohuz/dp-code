package cz.cvut.repository.measurment

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.MeasurementLatestTable
import cz.cvut.database.table.MeasurementMonthlyTable
import cz.cvut.database.table.MeasurementYearlyTable
import cz.cvut.model.measurement.MeasurementLatestEntity
import cz.cvut.model.measurement.MeasurementMonthlyEntity
import cz.cvut.model.measurement.MeasurementYearlyEntity
import cz.cvut.model.measurement.toMeasurement
import cz.cvut.model.measurment.MeasurementDailyEntity
import cz.cvut.model.measurment.toMeasurement
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.json.Extract
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import java.io.StringReader

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
        transaction {
            val latestMeasurements = MeasurementLatestEntity.find { MeasurementLatestTable.stationId eq stationId }
                .orderBy(MeasurementLatestTable.timestamp to org.jetbrains.exposed.sql.SortOrder.DESC)
                .limit(24)
                .toList()
        }
    }

    fun getLatestMeasurement(stationId: String): MeasurementLatestEntity? {
        return transaction {
            MeasurementLatestEntity.find { MeasurementLatestTable.stationId eq stationId }
                .orderBy(MeasurementLatestTable.timestamp to SortOrder.DESC)
                .limit(1)
                .firstOrNull()
        }
    }

//    fun getMeasurementsByStationAndElement(stationId: String, date: LocalDate): List<MeasurementDaily> {
//        return transaction {
//            val dayOfMonth = date.dayOfMonth
//            val month = date.monthValue
//
//            MeasurementDailyEntity.find {
//                (MeasurementDailyTable.stationId eq stationId) and
//                        ((MeasurementDailyTable.date eq date) or
//                                ((MeasurementDailyTable.date.dayOfMonth eq dayOfMonth) and
//                                        (MeasurementDailyTable.date.monthValue eq month)))
//            }.map { it.toMeasurement() }
//        }
//    }


//    fun getStats(date: LocalDate, stationId: String): Map<String, StationStat> {
//        return transaction {
//            MeasurementDailyEntity
//                .find {
//                    (Extract(MeasurementDailyTable.date, DatePart.DAY) eq date.dayOfMonth) and
//                            (Extract(MeasurementDailyTable.date, DatePart.MONTH) eq date.monthValue) and
//                            (MeasurementDaily.stationId eq stationId)
//                }
//                .groupBy { it.element }
//                .mapValues { (_, measurements) ->
//                    val values = measurements.mapNotNull { it.value }
//                    StationStat(
//                        record = values.maxOrNull() ?: Double.NaN,
//                        average = values.average().takeIf { values.isNotEmpty() } ?: Double.NaN
//                    )
//                }
//        }
//    }




//    fun getTemperatureStats(date: String, stationId: String): StationStat {
//        val record =  transaction {
//
//        }
//        val average = transaction {
//
//        }
//    }

}
