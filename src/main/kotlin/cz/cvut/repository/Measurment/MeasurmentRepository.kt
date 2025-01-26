package cz.cvut.repository.measurment

import cz.cvut.database.table.Measurement2Table
import cz.cvut.utils.StationUtils.parseLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MeasurementRepository {

    fun saveAllMeasurements(csvFilePath: String) {
        transaction {
            val sql = """
            COPY measurement2 (station_id, element, vtype, date_time, value, flag, quality)
            FROM '${csvFilePath}'
            WITH (FORMAT csv, HEADER true, DELIMITER ',');
        """.trimIndent()
            exec(sql)
        }

    }
}
