package com.zapic.sdk.android;

/**
 * Provides constant values that identify the status of the player in a Zapic challenge.
 *
 * @author Kyle Dodson
 * @since 1.3.0
 */
public final class ZapicChallengeStatus {
    /**
     * Identifies when the player has received an invitation to join the challenge.
     *
     * @since 1.3.0
     */
    public static final int INVITED = 0;

    /**
     * Identifies when the player has ignored an invitation to join the challenge.
     *
     * @since 1.3.0
     */
    public static final int IGNORED = 1;

    /**
     * Identifies when the player has accepted an invitation to join the challenge.
     *
     * @since 1.3.0
     */
    public static final int ACCEPTED = 2;

    /**
     * Prevents creating a new {@link ZapicChallengeStatus} instance.
     */
    private ZapicChallengeStatus() {
    }
}
