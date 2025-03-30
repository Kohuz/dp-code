package cz.cvut.service

import cz.cvut.model.measurement.MeasurementLatest
import cz.cvut.model.station.Station
import cz.cvut.model.stationElement.StationElement
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.station.StationRepository
import cz.cvut.repository.stationElement.StationElementRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class StationServiceTest {

    @Mock
    private lateinit var stationRepository: StationRepository

    @Mock
    private lateinit var measurementService: MeasurementService

    @Mock
    private lateinit var stationElementRepository: StationElementRepository

    @Mock
    private lateinit var measurementRepository: MeasurementRepository

    @InjectMocks
    private lateinit var stationService: StationService

    private val activeEndDate = LocalDateTime.parse("3999-12-31T23:59:00.000000")
    private val inactiveEndDate = LocalDateTime.parse("2023-12-31T23:59:00.000000")

    @Test
    fun `getAllStations should return all stations when active is null`() {
        // Given
        val stations = listOf(
            createTestStation("ST001", active = true),
            createTestStation("ST002", active = false)
        )
        whenever(stationRepository.getStationsFiltered(null)).thenReturn(stations)

        // When
        val result = stationService.getAllStations()

        // Then
        assertEquals(2, result.size)
        verify(stationRepository).getStationsFiltered(null)
    }

    @Test
    fun `getAllStations should filter active stations when active is true`() {
        // Given
        val activeStation = createTestStation("ST001", active = true)
        whenever(stationRepository.getStationsFiltered(true)).thenReturn(listOf(activeStation))

        // When
        val result = stationService.getAllStations(active = true)

        // Then
        assertEquals(1, result.size)
        assertEquals("ST001", result[0].stationId)
        verify(stationRepository).getStationsFiltered(true)
    }

    @Test
    fun `getAllStations should filter inactive stations when active is false`() {
        // Given
        val inactiveStation = createTestStation("ST002", active = false)
        whenever(stationRepository.getStationsFiltered(false)).thenReturn(listOf(inactiveStation))

        // When
        val result = stationService.getAllStations(active = false)

        // Then
        assertEquals(1, result.size)
        assertEquals("ST002", result[0].stationId)
        verify(stationRepository).getStationsFiltered(false)
    }

    @Test
    fun `getStationById should return null for non-existent station`() {
        // Given
        whenever(stationRepository.getStationById("NON_EXISTENT")).thenReturn(null)

        // When
        val result = stationService.getStationById("NON_EXISTENT")

        // Then
        assertNull(result)
    }

    @Test
    fun `getStationById should return station with measurements for active station with elements`() {
        // Given
        val stationId = "ST001"
        val station = createTestStation(stationId, active = true)
        val elements = listOf("T", "H")
        val measurements = listOf(
            createTestMeasurement(stationId, "T"),
            createTestMeasurement(stationId, "H")
        )

        whenever(stationRepository.getStationById(stationId)).thenReturn(station)
        whenever(stationElementRepository.getElementsForStation(stationId)).thenReturn(elements)
        whenever(measurementRepository.getLatestMeasurement("T", stationId)).thenReturn(measurements[0])
        whenever(measurementRepository.getLatestMeasurement("H", stationId)).thenReturn(measurements[1])

        // When
        val result = stationService.getStationById(stationId)

        // Then
        assertNotNull(result)
        assertEquals(2, result?.stationLatestMeasurements?.size)
        assertEquals("T", result?.stationLatestMeasurements?.get(0)?.element)
        assertEquals("H", result?.stationLatestMeasurements?.get(1)?.element)
    }


    @Test
    fun `getClosestStations should return single closest active station when count is 1`() {
        // Given
        val latitude = 50.0
        val longitude = 14.0
        val activeStation = createTestStation("ST001", 50.1, 14.1, active = true)
        val inactiveStation = createTestStation("ST002", 50.2, 14.2, active = false)

        whenever(stationRepository.getStationsList()).thenReturn(listOf(activeStation, inactiveStation))

        // When
        val result = stationService.getClosestStations(latitude, longitude, 1)

        // Then
        assertEquals(1, result.size)
        assertEquals("ST001", result[0].stationId)
    }

    @Test
    fun `getClosestStations should return multiple stations with measurements when active`() {
        // Given
        val latitude = 50.0
        val longitude = 14.0
        val station1 = createTestStation("ST001", 50.1, 14.1, active = true)
        val station2 = createTestStation("ST002", 50.2, 14.2, active = true)
        val elements = listOf("T", "H")
        val measurements = listOf(
            createTestMeasurement("ST001", "T"),
            createTestMeasurement("ST001", "H"),
            createTestMeasurement("ST002", "T")
        )

        whenever(stationRepository.getStationsList()).thenReturn(listOf(station1, station2))
        whenever(stationElementRepository.getElementsForStation("ST001")).thenReturn(elements)
        whenever(stationElementRepository.getElementsForStation("ST002")).thenReturn(listOf("T"))
        whenever(measurementRepository.getLatestMeasurement("T", "ST001")).thenReturn(measurements[0])
        whenever(measurementRepository.getLatestMeasurement("H", "ST001")).thenReturn(measurements[1])
        whenever(measurementRepository.getLatestMeasurement("T", "ST002")).thenReturn(measurements[2])

        // When
        val result = stationService.getClosestStations(latitude, longitude, 2)

        // Then
        assertEquals(2, result.size)
        assertEquals("ST001", result[0].stationId)
        assertEquals(2, result[0].stationLatestMeasurements.size)
        assertEquals("ST002", result[1].stationId)
        assertEquals(1, result[1].stationLatestMeasurements.size)
    }

    @Test
    fun `exists should return true for existing station`() {
        // Given
        val stationId = "ST001"
        whenever(stationRepository.getStationById(stationId)).thenReturn(createTestStation(stationId))

        // When
        val result = stationService.exists(stationId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `exists should return false for non-existent station`() {
        // Given
        val stationId = "NON_EXISTENT"
        whenever(stationRepository.getStationById(stationId)).thenReturn(null)

        // When
        val result = stationService.exists(stationId)

        // Then
        assertFalse(result)
    }

    // Helper functions
    private fun createTestStation(
        stationId: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        active: Boolean = true
    ): Station {
        return Station(
            stationId = stationId,
            code = stationId,
            startDate = LocalDateTime.parse("2020-01-01T00:00:00"),
            endDate = if (active) activeEndDate else inactiveEndDate,
            location = "Location $stationId",
            longitude = longitude,
            latitude = latitude,
            elevation = 0.0,
            stationElements = listOf(
                StationElement(
                    stationId = stationId,
                    beginDate = LocalDateTime.parse("2020-01-01T00:00:00"),
                    endDate = if (active) activeEndDate else inactiveEndDate,
                    elementAbbreviation = "T",
                    elementName = "Temperature",
                    unitDescription = "°C",
                    height = null,
                    schedule = "daily"
                )
            )
        )
    }

    private fun createTestMeasurement(stationId: String, element: String): MeasurementLatest {
        return MeasurementLatest(
            stationId = stationId,
            element = element,
            timestamp = LocalDateTime.parse("2023-06-15T12:00:00"),
            value = 25.0,
            flag = "G",
            quality = 1.0,
            createdAt = Instant.fromEpochSeconds(1686830400)
        )
    }
}