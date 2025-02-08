package cz.cvut.resources

import io.ktor.resources.*

@Resource("/measurements/{stationId}")
class MeasurementResource(val stationId: String) {

    @Resource("dayRecords/{date}")
    class DayRecords(val parent: MeasurementResource, val date: String)

    @Resource("statsDayLongTerm")
    class StatsDayLongTerm(val parent: MeasurementResource, val date: String)

    @Resource("actual")
    class Actual(val parent: MeasurementResource)

    @Resource("recent")
    class Recent(val parent: MeasurementResource)
}
