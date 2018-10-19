package com.zapic.sdk.android;

import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.net.URL;

/**
 * Represents a Zapic player.
 *
 * @author Kyle Dodson
 * @since 1.1.0
 */
public final class ZapicPlayer {
    /**
     * The URL of the profile icon.
     */
    @NonNull
    private final URL iconUrl;

    /**
     * The unique identifier.
     */
    @NonNull
    private final String id;

    /**
     * The profile name.
     */
    @NonNull
    private final String name;

    /**
     * The notification token.
     */
    @NonNull
    private final String notificationToken;

    /**
     * Creates a new {@link ZapicPlayer} instance.
     *
     * @param id                The unique identifier.
     * @param name              The profile name.
     * @param iconUrl           The URL of the profile icon.
     * @param notificationToken The notification token.
     * @throws IllegalArgumentException If {@code id}, {@code name}, {@code iconUrl} or {@code notificationToken} are
     *                                  {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    public ZapicPlayer(
            @NonNull final String id,
            @NonNull final String name,
            @NonNull final URL iconUrl,
            @NonNull final String notificationToken) {
        //noinspection ConstantConditions
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        //noinspection ConstantConditions
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        //noinspection ConstantConditions
        if (iconUrl == null) {
            throw new IllegalArgumentException("iconUrl must not be null");
        }

        //noinspection ConstantConditions
        if (notificationToken == null) {
            throw new IllegalArgumentException("notificationToken must not be null");
        }

        this.iconUrl = iconUrl;
        this.id = id;
        this.name = name;
        this.notificationToken = notificationToken;
    }

    /**
     * Gets the URL of the profile icon.
     *
     * @return The URL of the profile icon.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public URL getIconUrl() {
        return this.iconUrl;
    }

    /**
     * Gets the unique identifier.
     *
     * @return The unique identifier.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public String getId() {
        return this.id;
    }

    /**
     * Gets the profile name. This is not unique and may be changed by the player at any time.
     *
     * @return The profile name.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets the notification token.
     *
     * @return The notification token.
     * @since 1.1.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public String getNotificationToken() {
        return this.notificationToken;
    }

    /**
     * Gets the unique identifier.
     *
     * @return The unique identifier.
     * @since 1.1.0
     * @deprecated Replaced with {@link #getId()}.
     */
    @AnyThread
    @CheckResult
    @Deprecated
    @NonNull
    public String getPlayerId() {
        return this.id;
    }
}
