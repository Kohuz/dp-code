package cz.cvut.repository.measurment

import cz.cvut.database.table.Measurement2Table
import cz.cvut.utils.StationUtils.parseLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MeasurementRepository {

    fun saveAllMeasurements(measurements: List<List<String>>) {
        println("started sql")
        transaction {
            Measurement2Table.batchInsert(measurements) { row ->
                this[Measurement2Table.stationId] = row[0]
                this[Measurement2Table.element] = row[1]
                this[Measurement2Table.schedule] = row[2]
                this[Measurement2Table.dateTime] = parseLocalDateTime(row[3])
                this[Measurement2Table.flag] = row[4].ifEmpty { null }
                this[Measurement2Table.quality] = if (row[5].isEmpty()) -1.0 else row[5].toDoubleOrNull() ?: -1.0
                this[Measurement2Table.value] = row[6].toDoubleOrNull() ?: -1.0
            }
        }
        println("ended sql")

    }
}
