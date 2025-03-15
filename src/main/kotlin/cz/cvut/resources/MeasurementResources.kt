package cz.cvut.resources

import io.ktor.resources.*

@Resource("/measurements/{stationId}/daily")
class MeasurementResourceDaily(val stationId: String, val dateFrom: String, val dateTo: String, val element: String)

@Resource("/measurements/{stationId}/monthly")
class MeasurementResourceMonthly(val stationId: String, val dateFrom: String, val dateTo: String, val element: String)

@Resource("/measurements/{stationId}/yearly")
class MeasurementResourceYearly(val stationId: String, val dateFrom: String, val dateTo: String, val element: String)

@Resource("/measurements/{stationId}/statsDayLongTerm")
class MeasurementStatsDayLongTermResource(val stationId: String, val date: String)

@Resource("/measurements/{stationId}/statsMonthLongTerm")
class MeasurementStatsMonthLongTermResource(val stationId: String, val date: String)

@Resource("/measurements/{stationId}/measurementsDayAndMonth")
class MeasurementResourceDayAndMonth(val stationId: String, val date: String, val element: String)

@Resource("/measurements/{stationId}/measurementsMonth")
class MeasurementResourceMonth(val stationId: String, val date: String, val element: String)

@Resource("/measurements/{stationId}/statsDay")
class MeasurementStatsDayResource(val stationId: String, val date: String)

@Resource("/measurements/{stationId}/actual")
class MeasurementActualResource(val stationId: String)

@Resource("/measurements/{stationId}/recent")
class MeasurementRecentResource(val stationId: String)
