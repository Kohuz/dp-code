package cz.cvut.repository.measurment

import cz.cvut.database.table.DailyMeasurementTable
import cz.cvut.model.measurment.MeasurementEntity
import cz.cvut.model.measurment.toMeasurement
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class MeasurementRepository {
    data class StationStat (val record: Double, val average: Double)

    fun saveAllMeasurements(csvFilePath: String) {
        transaction {
            val sql = """
            COPY measurement (station_id, element, vtype, date, value, flag, quality)
            FROM '/data/test2.csv'
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
                DailyMeasurementTable.stationId eq stationId and
                        (DailyMeasurementTable.date greaterEq dateFrom) and
                        (DailyMeasurementTable.date lessEq dateTo) and
                        (DailyMeasurementTable.element eq element)
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
