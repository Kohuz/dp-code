package cz.cvut.repository.measurment

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.MeasurementTable
import cz.cvut.model.measurment.Measurement
import cz.cvut.model.measurment.MeasurementEntity
import cz.cvut.model.measurment.toMeasurement
import cz.cvut.model.stationElement.StationElement
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class MeasurementRepository {

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
                MeasurementTable.stationId eq stationId and
                        (MeasurementTable.date greaterEq dateFrom) and
                        (MeasurementTable.date lessEq dateTo) and
                        (MeasurementTable.element eq element)
            }
                .map { it.toMeasurement() }
        }
    }
}
