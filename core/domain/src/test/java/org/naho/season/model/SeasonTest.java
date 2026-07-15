package org.naho.season.model;

import org.junit.jupiter.api.Test;
import org.naho.i18n.message.season.SeasonDetailMessageKey;
import org.naho.season.exception.SeasonDomainErrorCode;
import org.naho.shared.exception.DomainException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class SeasonTest {

    @Test
    void should_BuildSeason_Successfully_When_ValidData() {
        // Arrange
        Instant startAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endAt = Instant.now().plus(2, ChronoUnit.DAYS);

        // Act
        Season season = Season.builder()
                .id(1L)
                .seasonNo(1)
                .startAt(startAt)
                .endAt(endAt)
                .build();

        // Assert
        assertNotNull(season);
        assertEquals(1L, season.getId());
        assertEquals(1, season.getSeasonNo());
        assertEquals(startAt, season.getStartAt());
        assertEquals(endAt, season.getEndAt());
    }

    @Test
    void should_ThrowException_When_SeasonNoIsNull() {
        // Arrange
        Instant startAt = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant endAt = Instant.now().plus(2, ChronoUnit.DAYS);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
                Season.builder()
                        .startAt(startAt)
                        .endAt(endAt)
                        .build()
        );

        assertEquals(SeasonDomainErrorCode.SEASON_NO_EMPTY, exception.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_NO_EMPTY, exception.getMessage());
    }

    @Test
    void should_ThrowException_When_StartAtIsNull() {
        // Arrange
        Instant endAt = Instant.now().plus(2, ChronoUnit.DAYS);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
                Season.builder()
                        .seasonNo(1)
                        .endAt(endAt)
                        .build()
        );

        assertEquals(SeasonDomainErrorCode.SEASON_START_AT_EMPTY, exception.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_START_AT_EMPTY, exception.getMessage());
    }

    @Test
    void should_ThrowException_When_EndAtIsNull() {
        // Arrange
        Instant startAt = Instant.now().plus(1, ChronoUnit.DAYS);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
                Season.builder()
                        .seasonNo(1)
                        .startAt(startAt)
                        .build()
        );

        assertEquals(SeasonDomainErrorCode.SEASON_END_AT_EMPTY, exception.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_END_AT_EMPTY, exception.getMessage());
    }

    @Test
    void should_ThrowException_When_EndAtIsBeforeOrEqualToStartAt() {
        // Arrange
        Instant startAt = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant endAt = Instant.now().plus(1, ChronoUnit.DAYS); // endAt is before startAt

        // Act & Assert
        DomainException exception1 = assertThrows(DomainException.class, () ->
                Season.builder()
                        .seasonNo(1)
                        .startAt(startAt)
                        .endAt(endAt)
                        .build()
        );

        assertEquals(SeasonDomainErrorCode.SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT, exception1.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT, exception1.getMessage());

        // Test equal
        Instant equalTime = Instant.now().plus(1, ChronoUnit.DAYS);
        DomainException exception2 = assertThrows(DomainException.class, () ->
                Season.builder()
                        .seasonNo(1)
                        .startAt(equalTime)
                        .endAt(equalTime)
                        .build()
        );

        assertEquals(SeasonDomainErrorCode.SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT, exception2.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT, exception2.getMessage());
    }

    @Test
    void should_ThrowException_When_EndAtIsBeforeOrEqualToCurrentTime() {
        // Arrange
        Instant startAt = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant endAt = Instant.now().minus(1, ChronoUnit.DAYS); // endAt is in the past, but after startAt

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
                Season.builder()
                        .seasonNo(1)
                        .startAt(startAt)
                        .endAt(endAt)
                        .build()
        );

        assertEquals(SeasonDomainErrorCode.SEASON_END_AT_BEFORE_OR_EQUAL_TO_CURRENT_TIME, exception.getErrorCode());
        assertEquals(SeasonDetailMessageKey.SEASON_END_AT_BEFORE_OR_EQUAL_TO_CURRENT_TIME, exception.getMessage());
    }
}
