package cz.cvut

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.Measurement2Table
import cz.cvut.database.table.StationTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5433/dp",
        user = "postgres",
        password = "123456"
    )
    transaction {
        exec("DROP TABLE IF EXISTS station_element CASCADE")
        exec("DROP TABLE IF EXISTS station CASCADE")
        exec("DROP TABLE IF EXISTS measurement2 CASCADE")
        //SchemaUtils.drop(MeasurementTable)
        SchemaUtils.create(StationTable, StationElementTable, Measurement2Table)
    }
}