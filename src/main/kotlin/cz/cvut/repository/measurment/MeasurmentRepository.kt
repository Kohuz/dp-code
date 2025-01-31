package cz.cvut.repository.measurment

import cz.cvut.database.table.MeasurementDailyTable
import cz.cvut.model.measurment.MeasurementEntity
import cz.cvut.model.measurment.toMeasurement
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class MeasurementRepository {
    data class StationStat (val record: Double, val average: Double)

    fun saveHistoricalDaily(csvFilePath: String) {
        transaction {
            val sql = """
            COPY measurementdaily (station_id, element, vtype, date, value, flag, quality)
            FROM '$csvFilePath'
            WITH (FORMAT csv, HEADER true, DELIMITER ',');
        """.trimIndent()
            exec(sql)
        }

    }
    fun saveHistoricalMonthly(csvFilePath: String) {
        transaction {
            val sql = """
        COPY measurementmonthly (station_id, element, year, month, time_function, md_function, value, flag_repeat, flag_interrupted)
        FROM '$csvFilePath'
        WITH (FORMAT csv, HEADER true, DELIMITER ',');
        """.trimIndent()
            exec(sql)
        }
    }

    fun saveHistoricalYearly(csvFilePath: String) {
        transaction {
            val sql = """
        COPY measurementyearly (station_id, element, year, time_function, md_function, value, flag_repeat, flag_interrupted)
        FROM '$csvFilePath'
        WITH (FORMAT csv, HEADER true, DELIMITER ',');
        """.trimIndent()
            exec(sql)
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

//    fun getTemperatureStats(date: String, stationId: String): StationStat {
//        val record =  transaction {
//
//        }
//        val average = transaction {
//
//        }
//    }

}
