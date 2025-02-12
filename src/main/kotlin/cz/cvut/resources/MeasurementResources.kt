package cz.cvut.resources

import io.ktor.resources.*

@Resource("/measurements/{stationId}")
class MeasurementResource(val stationId: String)

@Resource("/measurements/{stationId}/statsDayLongTerm")
class MeasurementStatsDayLongTermResource(val stationId: String, val date: String)

@Resource("/measurements/{stationId}/actual")
class MeasurementActualResource(val stationId: String)

@Resource("/measurements/{stationId}/recent")
class MeasurementRecentResource(val stationId: String)
