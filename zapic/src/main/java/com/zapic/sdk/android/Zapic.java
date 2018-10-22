package com.zapic.sdk.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.Future;

public final class Zapic {
    /**
     * The name of the OneSignal push notification tag.
     *
     * @since 1.2.1
     */
    @NonNull
    public static final String NOTIFICATION_TAG = "zapic_player_token";

    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "Zapic";

    /**
     * A synchronization lock for the static field {@link #bridge}.
     */
    @NonNull
    private static final Object bridgeLock = new Object();

    /**
     * The JavaScript bridge.
     */
    @Nullable
    private static JavaScriptBridge bridge;

    /**
     * Prevents creating a new {@link Zapic} instance.
     */
    private Zapic() {
    }

    /**
     * Attaches a special fragment to the specified activity that relays intents and notification messages.
     * <p>
     * This must be invoked to relay intents and notification messages. It should be invoked in all
     * {@link Activity#onCreate(Bundle)} lifecycle event callbacks.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param activity The activity.
     * @throws IllegalArgumentException If {@code activity} is {@code null}.
     */
    @AnyThread
    public static void attachFragment(@NonNull final Activity activity) {
        ZapicLog.v(Zapic.TAG, "attachFragment");
    }

    /**
     * Detaches a special fragment from the specified activity that relays intents and notification messages.
     * <p>
     * This generally should not be invoked. The fragments are automatically detached and destroyed when the activity is
     * destroyed.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param activity The activity.
     * @throws IllegalArgumentException If {@code activity} is {@code null}.
     */
    @AnyThread
    public static void detachFragment(@NonNull final Activity activity) {
        ZapicLog.v(Zapic.TAG, "detachFragment");
    }

    /**
     * Asynchronously gets the list of challenges for the player.
     * <p>
     * This method may be invoked on any thread. The result callback will be invoked on the application's main thread
     * when the asynchronous operation has completed.
     *
     * @param callback The result callback.
     * @return A future that represents the asynchronous operation. The future may be used to check the status of or
     * cancel the asynchronous operation.
     * @throws IllegalArgumentException If {@code callback} is {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    @NonNull
    public static Future<?> getChallenges(@NonNull final ZapicQueryCallback<ZapicChallenge[]> callback) {
        ZapicLog.v(Zapic.TAG, "getChallenges");

        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }

        return Zapic.getBridge().getChallenges(callback);
    }

    /**
     * Asynchronously gets the list of competitions for the player.
     * <p>
     * This method may be invoked on any thread. The result callback will be invoked on the application's main thread
     * when the asynchronous operation has completed.
     *
     * @param callback The result callback.
     * @return A future that represents the asynchronous operation. The future may be used to check the status of or
     * cancel the asynchronous operation.
     * @throws IllegalArgumentException If {@code callback} is {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    @NonNull
    public static Future<?> getCompetitions(@NonNull final ZapicQueryCallback<ZapicCompetition[]> callback) {
        ZapicLog.v(Zapic.TAG, "getCompetitions");

        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }

        return Zapic.getBridge().getCompetitions(callback);
    }

    /**
     * Gets the current player.
     * <p>
     * This method may be invoked on any thread.
     *
     * @return The current player or {@code null} if the current player has not logged in.
     * @since 1.2.0
     * @deprecated This method has been replaced with {@link #getPlayer(ZapicQueryCallback)}.
     */
    @AnyThread
    @CheckResult
    @Deprecated
    @Nullable
    public static ZapicPlayer getCurrentPlayer() {
        ZapicLog.v(Zapic.TAG, "getCurrentPlayer");

        return Zapic.getBridge().getCachedPlayer();
    }

    /**
     * Asynchronously gets the player.
     * <p>
     * This method may be invoked on any thread. The result callback will be invoked on the application's main thread
     * when the asynchronous operation has completed.
     *
     * @param callback The result callback.
     * @return A future that represents the asynchronous operation. The future may be used to check the status of or
     * cancel the asynchronous operation.
     * @throws IllegalArgumentException If {@code callback} is {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    @NonNull
    public static Future<?> getPlayer(@NonNull final ZapicQueryCallback<ZapicPlayer> callback) {
        ZapicLog.v(Zapic.TAG, "getPlayer");

        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }

        return Zapic.getBridge().getPlayer(callback);
    }

    /**
     * Asynchronously gets the list of statistics for the player.
     * <p>
     * This method may be invoked on any thread. The result callback will be invoked on the application's main thread
     * when the asynchronous operation has completed.
     *
     * @param callback The result callback.
     * @return A future that represents the asynchronous operation. The future may be used to check the status of or
     * cancel the asynchronous operation.
     * @throws IllegalArgumentException If {@code callback} is {@code null}.
     * @since 1.3.0
     */
    @AnyThread
    @NonNull
    public static Future<?> getStatistics(@NonNull final ZapicQueryCallback<ZapicStatistic[]> callback) {
        ZapicLog.v(Zapic.TAG, "getStatistics");

        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }

