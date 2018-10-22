package com.zapic.sdk.android;

import android.content.Context;
import android.net.Uri;
import android.os.*;
import android.support.annotation.*;
import android.webkit.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Future;

final class JavaScriptBridge {
    private static final int DISPATCH = 100;

    private static final int DISPATCH_QUERY = 101;

    private static final int HANDLE = 200;

    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "JavascriptBridge";

    @NonNull
    private final Handler mainHandler;

    @NonNull
    private final Looper mainLooper;

    @NonNull
    private final HashMap<UUID, ZapicQuery> queries;

    @NonNull
    private final Handler workerHandler;

    @NonNull
    private final Looper workerLooper;

    private WebView webView;

    private WebMessagePort webViewPort;

    @MainThread
    JavaScriptBridge(Context context) {
        this.queries = new HashMap<>();

        this.mainLooper = Looper.getMainLooper();
        this.mainHandler = new Handler(this.mainLooper) {
            @MainThread
            @Override
            public void handleMessage(Message msg) {
                if (msg == null) {
                    ZapicLog.e(TAG, "Received invalid message: null");
                    return;
                }

//                switch (msg.what) {
//                    case DISPATCH_QUERY:
//                        Result result = (Result)msg.obj;
//                        result.callback.onComplete(result.result, result.error);
//                        result.callback = null;
//                        msg.obj = null;
//                        break;
//                    case 0:
//                        try {
//                            JavaScriptBridge.this.dispatcher.dispatch((String) msg.obj);
//                        } catch (ZapicException e) {
//                            ZapicLog.e(TAG, e, "Error!");
//                        }
//
//                        break;
//                    default:
//                        ZapicLog.e(TAG, "Received invalid message: %d", msg.what);
//                        break;
//                }
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
                    return;
                }

                switch (msg.what) {
                    case JavaScriptBridge.DISPATCH_QUERY:
                        final ZapicQuery query = (ZapicQuery) msg.obj;
                        JavaScriptBridge.this.queries.put(query.getRequestId(), query);

                        final String action;
                        try {
                            action = new JSONObject()
                                    .put("type", "query")
                                    .put("payload", new JSONObject()
                                            .put("requestId", query.getRequestId().toString())
                                            .put("dataType", query.getDataType())
                                            .put("dataTypeVersion", query.getDataTypeVersion()))
                                    .toString();
                        } catch (JSONException e) {
                            ZapicLog.e(JavaScriptBridge.TAG, "Failed to serialize the action", e);
                            return;
                        }

                        JavaScriptBridge.this.mainHandler
                                .obtainMessage(
                                        JavaScriptBridge.DISPATCH,
                                        new JavaScriptBridge.Action(action, query.getRequestId()))
                                .sendToTarget();
                        break;
                    default:
                        ZapicLog.e(JavaScriptBridge.TAG, "Received invalid message from JavaScript bridge: %d", msg.what);
                        break;
                }
            }
        };

        final String html = "<html>" +
                "<head>" +
                "<title>Demo</title>" +
                "</head>" +
                "<body>" +
                "<script>" +
                "  window.addEventListener('message', function(event) {" +
                "    if (event.origin !== \"https://app.zapic.net\") {" +
                "      return;" +
                "    }" +
                "    if (event.data !== \"start\") {" +
                "      return;" +
                "    }" +
                "    window.zapicPort = event.ports[0];" +
                "    window.zapicPort.onmessage = function(event) {" +
                "      console.log(JSON.stringify(event));" +
                "    }" +
                "  });" +
                "</script>" +
                "</body>" +
                "</html";
        webView = new WebView(context);
        webView.loadDataWithBaseURL("https://app.zapic.net/", "", "text/html", "utf-8", "https://app.zapic.net/");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);

                if ("https://app.zapic.net/".equals(url)) {
                    final WebMessagePort[] webViewPorts = webView.createWebMessageChannel();

                    webViewPort = webViewPorts[0];
                    webViewPort.setWebMessageCallback(new WebMessagePort.WebMessageCallback() {
                        @Override
                        public void onMessage(final WebMessagePort port, final WebMessage message) {
                            super.onMessage(port, message);
                        }
                    }, workerHandler);

                    webView.postWebMessage(new WebMessage("start", new WebMessagePort[] { webViewPorts[1] }), Uri.parse("https://app.zapic.net"));
                }
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            final WebMessagePort[] ports = webView.createWebMessageChannel();
//            ports[0].setWebMessageCallback(new WebMessagePort.WebMessageCallback() {
//                @Override
//                public void onMessage(final WebMessagePort port, final WebMessage message) {
//                }
//            }, this.workerHandler);
//            webView.postWebMessage(new WebMessage("", new WebMessagePort[] { ports[1] }), "https://app.zapic.net");
//        }
    }

    private static class Action {
        @NonNull
        private final String action;

        @Nullable
        private final UUID requestId;

        private Action(@NonNull final String action, @Nullable final UUID requestId) {
            this.action = action;
            this.requestId = requestId;
        }
    }

    private static class ErrorResponse
    {
        @NonNull
        private final ZapicException error;

        @Nullable
        private final UUID requestId;

        private ErrorResponse(@NonNull final ZapicException error, @Nullable final UUID requestId) {
            this.error = error;
            this.requestId = requestId;
        }
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
        final ZapicQuery<ZapicChallenge[]> query = new ZapicQuery<>(UUID.randomUUID(), ZapicQuery.DataType.CHALLENGE_LIST, 1, callback);
        this.workerHandler.obtainMessage(JavaScriptBridge.DISPATCH_QUERY, query).sendToTarget();
        return query;
    }

    @AnyThread
    Future<?> getCompetitions(@NonNull final ZapicQueryCallback<ZapicCompetition[]> callback) {
        final ZapicQuery<ZapicCompetition[]> query = new ZapicQuery<>(UUID.randomUUID(), ZapicQuery.DataType.COMPETITION_LIST, 1, callback);
        this.workerHandler.obtainMessage(JavaScriptBridge.DISPATCH_QUERY, query).sendToTarget();
        return query;
    }

    @AnyThread
    Future<?> getPlayer(@NonNull final ZapicQueryCallback<ZapicPlayer> callback) {
        final ZapicQuery<ZapicPlayer> query = new ZapicQuery<>(UUID.randomUUID(), ZapicQuery.DataType.PLAYER, 1, callback);
        this.workerHandler.obtainMessage(JavaScriptBridge.DISPATCH_QUERY, query).sendToTarget();
        return query;
    }

    @AnyThread
    Future<?> getStatistics(@NonNull final ZapicQueryCallback<ZapicStatistic[]> callback) {
        final ZapicQuery<ZapicStatistic[]> query = new ZapicQuery<>(UUID.randomUUID(), ZapicQuery.DataType.STATISTIC_LIST, 1, callback);
        this.workerHandler.obtainMessage(JavaScriptBridge.DISPATCH_QUERY, query).sendToTarget();
        return query;
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
