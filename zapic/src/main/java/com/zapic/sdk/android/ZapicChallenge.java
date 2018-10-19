package com.zapic.sdk.android;

import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Represents a Zapic challenge.
 *
 * @author Kyle Dodson
 * @since 1.3.0
 */
public final class ZapicChallenge {
    /**
     * A value indicating whether the challenge is active.
     */
    private final boolean active;

    /**
     * The description.
     */
    @Nullable
    private final String description;

    /**
     * The end date and time in UTC.
     */
    @NonNull
    private final Date end;

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
     * The current rank.
     */
    @Nullable
    private final Long rank;

    /**
     * The current score.
     */
    @Nullable
    private final Double score;

    /**
     * The start date and time in UTC.
     */
    @NonNull
    private final Date start;

    /**
     * The current status.
     */
    @ZapicChallengeStatusDef
    private final int status;

    /**
     * The title.
     */
    @NonNull
    private final String title;

    /**
     * The total number of users that have accepted the invitation to join the challenge.
     */
    private final long totalUsers;

    /**
     * Creates a new {@link ZapicChallenge} instance.
     *
     * @param id             The unique identifier.
     * @param title          The title.
     * @param description    The description.
     * @param metadata       The custom metadata.
     * @param active         A value indicating whether the challenge is active.
     * @param start          The start date and time in UTC.
     * @param end            The end date and time in UTC.
     * @param totalUsers     The total number of users that have accepted the invitation to join the
     *                       challenge.
     * @param status         The current status.
     * @param score          The current score.
     * @param formattedScore The current score as a formatted string.
     * @param rank           The current rank.
     * @throws IllegalArgumentException If {@code id}, {@code title}, {@code start}, or {@code end} are {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    public ZapicChallenge(
            @NonNull final String id,
            @NonNull final String title,
            @Nullable final String description,
            @Nullable final String metadata,
            final boolean active,
            @NonNull final Date start,
            @NonNull final Date end,
            final long totalUsers,
            @ZapicChallengeStatusDef final int status,
            @Nullable final Double score,
            @Nullable final String formattedScore,
            @Nullable final Long rank) {
        //noinspection ConstantConditions
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        //noinspection ConstantConditions
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }

        //noinspection ConstantConditions
        if (start == null) {
            throw new IllegalArgumentException("start must not be null");
        }

        //noinspection ConstantConditions
        if (end == null) {
            throw new IllegalArgumentException("end must not be null");
        }

        this.active = active;
        this.description = description;
        this.end = end;
        this.formattedScore = formattedScore;
        this.id = id;
        this.metadata = metadata;
        this.rank = rank;
        this.score = score;
        this.start = start;
        this.status = status;
        this.title = title;
        this.totalUsers = totalUsers;
    }

    /**
     * Gets a value indicating whether the challenge is active.
     * <p>
     * This is {@code true} when the current date and time is between the start and end dates and times. This value
     * should be used instead of comparing the current date and time to the start and end dates and times to mitigate
     * issues with client-server clock skew.
     *
     * @return {@code true} if the challenge is active; {@code false} if the challenge is not active.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    public boolean getActive() {
        return this.active;
    }

    /**
     * Gets the description.
     * <p>
     * This is used as a longer, detailed description of the challenge.
     *
     * @return The description or {@code null} if it has not been defined.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the end date and time in UTC.
     *
     * @return The end date and time in UTC.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public Date getEnd() {
        return this.end;
    }

    /**
     * Gets the current score as a formatted string.
     *
     * @return The current score as a formatted string or {@code null} if the player has not accepted the invitation to
     * join the challenge or has not submitted an applicable event to generate a score.
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
     * Gets the current rank.
     *
     * @return The current rank or {@code null} if the player has not accepted the invitation to join the challenge.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public Long getRank() {
        return this.rank;
    }

    /**
     * Gets the current score.
     *
     * @return The current score or {@code null} if the player has not accepted the invitation to join the challenge or
     * has not submitted an applicable event to generate a score.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @Nullable
    public Double getScore() {
        return this.score;
    }

    /**
     * Gets the start date and time in UTC.
     *
     * @return The start date and time in UTC.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @NonNull
    public Date getStart() {
        return this.start;
    }

    /**
     * Gets the current status.
     * <p>
     * This will be equal to {@link ZapicChallengeStatus#INVITED}, {@link ZapicChallengeStatus#IGNORED}, or
     * {@link ZapicChallengeStatus#ACCEPTED}.
     *
     * @return The current status.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    @ZapicChallengeStatusDef
    public int getStatus() {
        return this.status;
    }

    /**
     * Gets the title.
     * <p>
     * This is used as a shorter, summary description of the challenge.
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

    /**
     * Gets the total number of users that have accepted the invitation to join the challenge.
     *
     * @return The total number of users that have accepted the invitation to join the challenge.
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    public long getTotalUsers() {
        return this.totalUsers;
    }
}
