package cz.cvut

import cz.cvut.service.RecordService
import cz.cvut.model.measurment.MeasurementDaily
import cz.cvut.model.record.StationRecord
import cz.cvut.repository.measurement.MeasurementRepository
import cz.cvut.repository.record.RecordRepository
import cz.cvut.repository.stationElement.StationElementRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class RecordServiceTest {

    @Mock
    private lateinit var recordRepository: RecordRepository

    @Mock
    private lateinit var stationElementRepository: StationElementRepository

    @Mock
    private lateinit var measurementRepository: MeasurementRepository

    @InjectMocks
    private lateinit var recordService: RecordService

    @Test
    fun `getAllTimeRecords should return stats grouped by element`() {
        // Arrange
        val records = listOf(
            StationRecord("ST001", "T", "max", 35.0, LocalDate.parse("2023-07-15")),
            StationRecord("ST001", "T", "min", -5.0, LocalDate.parse("2023-01-20")),
            StationRecord("ST002", "H", "max", 95.0, LocalDate.parse("2023-08-01")),
            StationRecord("ST002", "H", "min", 30.0, LocalDate.parse("2023-02-10"))
        )

        whenever(recordRepository.getAllTimeRecords()).thenReturn(records)

        // Act
        val result = recordService.getAllTimeRecords()

        // Assert
        assertEquals(2, result.size)

        val tempStats = result.find { it.element == "T" }
        assertNotNull(tempStats)
        assertEquals(35.0, tempStats?.highest?.value)
        assertEquals(-5.0, tempStats?.lowest?.value)
        assertEquals(15.0, tempStats?.average) // (35 + (-5)) / 2

        val humidityStats = result.find { it.element == "H" }
        assertNotNull(humidityStats)
        assertEquals(95.0, humidityStats?.highest?.value)
        assertEquals(30.0, humidityStats?.lowest?.value)
        assertEquals(62.5, humidityStats?.average) // (95 + 30) / 2
    }

    @Test
    fun `getAllTimeRecordsForStation should return stats for specific station`() {
        // Arrange
        val stationId = "ST001"
        val records = listOf(
            StationRecord(stationId, "T", "max", 35.0, LocalDate.parse("2023-07-15")),
            StationRecord(stationId, "T", "min", -5.0, LocalDate.parse("2023-01-20")),
            StationRecord(stationId, "P", "max", 1020.0, LocalDate.parse("2023-05-10")),
            StationRecord(stationId, "P", "min", 980.0, LocalDate.parse("2023-11-25"))
        )

        whenever(recordRepository.getAllTimeRecordsForStation(stationId)).thenReturn(records)

        // Act
        val result = recordService.getAllTimeRecordsForStation(stationId)

        // Assert
        assertEquals(2, result.size)

        val tempStats = result.find { it.element == "T" }
        assertNotNull(tempStats)
        assertEquals(35.0, tempStats?.highest?.value)
        assertEquals(-5.0, tempStats?.lowest?.value)

        val pressureStats = result.find { it.element == "P" }
        assertNotNull(pressureStats)
        assertEquals(1020.0, pressureStats?.highest?.value)
        assertEquals(980.0, pressureStats?.lowest?.value)
    }

    @Test
    fun `should insert min record for TMI element`() {
        val stationId = "ST001"
        val date = java.time.LocalDate.now()
        val measurements = listOf(
            MeasurementDaily(stationId, "TMI", "D", date.minusDays(2).toKotlinLocalDate(), 5.0, null, 1.0),
            MeasurementDaily(stationId, "TMI", "D", date.minusDays(1).toKotlinLocalDate(), 3.0, null, 1.0), // min value
            MeasurementDaily(stationId, "TMI", "D", date.toKotlinLocalDate(), 4.0, null, 1.0)
        )

        whenever(stationElementRepository.getElementsForStation(stationId)).thenReturn(listOf("TMI"))
        whenever(measurementRepository.getMeasurementsDailyByStationandandElement(stationId, "TMI")).thenReturn(measurements)
        doNothing().`when`(recordRepository).insertRecord(any())

        recordService.calculateAndInsertRecords(stationId)

        val expectedRecord = StationRecord(
            stationId = stationId,
            element = "TMI",
            recordType = "min",
            value = 3.0,
            recordDate = date.minusDays(1).toKotlinLocalDate()
        )
        verify(recordRepository).insertRecord(argThat {
            stationId == expectedRecord.stationId &&
                    element == expectedRecord.element &&
                    recordType == expectedRecord.recordType &&
                    value == expectedRecord.value &&
                    recordDate == expectedRecord.recordDate
        })
    }

    @Test
    fun `should insert max record for TMA element`() {
        val stationId = "ST001"
        val date = java.time.LocalDate.now()
        val measurements = listOf(
            MeasurementDaily(stationId, "TMA", "D", date.minusDays(2).toKotlinLocalDate(), 25.0, null, 1.0),
            MeasurementDaily(stationId, "TMA", "D", date.minusDays(1).toKotlinLocalDate(), 28.0, null, 1.0), // max value
            MeasurementDaily(stationId, "TMA", "D", date.toKotlinLocalDate(), 26.0, null, 1.0)
        )

        whenever(stationElementRepository.getElementsForStation(stationId)).thenReturn(listOf("TMA"))
        whenever(measurementRepository.getMeasurementsDailyByStationandandElement(stationId, "TMA")).thenReturn(measurements)
        doNothing().`when`(recordRepository).insertRecord(any())

        recordService.calculateAndInsertRecords(stationId)

        val expectedRecord = StationRecord(
            stationId = stationId,
            element = "TMA",
            recordType = "max",
            value = 28.0,
            recordDate = date.minusDays(1).toKotlinLocalDate()
        )
        verify(recordRepository).insertRecord(argThat {
            stationId == expectedRecord.stationId &&
                    element == expectedRecord.element &&
                    recordType == expectedRecord.recordType &&
                    value == expectedRecord.value &&
                    recordDate == expectedRecord.recordDate
        })
    }

    @Test
    fun `getStats should handle empty records`() {
        // Arrange
        val emptyRecords = emptyList<StationRecord>()
        val element = "T"

        // Act
        val result = recordService.getStats(emptyRecords, element)

        // Assert
        assertEquals(element, result.element)
        assertNull(result.highest)
        assertNull(result.lowest)
        assertNull(result.average)
    }

    @Test
    fun `getStats should handle records with null values`() {
        // Arrange
        val records = listOf(
            StationRecord("ST001", "T", "max", null, LocalDate.parse("2023-01-01")),
            StationRecord("ST001", "T", "min", null, LocalDate.parse("2023-01-02"))
        )
        val element = "T"

        // Act
        val result = recordService.getStats(records, element)

        // Assert
        assertEquals(element, result.element)
        assertNull(result.highest?.value)
        assertNull(result.lowest?.value)
        assertNull(result.average)
    }

    @Test
    fun `getStats should calculate correct stats`() {
        // Arrange
        val records = listOf(
            StationRecord("ST001", "T", "max", 30.0, LocalDate.parse("2023-07-15")),
            StationRecord("ST001", "T", "min", 10.0, LocalDate.parse("2023-01-20")),
            StationRecord("ST001", "T", "other", 20.0, LocalDate.parse("2023-04-10"))
        )
        val element = "T"

        // Act
        val result = recordService.getStats(records, element)

        // Assert
        assertEquals(30.0, result.highest?.value)
        assertEquals(10.0, result.lowest?.value)
        assertEquals(20.0, result.average) // (30 + 10 + 20) / 3
    }
}