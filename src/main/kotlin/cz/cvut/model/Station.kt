package cz.cvut.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Station (
        val stationId: String,
        val code: String,
        @Serializable(with = ZonedDateTimeSerializer::class)
        val startDate: ZonedDateTime? = null,
        @Serializable(with = ZonedDateTimeSerializer::class)
        val endDate: ZonedDateTime,
        val location: String,
        val longitude: Double,
        val latitude: Double,
        val elevation: Double
    )

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ZonedDateTime) {
                encoder.encodeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
        }

        override fun deserialize(decoder: Decoder): ZonedDateTime {
                return ZonedDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
}