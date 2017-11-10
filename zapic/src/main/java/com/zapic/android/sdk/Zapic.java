package com.zapic.android.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provides static methods to start and interact with Zapic.
 *
 * @author Kyle Dodson
 * @since 1.0
 */
public final class Zapic {
    /**
     * The tag used for fragment identifiers.
     */
    @NonNull
    private static final String FRAGMENT_TAG = "ZapicClient";

    /**
     * The tag used for log messages.
     */
    @NonNull
    private static final String TAG = "Zapic";

    /**
     * The web view manager handles the lifecycle of starting and stopping the web client and
     * provides a communication bridge between the Java and JavaScript application contexts.
     * <p>
     * The suppressed "StaticFieldLeak" warning identifies the {@see android.webkit.WebView}
     * reference as a potential memory leak. This should be safe to suppress as the web view is tied
     * to the application context and the lifecycle management based on activity and fragment
     * reference counting.
     */
    @Nullable
    @SuppressLint("StaticFieldLeak")
    private static volatile WebViewManager webViewManager = null;

    /**
     * Prevents creating a new instance.
     */
    private Zapic() {
    }

    /**
     * Starts the Zapic client.
     *
     * @param parentActivity The parent activity.
     * @param appVersion     The app version.
     * @throws IllegalArgumentException If {@code parentActivity} or {@code version} are
     *                                  {@code null}.
     */
    public static void start(@Nullable Activity parentActivity, @Nullable String appVersion) {
        if (parentActivity == null) {
            throw new IllegalArgumentException("parentActivity must not be null");
        }

        if (appVersion == null) {
            throw new IllegalArgumentException("version must not be null");
        }

        if (appVersion.length() == 0) {
            throw new IllegalArgumentException("version must not be empty");
        }

        if (Zapic.webViewManager != null) {
            throw new IllegalStateException("start must not be called more than once");
        }

        Zapic.webViewManager = new WebViewManager(appVersion);

        final Fragment fragment = parentActivity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            FragmentTransaction transaction = parentActivity.getFragmentManager().beginTransaction();
            transaction.add(new ZapicFragment(), FRAGMENT_TAG);
            transaction.commit();
        }
    }

    public static void show(@Nullable Activity parentActivity, @Nullable String page) {
        if (parentActivity == null) {
            throw new IllegalArgumentException("parentActivity must not be null");
        }

        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }

        if (Zapic.webViewManager == null) {
            throw new IllegalStateException("Zapic.start() must be called before show may be called");
        }

        final Intent openIntent = new Intent(parentActivity, ZapicActivity.class);
        openIntent.putExtra("PAGE", page);
        parentActivity.startActivity(openIntent);
    }

    public static void submitEvent(@Nullable String payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload must not be null");
        }

        // TODO: Dispatch event.
        // webViewBridge.dispatchMessage("{ type: 'SUBMIT_EVENT', payload: JSON.parse('" + param + "') }");
    }

    @Nullable
    static WebViewManager getWebViewManager() {
        return Zapic.webViewManager;
    }
}
