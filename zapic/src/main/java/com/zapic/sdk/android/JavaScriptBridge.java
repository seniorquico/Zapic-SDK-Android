package com.zapic.sdk.android;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.*;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Future;

final class JavaScriptBridge {
    private static final int DISPATCH = 100;

    private static final int HANDLE = 101;

    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "JavascriptBridge";

    @NonNull
    private final Handler workerHandler;

    @NonNull
    private final Looper workerLooper;

    @NonNull
    private final JavaScriptBridge.Dispatcher dispatcher;

    @NonNull
    private final Handler mainHandler;

    @NonNull
    private final Looper mainLooper;

    JavaScriptBridge(@NonNull final JavaScriptBridge.Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        this.mainLooper = Looper.getMainLooper();
        this.mainHandler = new Handler(this.mainLooper) {
            @MainThread
            @Override
            public void handleMessage(Message msg) {
                if (msg == null) {
                    ZapicLog.e(TAG, "Received invalid message: null");
                    return;
                }

                switch (msg.what) {
                    case 1:
                        Result result = (Result)msg.obj;
                        result.callback.onComplete(result.result, result.error);
                        result.callback = null;
                        msg.obj = null;
                        break;
                    case DISPATCH:
                        try {
                            JavaScriptBridge.this.dispatcher.dispatch((String) msg.obj);
                        } catch (ZapicException e) {
                            ZapicLog.e(TAG, e, "Error!");
                        }

                        break;
                    default:
                        ZapicLog.e(TAG, "Received invalid message: %d", msg.what);
                        break;
                }
            }
        };

        final HandlerThread workerThread = new HandlerThread("JavascriptBridge");
        workerThread.start();

        this.workerLooper = workerThread.getLooper();
        this.workerHandler = new Handler(this.workerLooper) {
            @Override
            @WorkerThread
            public void handleMessage(Message msg) {
                if (msg == null) {
                    ZapicLog.e(TAG, "Received invalid message from JavaScript bridge: null");
                    return;
                }

                switch (msg.what) {
                    case 1:
                        Result challengesResult = new Result();
                        challengesResult.callback = (ZapicQueryCallback) msg.obj;
                        challengesResult.error = null;
                        challengesResult.result = new ZapicChallenge[0];
                        msg.obj = null;
                        JavaScriptBridge.this.mainHandler.obtainMessage(1, challengesResult).sendToTarget();
                        break;
                    case 2:
                        Result competitionsResult = new Result();
                        competitionsResult.callback = (ZapicQueryCallback) msg.obj;
                        competitionsResult.error = null;
                        competitionsResult.result = new ZapicCompetition[0];
                        msg.obj = null;
                        JavaScriptBridge.this.mainHandler.obtainMessage(1, competitionsResult).sendToTarget();
                        break;
                    case 3:
                        Result playerResult = new Result();
                        playerResult.callback = (ZapicQueryCallback) msg.obj;
                        playerResult.error = null;
                        playerResult.result = null;
                        msg.obj = null;
                        JavaScriptBridge.this.mainHandler.obtainMessage(1, playerResult).sendToTarget();
                        break;
                    case 4:
                        Result statisticsResult = new Result();
                        statisticsResult.callback = (ZapicQueryCallback) msg.obj;
                        statisticsResult.error = null;
                        statisticsResult.result = new ZapicStatistic[0];
                        msg.obj = null;
                        JavaScriptBridge.this.mainHandler.obtainMessage(1, statisticsResult).sendToTarget();
                        break;
                    case HANDLE:
                        final String message = (String) msg.obj;
                        final JSONObject parsedMessage;
                        try {
                            parsedMessage = new JSONObject(message);
                        } catch (JSONException e) {
                            ZapicLog.e(TAG, e, "Received invalid message from Zapic application: %s", message);
                            return;
                        }

                        JavaScriptBridge.this.onReceive(parsedMessage);
                        break;
                    default:
                        ZapicLog.e(TAG, "Received invalid message from JavaScript bridge: %d", msg.what);
                        break;
                }
            }
        };
    }

    private static class Result
    {
        ZapicQueryCallback callback;
        ZapicException error;
        Object result;
    }

    @AnyThread
    @CheckResult
    @Deprecated
    @Nullable
    ZapicPlayer getCachedPlayer() {
        return null;
    }

    @AnyThread
    Future<?> getChallenges(@NonNull final ZapicQueryCallback<ZapicChallenge[]> callback) {
        this.workerHandler.obtainMessage(1, callback).sendToTarget();
        return ImmediateFuture.getInstance();
    }

