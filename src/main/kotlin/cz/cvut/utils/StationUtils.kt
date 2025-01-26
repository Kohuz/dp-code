package cz.cvut.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import java.time.format.DateTimeFormatter

object StationUtils {
    fun parseLocalDateTime(dateTimeString: String): LocalDateTime {
        val normalizedString = if (dateTimeString.endsWith("Z")) {
            dateTimeString.removeSuffix("Z")
        } else {
            dateTimeString
        }
        return LocalDateTime.parse(normalizedString)
    }




}