package cz.cvut

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/dp",
        user = "postgres",
        password = "123456"
    )

//    Database.connect(
//        "jdbc:postgresql://localhost:5432/dp",
//        user = "root",
//        password = "temp_password"
//    )
    transaction {
        exec("DROP TABLE IF EXISTS station_element CASCADE")
        exec("DROP TABLE IF EXISTS station CASCADE")
        exec("DROP TABLE IF EXISTS measurementdaily CASCADE")
        exec("DROP TABLE IF EXISTS measurementmonthly CASCADE")
        exec("DROP TABLE IF EXISTS measurementyearly CASCADE")


        //SchemaUtils.drop(MeasurementTable)
        SchemaUtils.create(StationTable, StationElementTable, MeasurementDailyTable, MeasurementMonthlyTable, MeasurementYearlyTable, MeasurementLatest)
    }
}