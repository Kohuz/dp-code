package cz.cvut.model

import cz.cvut.database.StationElementTable
import cz.cvut.database.StationElementTable.nullable
import cz.cvut.database.table.StationTable
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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



object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: LocalDateTime) {
                encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): LocalDateTime {
                return LocalDateTime.parse(decoder.decodeString())
        }
}
