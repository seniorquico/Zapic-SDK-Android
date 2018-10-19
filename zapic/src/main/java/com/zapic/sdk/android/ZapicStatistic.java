package com.zapic.sdk.android;

import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents a Zapic statistic.
 *
 * @author Kyle Dodson
 * @since 1.3.0
 */
public final class ZapicStatistic {
    /**
     * The current score as a formatted string.
     */
    @Nullable
    private final String formattedScore;

    /**
     * The unique identifier.
     */
    @NonNull
    private final String id;

    /**
     * The custom metadata.
     */
    @Nullable
    private final String metadata;

    /**
     * The percentile of the current score.
     */
    @Nullable
    private final Integer percentile;

    /**
     * The current score.
     */
    @Nullable
    private final Double score;

    /**
     * The title.
     */
    @NonNull
    private final String title;

    /**
     * Creates a new {@link ZapicStatistic} instance.
     *
     * @param id             The unique identifier.
     * @param title          The title.
     * @param metadata       The custom metadata.
     * @param score          The current score.
     * @param formattedScore The current score as a formatted string.
     * @param percentile     The percentile of the current score.
     * @throws IllegalArgumentException If {@code id} or {@code title} are {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    public ZapicStatistic(
            @NonNull final String id,
            @NonNull final String title,
            @Nullable final String metadata,
            @Nullable final Double score,
            @Nullable final String formattedScore,
            @Nullable final Integer percentile) {
        //noinspection ConstantConditions
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        //noinspection ConstantConditions
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }

        this.formattedScore = formattedScore;
        this.id = id;
        this.metadata = metadata;
        this.percentile = percentile;
        this.score = score;
        this.title = title;
    }

    /**
     * Gets the current score as a formatted string.
     *
     * @return The current score as a formatted string or {@code null} if the player has not submitted an applicable
     * event to generate a score.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public String getFormattedScore() {
        return this.formattedScore;
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
     * Gets the custom metadata.
     *
     * @return The custom metadata or {@code null} if it has not been defined.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public String getMetadata() {
        return this.metadata;
    }

    /**
     * Gets the percentile of the current score.
     *
     * @return The percentile of the current score or {@code null} if the player has not submitted an applicable event
     * to generate a score.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public Integer getPercentile() {
        return this.percentile;
    }

    /**
     * Gets the current score.
     *
     * @return The current score or {@code null} if the player has not submitted an applicable event to generate a
     * score.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public Double getScore() {
        return this.score;
    }

    /**
     * Gets the title.
     * <p>
     * This is used as a shorter, summary description of the statistic.
     *
     * @return The title.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public String getTitle() {
        return this.title;
    }
}
