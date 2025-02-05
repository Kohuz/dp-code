package cz.cvut.repository.measurment

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.database.table.MeasurementLatest
import cz.cvut.model.measurment.MeasurementEntity
import cz.cvut.model.measurment.toMeasurement
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.and
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
        element: String) {
        return transaction {
            MeasurementEntity
                    .find {
                MeasurementDailyTable.stationId eq stationId and
                        (MeasurementDailyTable.date greaterEq dateFrom) and
                        (MeasurementDailyTable.date lessEq dateTo) and
                        (MeasurementDailyTable.element eq element)
            }
                .map { it.toMeasurement() }
        }
    }

    fun getStats(date: LocalDate, stationId: String) {
        return transaction {
            MeasurementEntity
        }
    }

    fun getRecentMeasurements(stationId: String) {
        return transaction {
            MeasurementLatest
        }
    }

    fun getLatestMeasurement(stationId: String) {
        return transaction {
            MeasurementLatest
        }
    }

//    fun getTemperatureStats(date: String, stationId: String): StationStat {
//        val record =  transaction {
//
//        }
//        val average = transaction {
//
//        }
//    }

}