        return Zapic.getBridge().getStatistics(callback);
    }

    /**
     * Handles an interaction event. Depending on the event parameters, Zapic may open and show contextual information.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param parameters The event parameters.
     * @throws IllegalArgumentException If {@code parameters} is {@code null} or contains a value that is not a boolean,
     *                                  number, or string.
     * @since 1.2.0
     */
    @AnyThread
    public static void handleInteraction(@NonNull final JSONObject parameters) {
        ZapicLog.v(Zapic.TAG, "handleInteraction");

        //noinspection ConstantConditions
        if (parameters == null) {
            throw new IllegalArgumentException("parameters must not be null");
        }

        try {
            final JSONObject parsedParameters = new JSONObject(parameters.toString());
            Zapic.handleEvent("interaction", parsedParameters);
        } catch (JSONException e) {
            throw new IllegalArgumentException("parameters must be a valid JSON object");
        }
    }

    /**
     * Handles an interaction event. Depending on the event parameters, Zapic may open and show contextual information.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param parameters The event parameters.
     * @throws IllegalArgumentException If {@code parameters} is {@code null}, not a valid JSON object, or contains a
     *                                  value that is not a boolean, number, or string.
     * @since 1.2.0
     */
    @AnyThread
    public static void handleInteraction(@NonNull final String parameters) {
        ZapicLog.v(Zapic.TAG, "handleInteraction");

        //noinspection ConstantConditions
        if (parameters == null) {
            throw new IllegalArgumentException("parameters must not be null");
        }

        try {
            final JSONObject parsedParameters = new JSONObject(parameters);
            Zapic.handleEvent("interaction", parsedParameters);
        } catch (JSONException e) {
            throw new IllegalArgumentException("parameters must be a valid JSON object");
        }
    }

    /**
     * Sets the authentication handler that is notified after a player has logged in or logged out.
     * <p>
     * The authentication handler will be immediately invoked with a {@link ZapicPlayer} instance if the current player
     * has already logged in.
     * <p>
     * This method may be invoked on any thread. The authentication handler will be invoked on the application's main
     * thread after a player has logged in or logged out.
     *
     * @param authenticationHandler The authentication handler that is notified after a player has logged in or logged
     *                              out. This may be {@code null} to unsubscribe a previous authentication handler.
     * @since 1.2.0
     */
    @AnyThread
    public static void setPlayerAuthenticationHandler(
            @Nullable final ZapicPlayerAuthenticationHandler authenticationHandler) {
        ZapicLog.v(Zapic.TAG, "setPlayerAuthenticationHandler");

        Zapic.getBridge().setPlayerAuthenticationHandler(authenticationHandler);
    }

    /**
     * Opens Zapic and shows the default page.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param activity The {@link Activity} over which Zapic will be shown.
     * @throws IllegalArgumentException If {@code activity} is {@code null}.
     * @since 1.2.0
     */
    @AnyThread
    public static void showDefaultPage(@NonNull final Activity activity) {
        ZapicLog.v(Zapic.TAG, "showDefaultPage");

        //noinspection ConstantConditions
        if (activity == null) {
            throw new IllegalArgumentException("activity must not be null");
        }

        // TODO: Start activity.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.startActivity(ZapicActivity.createIntent(activity, "default"), ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
//        } else {
//            activity.startActivity(ZapicActivity.createIntent(activity, "default"));
//        }
    }

    /**
     * Opens Zapic and shows the specified page.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param activity The {@link Activity} over which Zapic will be shown.
     * @param page     The page to show.
     * @throws IllegalArgumentException If {@code activity} or {@code page} are {@code null}.
     * @since 1.2.0
     */
    @AnyThread
    public static void showPage(@NonNull final Activity activity, @NonNull @ZapicPageDef final String page) {
        ZapicLog.v(Zapic.TAG, "showPage");

        //noinspection ConstantConditions
        if (activity == null) {
            throw new IllegalArgumentException("activity must not be null");
        }

        //noinspection ConstantConditions
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }

        // TODO: Start activity.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.startActivity(ZapicActivity.createIntent(activity, page), ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
