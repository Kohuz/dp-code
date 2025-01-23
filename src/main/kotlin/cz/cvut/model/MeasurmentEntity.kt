import cz.cvut.database.StationElementTable
import cz.cvut.database.table.StationTable
import cz.cvut.model.StationElementEntity
import cz.cvut.model.StationEntity.Companion.referrersOn
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
