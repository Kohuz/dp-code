package cz.cvut.resources

import io.ktor.resources.*

@Resource("/measurements/{stationId}")
class MeasurementResource(val stationId: String) {

    @Resource("statsDay/{date}")
    class StatsDay(val parent: MeasurementResource, val date: String)

    @Resource("statsDayLongTerm")
    class StatsDayLongTerm(val parent: MeasurementResource, val date: String)

    @Resource("actual")
    class Actual(val parent: MeasurementResource)

    @Resource("recent")
    class Recent(val parent: MeasurementResource)
}
