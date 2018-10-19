package com.zapic.sdk.android;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.assertSame;

public class ZapicPlayerUnitTest {
    @Rule
    public ExpectedException assertThrows = ExpectedException.none();

    @Test
    public void getIconUrl_returnsValue() throws MalformedURLException {
        // Arrange
        final URL expectedIconUrl = new URL("https://www.example.com/icon.png");
        final ZapicPlayer player = new ZapicPlayer(UUID.randomUUID().toString(), "John Smith", expectedIconUrl, "ABCDEFG");

        // Act
        final URL actualIconUrl = player.getIconUrl();

        // Assert
        assertSame(expectedIconUrl, actualIconUrl);
    }

    @Test
    public void getId_returnsValue() throws MalformedURLException {
        // Arrange
        final String expectedId = UUID.randomUUID().toString();
        final ZapicPlayer player = new ZapicPlayer(expectedId, "John Smith", new URL("https://www.example.com/icon.png"), "ABCDEFG");

        // Act
        final String actualId = player.getId();
        final String actualPlayerId = player.getPlayerId();

        // Assert
        assertSame(expectedId, actualId);
        assertSame(expectedId, actualPlayerId);
    }

    @Test
    public void getName_returnsValue() throws MalformedURLException {
        // Arrange
        final String expectedName = "John Smith";
        final ZapicPlayer player = new ZapicPlayer(UUID.randomUUID().toString(), expectedName, new URL("https://www.example.com/icon.png"), "ABCDEFG");

        // Act
        final String actualName = player.getName();

        // Assert
        assertSame(expectedName, actualName);
    }

    @Test
    public void getNotificationToken_returnsValue() throws MalformedURLException {
        // Arrange
        final String expectedNotificationToken = "ABCDEFG";
        final ZapicPlayer player = new ZapicPlayer(UUID.randomUUID().toString(), "John Smith", new URL("https://www.example.com/icon.png"), expectedNotificationToken);

        // Act
        final String actualNotificationToken = player.getNotificationToken();

        // Assert
        assertSame(expectedNotificationToken, actualNotificationToken);
    }

    @Test
    public void iconUrl_throwsIllegalArgumentExceptionWhenNull() {
        // Arrange
        this.assertThrows.expect(IllegalArgumentException.class);

        // Act
        new ZapicPlayer(UUID.randomUUID().toString(), "John Smith", null, "ABCDEFG");
    }

    @Test
    public void id_throwsIllegalArgumentExceptionWhenNull() throws MalformedURLException {
        // Arrange
        this.assertThrows.expect(IllegalArgumentException.class);

        // Act
        new ZapicPlayer(null, "John Smith", new URL("https://www.example.com/icon.png"), "ABCDEFG");
    }

    @Test
    public void name_throwsIllegalArgumentExceptionWhenNull() throws MalformedURLException {
        // Arrange
        this.assertThrows.expect(IllegalArgumentException.class);

        // Act
        new ZapicPlayer(UUID.randomUUID().toString(), null, new URL("https://www.example.com/icon.png"), "ABCDEFG");
    }

    @Test
    public void notificationToken_throwsIllegalArgumentExceptionWhenNull() throws MalformedURLException {
        // Arrange
        this.assertThrows.expect(IllegalArgumentException.class);

        // Act
        new ZapicPlayer(UUID.randomUUID().toString(), "John Smith", new URL("https://www.example.com/icon.png"), null);
    }
}