//        } else {
//            activity.startActivity(ZapicActivity.createIntent(activity, page));
//        }
    }

    /**
     * Starts Zapic.
     * <p>
     * This must be invoked once to start Zapic. It should be invoked in the {@link Application#onCreate()} lifecycle
     * event callback. Alternatively, it may be invoked in the first {@link Activity#onCreate} lifecycle event callback.
     * There is no effect if invoked multiple times.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param context Any context object (e.g. the global {@link Application} or an {@link Activity}).
     * @throws IllegalArgumentException If {@code context} is {@code null}.
     * @since 1.2.0
     */
    @AnyThread
    public static void start(@NonNull final Context context) {
        ZapicLog.v(Zapic.TAG, "start");

        //noinspection ConstantConditions
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }

        try {
            Application application = (Application) context;
            application.registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
        } catch (ClassCastException ignored) {
        }

        final JavaScriptBridge bridge = Zapic.getBridge();
    }

    /**
     * Handles a gameplay event.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param parameters The event parameters.
     * @throws IllegalArgumentException If {@code parameters} is {@code null} or contains a value that is not a boolean,
     *                                  number, or string.
     * @since 1.2.0
     */
    @AnyThread
    public static void submitEvent(@NonNull final JSONObject parameters) {
        ZapicLog.v(Zapic.TAG, "submitEvent");

        //noinspection ConstantConditions
        if (parameters == null) {
            throw new IllegalArgumentException("parameters must not be null");
        }

        try {
            final JSONObject parsedParameters = new JSONObject(parameters.toString());
            Zapic.handleEvent("gameplay", parsedParameters);
        } catch (JSONException e) {
            throw new IllegalArgumentException("parameters must be a valid JSON object");
        }
    }

    /**
     * Handles a gameplay event.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param parameters The event parameters as a JSON-encoded string.
     * @throws IllegalArgumentException If {@code parameters} is {@code null}, not a valid JSON object, or contains a
     *                                  value that is not a boolean, number, or string.
     * @since 1.2.0
     */
    @AnyThread
    public static void submitEvent(@NonNull final String parameters) {
        ZapicLog.v(Zapic.TAG, "submitEvent");

        //noinspection ConstantConditions
        if (parameters == null) {
            throw new IllegalArgumentException("parameters must not be null");
        }

        try {
            final JSONObject parsedParameters = new JSONObject(parameters);
            Zapic.handleEvent("gameplay", parsedParameters);
        } catch (JSONException e) {
            throw new IllegalArgumentException("parameters must be a valid JSON object");
        }
    }

    /**
     * Gets the JavaScript bridge.
     * <p>
     * This method may be invoked on any thread.
     *
     * @return The JavaScript bridge.
     */
    @AnyThread
    @CheckResult
    @NonNull
    static JavaScriptBridge getBridge() {
        JavaScriptBridge bridge = Zapic.bridge;
        if (bridge == null) {
            synchronized (Zapic.bridgeLock) {
                bridge = Zapic.bridge;
                if (bridge == null) {
                    // TODO: Connect bridge to WebView.
                    bridge = new JavaScriptBridge();
                }
            }
        }

        return bridge;
    }

    /**
     * Handles a gameplay or interaction event.
     * <p>
     * This method may be invoked on any thread.
     *
     * @param type       The event type.
     * @param parameters The event parameters.
     * @throws IllegalArgumentException If {@code type} is not "gameplay" or "interaction" or if {@code parameters}
     *                                  contains a value that is not a boolean, number, or string.
     */
    @AnyThread
    private static void handleEvent(@NonNull final String type, @NonNull final JSONObject parameters) {
        if (parameters.length() < 1) {
            return;
        }

        final Iterator<String> keys = parameters.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            Object value;
            try {
                value = parameters.get(key);
            } catch (JSONException ignored) {
                value = null;
            }

            if (!(value instanceof Integer || value instanceof Long || value instanceof Float ||
                    value instanceof Double || value instanceof Boolean || value instanceof String)) {
                throw new IllegalArgumentException(
                        String.format("parameter \"%s\" does not have a boolean, number, or string value", key));
            }
        }

        try {
            final JSONObject event = new JSONObject()
                    .put("type", type)
                    .put("params", parameters);
            Zapic.getBridge().handleEvent(event);
        } catch (JSONException ignored) {
        }
    }
}