    @AnyThread
    Future<?> getCompetitions(@NonNull final ZapicQueryCallback<ZapicCompetition[]> callback) {
        this.workerHandler.obtainMessage(2, callback).sendToTarget();
        return ImmediateFuture.getInstance();
    }

    @AnyThread
    Future<?> getPlayer(@NonNull final ZapicQueryCallback<ZapicPlayer> callback) {
        this.workerHandler.obtainMessage(3, callback).sendToTarget();
        return ImmediateFuture.getInstance();
    }

    @AnyThread
    Future<?> getStatistics(@NonNull final ZapicQueryCallback<ZapicStatistic[]> callback) {
        this.workerHandler.obtainMessage(4, callback).sendToTarget();
        return ImmediateFuture.getInstance();
    }

    void handleEvent(@NonNull final JSONObject event) {
    }

    @AnyThread
    void setPlayerAuthenticationHandler(@NonNull final ZapicPlayerAuthenticationHandler handler) {
    }

//    private final HashMap<UUID, ZapicCallback<ZapicChallenge[]>> getChallengesRequests = new HashMap<>();
//
//    @WorkerThread
//    private void getChallengesDispatched(@NonNull final ZapicCallback<ZapicChallenge[]> callback) {
//        if (!this.getChallengesRequests.isEmpty()) {
//            ZapicLog.w(JavaScriptBridge.TAG, "Concurrent requests dispatched for the list of challenges");
//        }
//
//        try {
//            final UUID requestId = UUID.randomUUID();
//            final String action = new JSONObject()
//                    .put("type", "query")
//                    .put("payload", new JSONObject()
//                            .put("requestId", requestId.toString())
//                            .put("dataType", "challenges")
//                            .put("dataTypeVersion", 1))
//                    .toString();
//            this.getChallengesRequests.put(requestId, callback);
//            this.mainHandler.obtainMessage(JavaScriptBridge.CHALLENGE_LIST_QUERY, action).sendToTarget();
//        } catch (JSONException e) {
//            this.mainHandler.obtainMessage(JavaScriptBridge.INVALID_QUERY, new ZapicException(
//                    ZapicException.ErrorCode.INVALID_QUERY,
//                    "Failed to encode action",
//                    e))
//            final ZapicException error = ;
//        }
//
//        try {
//            this.dispatcher.dispatch(action);
//            return task;
//        } catch (ZapicException error) {
//            callback.onComplete(null, error);
//            return ImmediateFuture.getInstance();
//        }
//    }

//    private <Data> void completeWithData(@NonNull final ZapicCallback<Data> callback, @NonNull final Data data) {
//
//
//        if (isMainThread) {
//            callback.onComplete(data, null);
//        } else {
//            this.mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    callback.onComplete(data, null);
//                }
//            });
//        }
//    }
//
//    private void completeWithError(@NonNull final ZapicCallback<?> callback, @NonNull final ZapicException error) {
//        final boolean isMainThread;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            isMainThread = mainLooper.isCurrentThread();
//        } else {
//            isMainThread = Thread.currentThread() == mainLooper.getThread();
//        }
//
//        if (isMainThread) {
//            callback.onComplete(null, error);
//        } else {
//            this.mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    callback.onComplete(null, error);
//                }
//            });
//        }
//    }

    @WorkerThread
    private void onReceive(@NonNull final JSONObject message) {
        final String action;
        try {
            action = message.getString("action");
        } catch (JSONException e) {
            ZapicLog.e(TAG, e, "Received invalid message from Zapic application: %s", message);
            return;
        }

        switch (action) {
            case "":
                break;
            default:
                ZapicLog.e(TAG, "Received invalid message from Zapic application: %s", message);
                break;
        }
    }

    /**
     * Dispatches an action to the Zapic SDK from the Zapic application.
     *
     * @param action The action.
     */
    @JavascriptInterface
    @WorkerThread
    public void dispatch(@Nullable final String action) {
        if (action == null) {
            ZapicLog.e(TAG, "Received invalid action from Zapic application: null");
            return;
        }

        if (action.isEmpty()) {
            ZapicLog.e(TAG, "Received invalid action from Zapic application: \"\"");
            return;
        }

        JavaScriptBridge.this.workerHandler.obtainMessage(HANDLE, action).sendToTarget();
    }

    /**
     * Represents a method that dispatches a message to the Zapic application.
     */
    interface Dispatcher {
        /**
         * Dispatches an action to the Zapic application from the Zapic SDK.
         *
         * @param action The action.
         * @throws ZapicException If a synchronous error occurs dispatching {@code action} to the
         *                        Zapic application.
         */
        @MainThread
        void dispatch(@NonNull String action) throws ZapicException;
    }
}
