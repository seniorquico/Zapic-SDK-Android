package com.zapic.sdk.android;

import android.support.annotation.NonNull;

/**
 * Provides constant values that identify different pages that may be shown using {@code Zapic.showPage}.
 *
 * @author Kyle Dodson
 * @since 1.3.0
 */
public final class ZapicPage {
    /**
     * Identifies a page that shows the list of challenges.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String CHALLENGE_LIST = "Challenges";

    /**
     * Identifies a page that shows the list of competitions.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String COMPETITION_LIST = "Competitions";

    /**
     * Identifies a page that shows a form to create a new challenge.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String CREATE_CHALLENGE = "CreateChallenge";

    /**
     * Identifies a page that shows a form to login.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String LOGIN = "Login";

    /**
     * Identifies a page that shows a form to logout.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String LOGOUT = "Logout";

    /**
     * Identifies a page that shows the profile.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String PROFILE = "Profile";

    /**
     * Identifies a page that shows the list of statistics.
     *
     * @since 1.3.0
     */
    @NonNull
    public static final String STAT_LIST = "Stats";

    /**
     * Prevents creating a new {@link ZapicPage} instance.
     */
    private ZapicPage() {
    }
}
