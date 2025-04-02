package cz.cvut

import cz.cvut.model.measurement.MeasurementLatest
import cz.cvut.model.measurement.MeasurementMonthly
import cz.cvut.model.measurement.MeasurementYearly
import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.stationElement.StationElementRepository
import cz.cvut.service.MeasurementService
import kotlinx.datetime.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class MeasurementServiceTest {

    @Mock
    private lateinit var measurementRepository: MeasurementRepository

    @Mock
    private lateinit var stationElementRepository: StationElementRepository

    @InjectMocks
    private lateinit var measurementService: MeasurementService

    @Test
    fun `getMeasurementsDaily should return daily measurements`() {
        // Arrange
        val stationId = "ST001"
        val dateFrom = "2023-01-01"
        val dateTo = "2023-01-31"
        val element = "T"
        val expectedMeasurements = listOf(
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = LocalDate.parse("2023-01-01"),
                value = 10.5,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = LocalDate.parse("2023-01-02"),
                value = 11.2,
                quality = null
            )
        )

        whenever(measurementRepository.getMeasurementsDailyByStationandDateandElement(
            stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element
        )).thenReturn(expectedMeasurements)

        // Act
        val result = measurementService.getMeasurementsDaily(stationId, dateFrom, dateTo, element)

        // Assert
        assertEquals(expectedMeasurements, result)
        verify(measurementRepository).getMeasurementsDailyByStationandDateandElement(
            stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    @Test
    fun `getMeasurementsMonthly should return monthly measurements`() {
        // Arrange
        val stationId = "ST001"
        val dateFrom = "2023-01-01"
        val dateTo = "2023-12-31"
        val element = "T"
        val expectedMeasurements = listOf(
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2023,
                month = 1,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 5.5
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2023,
                month = 2,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 6.2
            )
        )

        whenever(measurementRepository.getMeasurementsMonthlyByStationandDateandElement(
            stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element
        )).thenReturn(expectedMeasurements)

        // Act
        val result = measurementService.getMeasurementsMonthly(stationId, dateFrom, dateTo, element)

        // Assert
        assertEquals(expectedMeasurements, result)
        verify(measurementRepository).getMeasurementsMonthlyByStationandDateandElement(
            stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    @Test
    fun `getStatsDayLongTerm should return correct statistics`() {
        // Arrange
        val stationId = "ST001"
        val date = "2023-05-15"
        val element1 = "T"
        val element2 = "H"

        val records = listOf(
            MeasurementDaily(
                stationId = stationId,
                element = element1,
                vtype = "AVG",
                date = LocalDate.parse("2020-05-15"),
                value = 25.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element1,
                vtype = "AVG",
                date = LocalDate.parse("2021-05-15"),
                value = 30.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element1,
                vtype = "AVG",
                date = LocalDate.parse("2022-05-15"),
                value = 20.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element2,
                vtype = "AVG",
                date = LocalDate.parse("2020-05-15"),
                value = 60.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element2,
                vtype = "AVG",
                date = LocalDate.parse("2021-05-15"),
                value = 70.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element2,
                vtype = "AVG",
                date = LocalDate.parse("2022-05-15"),
                value = 65.0,
                quality = null
            )
        )

        whenever(measurementRepository.getLongTermMeasurementsDaily(stationId, null))
            .thenReturn(records)

        // Act
        val result = measurementService.getStatsDayLongTerm(stationId, date)

        // Assert
        assertEquals(2, result.size)

        val tempStats = result.find { it.element == element1 }
        assertNotNull(tempStats)
        assertEquals(30.0, tempStats?.highest)
        assertEquals(20.0, tempStats?.lowest)
        assertEquals(25.0, tempStats?.average)

        val humidityStats = result.find { it.element == element2 }
        assertNotNull(humidityStats)
        assertEquals(70.0, humidityStats?.highest)
        assertEquals(60.0, humidityStats?.lowest)
        assertEquals(65.0, humidityStats?.average)
    }

    @Test
    fun `getStatsMonthLongTerm should return correct statistics`() {
        // Arrange
        val stationId = "ST001"
        val date = "2023-05-15"
        val element = "T"

        val records = listOf(
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2020,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "MAX",
                value = 25.0
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2020,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "MIN",
                value = 10.0
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2020,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 17.5
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2021,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "MAX",
                value = 30.0
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2021,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "MIN",
                value = 15.0
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2021,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 22.5
            )
        )

        whenever(measurementRepository.getLongTermMeasurementsMonthly(stationId, null))
            .thenReturn(records)

        // Act
        val result = measurementService.getStatsMonthLongTerm(stationId, date)

        // Assert
        assertEquals(1, result.size)
        val stats = result[0]
        assertEquals(element, stats.element)
        assertEquals(30.0, stats.highest)
        assertEquals(10.0, stats.lowest)
        assertEquals(20.0, stats.average) // (17.5 + 22.5) / 2
    }

    @Test
    fun `getMeasurementsForDayAndMonth should return filtered records`() {
        // Arrange
        val stationId = "ST001"
        val date = "2023-05-15"
        val element = "T"

        val records = listOf(
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = LocalDate.parse("2020-05-15"),
                value = 25.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = LocalDate.parse("2021-05-15"),
                value = 30.0,
                quality = null
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = LocalDate.parse("2020-06-15"),
                value = 20.0,
                quality = null
            ), // Wrong month
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = LocalDate.parse("2021-05-16"),
                value = 35.0,
                quality = null
            )  // Wrong day
        )

        whenever(measurementRepository.getLongTermMeasurementsDaily(stationId, element))
            .thenReturn(records)

        // Act
        val result = measurementService.getMeasurementsForDayAndMonth(stationId, date, element)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.date.dayOfMonth == 15 && it.date.month == Month.MAY })
    }

    @Test
    fun `getMeasurementsForMonth should return filtered records`() {
        // Arrange
        val stationId = "ST001"
        val date = "2023-05-15"
        val element = "T"

        val records = listOf(
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2020,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 25.0
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2021,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 30.0
            ),
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2020,
                month = 6,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 20.0
            ), // Wrong month
            MeasurementMonthly(
                stationId = stationId,
                element = element,
                year = 2021,
                month = 5,
                timeFunction = "AVG",
                mdFunction = "MAX",
                value = 35.0
            )  // Wrong function
        )

        whenever(measurementRepository.getLongTermMeasurementsMonthly(stationId, element))
            .thenReturn(records)

        // Act
        val result = measurementService.getMeasurementsForMonth(stationId, date, element)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all {
            Month.of(it.month) == Month.MAY &&
                    it.mdFunction == "AVG" &&
                    it.timeFunction == "AVG"
        })
    }

    @Test
    fun `getMeasurementsYearly should return yearly measurements`() {
        // Arrange
        val stationId = "ST001"
        val dateFrom = "2020-01-01"
        val dateTo = "2023-12-31"
        val element = "T"
        val expectedMeasurements = listOf(
            MeasurementYearly(
                stationId = stationId,
                element = element,
                year = 2020,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 10.5,
                flagRepeat = null,
                flagInterrupted = null
            ),
            MeasurementYearly(
                stationId = stationId,
                element = element,
                year = 2021,
                timeFunction = "AVG",
                mdFunction = "AVG",
                value = 11.2,
                flagRepeat = "R",
                flagInterrupted = null
            )
        )

        whenever(measurementRepository.getMeasurementsYearlyByStationandDateandElement(
            stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
        ).thenReturn(expectedMeasurements)

        // Act
        val result = measurementService.getMeasurementsYearly(stationId, dateFrom, dateTo, element)

        // Assert
        assertEquals(expectedMeasurements, result)
        verify(measurementRepository).getMeasurementsYearlyByStationandDateandElement(
            stationId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo), element)
    }

    @Test
    fun `getActualMeasurements should return latest measurements for all elements`() {
        // Arrange
        val stationId = "ST001"
        val elements = listOf("T", "H", "P")
        val now = Instant.now()
        val timestamp = LocalDateTime.now()

        val expectedMeasurements = listOf(
            MeasurementLatest(
                stationId = stationId,
                element = "T",
                timestamp = timestamp.toKotlinLocalDateTime(),
                value = 22.5,
                flag = "G",
                quality = 1.0,
                createdAt = now.toKotlinInstant()
            ),
            MeasurementLatest(
                stationId = stationId,
                element = "H",
                timestamp = timestamp.toKotlinLocalDateTime(),
                value = 65.0,
                flag = "Q",
                quality = 0.8,
                createdAt = now.toKotlinInstant()
            )
        )

        whenever(stationElementRepository.getElementsForStation(stationId)).thenReturn(elements)
        whenever(measurementRepository.getLatestMeasurement("T", stationId)).thenReturn(expectedMeasurements[0])
        whenever(measurementRepository.getLatestMeasurement("H", stationId)).thenReturn(expectedMeasurements[1])
        whenever(measurementRepository.getLatestMeasurement("P", stationId)).thenReturn(null)

        // Act
        val result = measurementService.getActualMeasurements(stationId)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.containsAll(expectedMeasurements))
        verify(stationElementRepository).getElementsForStation(stationId)
        verify(measurementRepository, times(3)).getLatestMeasurement(any(), eq(stationId))
    }

    @Test
    fun `getStatsDay should return daily statistics for given date`() {
        // Arrange
        val stationId = "ST001"
        val date = "2023-05-15"
        val parsedDate = LocalDate.parse(date)
        val element = "T"

        val expectedStats = listOf(
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "MAX",
                date = parsedDate,
                value = 25.0,
                flag = "G",
                quality = 1.0
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "MIN",
                date = parsedDate,
                value = 15.0,
                flag = "G",
                quality = 1.0
            ),
            MeasurementDaily(
                stationId = stationId,
                element = element,
                vtype = "AVG",
                date = parsedDate,
                value = 20.0,
                flag = "G",
                quality = 1.0
            )
        )

        whenever(measurementRepository.getStatsDay(stationId, parsedDate))
            .thenReturn(expectedStats)

        // Act
        val result = measurementService.getStatsDay(stationId, date)

        // Assert
        assertEquals(expectedStats, result)
        verify(measurementRepository).getStatsDay(stationId, parsedDate)
    }

    @Test
    fun `getStatsDay should handle empty result from repository`() {
        // Arrange
        val stationId = "ST001"
        val date = "2023-05-15"
        val parsedDate = LocalDate.parse(date)

        whenever(measurementRepository.getStatsDay(stationId, parsedDate))
            .thenReturn(emptyList())

        // Act
        val result = measurementService.getStatsDay(stationId, date)

        // Assert
        assertTrue(result.isEmpty())
        verify(measurementRepository).getStatsDay(stationId, parsedDate)
    }


}