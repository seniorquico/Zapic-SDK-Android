package com.zapic.sdk.android;

import android.support.annotation.NonNull;

/**
 * Provides constant values that identify different pages that may be shown using {@code Zapic.showPage}.
 *
 * @author Kyle Dodson
 * @since 1.2.0
 * @deprecated Replaced with {@link ZapicPage}.
 */
public final class ZapicPages {
    /**
     * Identifies a page that shows the list of challenges.
     *
     * @since 1.2.0
     */
    @NonNull
    public static final String CHALLENGE_LIST = ZapicPage.CHALLENGE_LIST;

    /**
     * Identifies a page that shows a form to create a new challenge.
     *
     * @since 1.2.0
     */
    @NonNull
    public static final String CREATE_CHALLENGE = ZapicPage.CREATE_CHALLENGE;

    /**
     * Identifies a page that shows the profile.
     *
     * @since 1.2.0
     */
    @NonNull
    public static final String PROFILE = ZapicPage.PROFILE;

    /**
     * Identifies a page that shows the list of statistics.
     *
     * @since 1.2.0
     */
    @NonNull
    public static final String STAT_LIST = ZapicPage.STAT_LIST;

    /**
     * Prevents creating a new {@link ZapicPages} instance.
     */
    private ZapicPages() {
    }
}
