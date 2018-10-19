package com.zapic.sdk.android;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static org.junit.Assert.*;

public class ZapicStatisticUnitTest {
    @Rule
    public ExpectedException assertThrows = ExpectedException.none();

    @Test
    public void getFormattedScore_returnsNull() {
        // Arrange
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, null, null, null);

        // Act
        final String actualFormattedScore = statistic.getFormattedScore();

        // Assert
        assertNull(actualFormattedScore);
    }

    @Test
    public void getFormattedScore_returnsValue() {
        // Arrange
        final String expectedFormattedScore = "100.0";
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, 100.0, expectedFormattedScore, 10);

        // Act
        final String actualFormattedScore = statistic.getFormattedScore();

        // Assert
        assertSame(expectedFormattedScore, actualFormattedScore);
    }

    @Test
    public void getId_returnsValue() {
        // Arrange
        final String expectedId = UUID.randomUUID().toString();
        final ZapicStatistic statistic = new ZapicStatistic(expectedId, "Title", null, null, null, null);

        // Act
        final String actualId = statistic.getId();

        // Assert
        assertSame(expectedId, actualId);
    }

    @Test
    public void getMetadata_returnsNull() {
        // Arrange
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, null, null, null);

        // Act
        final String actualMetadata = statistic.getMetadata();

        // Assert
        assertNull(actualMetadata);
    }

    @Test
    public void getMetadata_returnsValue() {
        // Arrange
        final String expectedMetadata = "{\"difficulty\":\"EASY\"}";
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", expectedMetadata, null, null, null);

        // Act
        final String actualMetadata = statistic.getMetadata();

        // Assert
        assertSame(expectedMetadata, actualMetadata);
    }

    @Test
    public void getPercentile_returnsNull() {
        // Arrange
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, null, null, null);

        // Act
        final Integer actualPercentile = statistic.getPercentile();

        // Assert
        assertNull(actualPercentile);
    }

    @Test
    public void getPercentile_returnsValue() {
        // Arrange
        final Integer expectedPercentile = 10;
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, 100.0, "100.0", expectedPercentile);

        // Act
        final Integer actualPercentile = statistic.getPercentile();

        // Assert
        assertSame(expectedPercentile, actualPercentile);
    }

    @Test
    public void getScore_returnsNull() {
        // Arrange
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, null, null, null);

        // Act
        final Double actualScore = statistic.getScore();

        // Assert
        assertNull(actualScore);
    }

    @Test
    public void getScore_returnsValue() {
        // Arrange
        final Double expectedScore = 100.0;
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), "Title", null, expectedScore, "100.0", 10);

        // Act
        final Double actualScore = statistic.getScore();

        // Assert
        assertSame(expectedScore, actualScore);
    }

    @Test
    public void getTitle_returnsValue() {
        // Arrange
        final String expectedTitle = "Title";
        final ZapicStatistic statistic = new ZapicStatistic(UUID.randomUUID().toString(), expectedTitle, null, null, null, null);

        // Act
        final String actualTitle = statistic.getTitle();

        // Assert
        assertSame(expectedTitle, actualTitle);
    }

    @Test
    public void id_throwsIllegalArgumentExceptionWhenNull() {
        // Arrange
        this.assertThrows.expect(IllegalArgumentException.class);

        // Act
        new ZapicStatistic(null, "Title", null, null, null, null);
    }

    @Test
    public void title_throwsIllegalArgumentExceptionWhenNull() {
        // Arrange
        this.assertThrows.expect(IllegalArgumentException.class);

        // Act
        new ZapicStatistic(UUID.randomUUID().toString(), null, null, null, null, null);
    }
}
