package cz.cvut

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.StationTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/dp",
        user = "root",
        password = "temp_password"
    )
    transaction {
        SchemaUtils.drop(StationTable, StationElementTable)
        SchemaUtils.create(StationTable, StationElementTable)
    }
}