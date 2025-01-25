package cz.cvut.model.Station

import cz.cvut.database.StationElementTable
import cz.cvut.database.table.StationTable
import cz.cvut.model.StationElement.StationElementEntity
import cz.cvut.model.StationElement.toStationElement
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class StationEntity(id: EntityID<Int>) : IntEntity(id){
        companion object : IntEntityClass<StationEntity>(StationTable)

        var stationId by StationTable.stationId
        var code by StationTable.code
        var startDate by StationTable.startDate
        var endDate by StationTable.endDate
        var location by StationTable.location
        var longitude by StationTable.longitude
        var latitude by StationTable.latitude
        var elevation by StationTable.elevation

        val stationElements by StationElementEntity referrersOn StationElementTable.stationId
}


fun StationEntity.toStation(): Station {
        return Station(
                stationId = this.stationId,
                code = this.code,
                startDate = this.startDate,
                endDate = this.endDate,
                location = this.location,
                longitude = this.longitude,
                latitude = this.latitude,
                elevation = this.elevation,
                stationElements = this.stationElements.map { it.toStationElement() } // If applicable
        )
}

fun Station.toStationEntity(): StationEntity {
        return StationEntity.new {
                stationId = this@toStationEntity.stationId
                code = this@toStationEntity.code
                startDate = this@toStationEntity.startDate
                endDate = this@toStationEntity.endDate
                location = this@toStationEntity.location
                longitude = this@toStationEntity.longitude
                latitude = this@toStationEntity.latitude
                elevation = this@toStationEntity.elevation
        }
}
