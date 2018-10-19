//package com.zapic.sdk.android;
//
//import android.app.Activity;
//import android.app.ActivityOptions;
//import android.app.Application;
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Looper;
//import android.support.annotation.AnyThread;
//import android.support.annotation.CheckResult;
//import android.support.annotation.MainThread;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.webkit.WebView;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
///**
// * Provides static methods to manage and interact with Zapic.
// * <p>
// * It is the responsibility of the game's {@link Application} to call the {@link #start(Context)}
// * method during its {@code onCreate} lifecycle method. The game may optionally register an
// * authentication handler that is notified when a player is logged in or out.
// * <p>
// * It is the responsibility of the game's activity (or game's activities if there are multiple) to
// * call the {@link #attachFragment(Activity)} method during its {@code onCreate} lifecycle method.
// * This method creates and attaches a non-UI fragment, {@link ZapicFrameworkFragment} or
// * {@link ZapicSupportFragment} (depending on the game's activity type), to the activity. The non-UI
// * fragment downloads and runs the Zapic web page in the background. If a game's activity does
// * <i>not</i> call {@link #attachFragment(Activity)}, the Zapic web page may be garbage collected
// * and the current player's session reset. It is also the responsibility of the game's activity to
// * call the {@link #showDefaultPage(Activity)} method after the player interacts with a
// * Zapic-branded button visible from the game's main menu (requirements are outlined in the
// * <a href="https://www.zapic.com/terms/">Terms of Use</a>). The game may optionally call the
// * {@link #showPage(Activity, String)} method to deep link to Zapic features or content. The
// * supported page constants are available in {@link ZapicPages}.
// * <p>
// * The Zapic web page runs in a {@link WebView}. Generally, the {@link WebView} runs for the
// * lifetime of the Android application. While the game is in focus, the {@link WebView} runs in the
// * background (managed by one or more non-UI fragments attached to the game's activities). The Zapic
// * web page processes events and receives notifications while running in the background. When the
// * player shifts focus to Zapic, the {@link WebView} moves to the foreground and is presented by a
// * {@link ZapicActivity}. When the player shifts focus back to the game, the {@link ZapicActivity}
// * finishes and the {@link WebView} returns to the background (again managed by one or more non-UI
// * fragments attached to the game's activities).
// *
// * @author Kyle Dodson
// * @since 1.0.0
// */
//public final class Zapic {
//    /**
//     * The name of the OneSignal push notification tag.
//     */
//    @SuppressWarnings("unused")
//    public static final String NOTIFICATION_TAG = "zapic_player_token";
//
//    /**
//     * A synchronization lock for {@code sInstance} writes.
//     */
//    @NonNull
//    private static final Object INSTANCE_LOCK = new Object();
//
//    /**
//     * The tag used to identify log messages.
//     */
//    @NonNull
//    private static final String TAG = "Zapic";
//
//    /**
//     * The {@link Zapic} instance.
//     */
//    @Nullable
//    private static volatile Zapic sInstance = null;
//
//    /**
//     * The {@link SessionManager} instance.
//     */
//    @NonNull
//    private final SessionManager mSessionManager;
//
//    /**
//     * The {@link ViewManager} instance.
//     */
//    @NonNull
//    private final ViewManager mViewManager;
//
//    /**
//     * The {@link WebViewManager} instance.
//     */
//    @NonNull
//    private final WebViewManager mWebViewManager;
//
//    /**
//     * Creates a new {@link Zapic} instance.
//     * <p>
//     * <b>This constructor must be invoked on the UI thread.</b>
//     *
//     * @param context Any context object (e.g. the global {@link Application} or an
//     *                {@link Activity}).
//     */
//    @MainThread
//    private Zapic(@NonNull final Context context) {
//        mSessionManager = new SessionManager(context);
//        mViewManager = new ViewManager();
//        mWebViewManager = new WebViewManager(context, mSessionManager, mViewManager);
//    }
//
//    /**
//     * Attaches a special fragment to the specified activity that relays intents and notification
//     * messages.
//     * <p>
//     * This must be invoked to relay intents and notification messages. It should be invoked in all
//     * {@link Activity#onCreate(Bundle)} lifecycle event callbacks.
//     * <p>
//     * <b>This method must be invoked on the UI thread.</b>
//     *
//     * @param activity The activity.
//     * @throws IllegalArgumentException    If {@code activity} is {@code null}.
//     * @throws IllegalThreadStateException If not invoked on the UI thread.
//     */
//    @MainThread
//    @SuppressWarnings({"unused", "WeakerAccess"})
//    public static void attachFragment(@Nullable final Activity activity) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "attachFragment");
//        }
//
//        if (activity == null) {
//            throw new IllegalArgumentException("activity must not be null");
//        }
//
//        ensureUIThread();
//
//        Zapic instance = Zapic.sInstance;
//        if (instance == null) {
//            start(activity);
//
//            instance = Zapic.sInstance;
//            assert instance != null : "instance is null";
//        }
//
//        instance.mViewManager.attachFragment(activity);
//    }
//
//    /**
//     * Detaches a special fragment from the specified activity that relays intents and notification
//     * messages.
//     * <p>
//     * This generally should not be invoked. The fragments are automatically detached and destroyed
//     * when the activity is destroyed.
//     * <p>
//     * <b>This method must be invoked on the UI thread.</b>
//     *
//     * @param activity The activity.
//     * @throws IllegalArgumentException    If {@code activity} is {@code null}.
//     * @throws IllegalThreadStateException If not invoked on the UI thread.
//     */
//    @MainThread
//    @SuppressWarnings({"unused", "WeakerAccess"})
//    public static void detachFragment(@Nullable final Activity activity) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "detachFragment");
//        }
//
//        if (activity == null) {
//            throw new IllegalArgumentException("activity must not be null");
//        }
//
//        ensureUIThread();
//
//        Zapic instance = Zapic.sInstance;
//        if (instance == null) {
//            start(activity);
//
//            instance = Zapic.sInstance;
//            assert instance != null : "instance is null";
//        }
//
//        instance.mViewManager.detachFragment(activity);
//    }
//
//    /**
//     * Gets the {@link SessionManager}, {@link ViewManager}, and {@link WebViewManager} instances.
//     * <p>
//     * <b>This method must be invoked on the UI thread.</b>
//     *
//     * @param context Any context object (e.g. the global {@link Application} or an
//     *                {@link Activity}).
//     * @return The {@link SessionManager}, {@link ViewManager}, and {@link WebViewManager}
//     * instances.
//     * @throws IllegalArgumentException    If {@code context} is {@code null}.
//     * @throws IllegalThreadStateException If not invoked on the UI thread.
//     */
//    @MainThread
//    @NonNull
//    static Managers onAttachedFragment(@Nullable final Context context) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "onAttachedFragment");
//        }
//
//        if (context == null) {
//            throw new IllegalArgumentException("context must not be null");
//        }
//
//        ensureUIThread();
//
//        Zapic instance = Zapic.sInstance;
//        if (instance == null) {
//            start(context);
//
//            instance = Zapic.sInstance;
//            assert instance != null : "instance is null";
//        }
//
//        return new Managers(instance.mSessionManager, instance.mViewManager, instance.mWebViewManager);
//    }
//
//    /**
//     * Starts Zapic.
//     * <p>
//     * This must be invoked once to start Zapic. It should be invoked in the
//     * {@link Application#onCreate()} lifecycle event callback. Most {@link Zapic} methods will
//     * throw an {@link IllegalStateException} if invoked prior to {@code start}. This method is
//     * idempotent and may be invoked multiple times (for example, in all
//     * {@link Activity#onCreate(Bundle)} lifecycle event callbacks if it cannot be invoked in the
//     * {@link Application#onCreate()} lifecycle event callback).
//     * <p>
//     * <b>This method must be invoked on the UI thread.</b>
//     *
//     * @param context Any context object (e.g. the global {@link Application} or an
//     *                {@link Activity}).
//     * @throws IllegalArgumentException    If {@code context} is {@code null}.
//     * @throws IllegalThreadStateException If not invoked on the UI thread.
//     */
//    @MainThread
//    @SuppressWarnings({"unused", "WeakerAccess"})
//    public static void start(@Nullable final Context context) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "start");
//        }
//
//        if (context == null) {
//            throw new IllegalArgumentException("context must not be null");
//        }
//
//        ensureUIThread();
//
//        if (sInstance == null) {
//            synchronized (INSTANCE_LOCK) {
//                if (BuildConfig.DEBUG) {
//                    // This enables WebView debugging for all WebViews in the current process. Changes
//                    // to this value are accepted only before the WebView process is created.
//                    WebView.setWebContentsDebuggingEnabled(true);
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//                    // This marks "zapic.net" as a safe domain for all WebViews in the current process.
//                    // Changes to this value are accepted only before the WebView process is created.
//                    WebView.setSafeBrowsingWhitelist(Collections.singletonList("zapic.net"), null);
//                }
//
//                if (sInstance == null) {
//                    Log.i(TAG, String.format("Starting Zapic %s (%s)", BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE));
//                    final Zapic zapic = new Zapic(context);
//                    sInstance = zapic;
//                    zapic.mWebViewManager.start();
//                }
//            }
//        }
//    }
//
//    /**
//     * Ensures the UI thread is the current thread.
//     *
//     * @throws IllegalThreadStateException If not invoked on the UI thread.
//     */
//    @AnyThread
//    private static void ensureUIThread() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Looper.getMainLooper().isCurrentThread()) {
//                throw new IllegalThreadStateException("start must be invoked on the UI thread");
//            }
//        } else {
//            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
//                throw new IllegalThreadStateException("start must be invoked on the UI thread");
//            }
//        }
//    }
//}
