package cz.cvut.resources

import io.ktor.resources.*
import kotlinx.datetime.LocalDate

@Resource("/recordsAllTime")
class AllTimeRecordsResource
@Resource("/dayRecords")
class DayRecordsResource(val date: LocalDate)
@Resource("/stationDayRecords/{stationId}")
class StationDayRecords(val stationId: String, val date: LocalDate)
@Resource("/stationAllTimeRecords/{stationId}")
class StationAllTimeRecords(val stationId: String)

