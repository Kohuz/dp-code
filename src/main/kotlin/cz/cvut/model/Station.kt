package cz.cvut.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Station(
        val stationId: String,
        val code: String,
        @Serializable(with = LocalDateTimeSerializer::class)
        val startDate: LocalDateTime? = null,
        @Serializable(with = LocalDateTimeSerializer::class)
        val endDate: LocalDateTime,
        val location: String,
        val longitude: Double,
        val latitude: Double,
        val elevation: Double,
        val stationElements: List<StationElement> = emptyList() // Add relationship
)


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: LocalDateTime) {
                encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): LocalDateTime {
                return LocalDateTime.parse(decoder.decodeString())
        }
}
