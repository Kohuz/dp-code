package cz.cvut

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.*
import cz.cvut.model.measurement.MeasurementLatest
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases(config: ApplicationConfig) {
    val url = config.property("storage.jdbcURL").getString()
    val user = config.property("storage.user").getString()
    val password = config.property("storage.password").getString()

    Database.connect(
        url,
        user = user,
        password = password
    )

//    Database.connect(
//        url,
//        user = user,
//        password = password
//    )
//
//    Database.connect(
//        "jdbc:postgresql://localhost:5433/dp",
//        user = "postgres",
//        password = "123456"
//    )
    transaction {
        exec("DROP TABLE IF EXISTS stationelement CASCADE")
        exec("DROP TABLE IF EXISTS station CASCADE")
        exec("DROP TABLE IF EXISTS measurementdaily CASCADE")
        exec("DROP TABLE IF EXISTS measurementmonthly CASCADE")
        exec("DROP TABLE IF EXISTS measurementyearly CASCADE")
        exec("DROP TABLE IF EXISTS elementcodelist CASCADE")


        exec("DROP TABLE IF EXISTS measurementlatest")

        SchemaUtils.create(
            StationTable,
            StationElementTable,
            MeasurementDailyTable,
            MeasurementMonthlyTable,
            MeasurementYearlyTable,
            MeasurementLatestTable,
            ElementCodelistTable,
            StationRecordTable
        )
        SchemaUtils.create(
            MeasurementLatestTable
        )
    }

   transaction {
       exec("DROP TABLE IF EXISTS elementcodelist")
              exec("DROP TABLE IF EXISTS stationelement")
       SchemaUtils.create(StationElementTable)
       SchemaUtils.create(ElementCodelistTable)
   }

//       exec(
//           """
//                CREATE INDEX IF NOT EXISTS idx_measurementlatest_id_element
//                ON measurementlatest (station_id, element, timestamp DESC);
//            """.trimIndent()
//       )
//
//       // Date-specific index
//       exec(
//           """
//        CREATE INDEX IF NOT EXISTS idx_measurementdaily_element_date
//        ON measurementdaily (element, date);
//        """.trimIndent()
//       )








//    }
}