//package com.zapic.sdk.android;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.webkit.WebView;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.concurrent.Future;
//
//final class QueryHandler implements Handler.Callback {
//    /**
//     * The tag used to identify log messages.
//     */
//    @NonNull
//    private static final String TAG = "ZapicCallback";
//
//    private static final int GET_CHALLENGE_LIST = 1000;
//    private static final int GET_COMPETITION_LIST = 1001;
//    private static final int GET_PLAYER = 1002;
//    private static final int GET_STATISTIC_LIST = 1003;
//
//    @NonNull
//    private final Map<String, ZapicFutureTask<ZapicChallenge[]>> challengeListRequests;
//
//    @NonNull
//    private final Map<String, ZapicFutureTask<ZapicCompetition[]>> competitionListRequests;
//
//    @NonNull
//    private final Handler mainHandler;
//
//    @NonNull
//    private final Looper mainLooper;
//
//    @NonNull
//    private final Map<String, ZapicFutureTask<ZapicPlayer>> playerRequests;
//
//    @NonNull
//    private final Map<String, ZapicFutureTask<ZapicStatistic[]>> statisticListRequests;
//
//    private int requestId;
//
//    private WebView webView;
//
//    QueryHandler() {
//        this.challengeListRequests = new LinkedHashMap<>();
//        this.competitionListRequests = new LinkedHashMap<>();
//        this.mainLooper = Looper.getMainLooper();
//        this.mainHandler = new Handler(mainLooper);
//        this.playerRequests = new LinkedHashMap<>();
//        this.requestId = 1;
//        this.statisticListRequests = new LinkedHashMap<>();
//        this.webView = null;
//    }
//
//    Future<?> getChallenges(ZapicCallback<ZapicChallenge[]> callback) {
//        final ZapicFutureTask<ZapicChallenge[]> task = new ZapicFutureTask<>(callback);
//
//        final Message message = this.mainHandler.obtainMessage(QueryHandler.GET_CHALLENGE_LIST, task);
//        message.sendToTarget();
//
//        return task;
//    }
//
//    Future<?> getCompetitions(ZapicCallback<ZapicCompetition[]> callback) {
//        final ZapicFutureTask<ZapicCompetition[]> task = new ZapicFutureTask<>(callback);
//
//        final Message message = this.mainHandler.obtainMessage(QueryHandler.GET_COMPETITION_LIST, task);
//        message.sendToTarget();
//
//        return task;
//    }
//
//    Future<?> getPlayer(ZapicCallback<ZapicPlayer> callback) {
//        final ZapicFutureTask<ZapicPlayer> task = new ZapicFutureTask<>(callback);
//
//        final Message message = this.mainHandler.obtainMessage(QueryHandler.GET_PLAYER, task);
//        message.sendToTarget();
//
//        return task;
//    }
//
//    Future<?> getStatistics(ZapicCallback<ZapicStatistic[]> callback) {
//        final ZapicFutureTask<ZapicStatistic[]> task = new ZapicFutureTask<>(callback);
//
//        final Message message = this.mainHandler.obtainMessage(QueryHandler.GET_STATISTIC_LIST, task);
//        message.sendToTarget();
//
//        return task;
//    }
//
//    @Override
//    public boolean handleMessage(Message msg) {
//        if (msg == null) {
//            return false;
//        }
//
//        switch (msg.what) {
//            case QueryHandler.GET_CHALLENGE_LIST:
//                @SuppressWarnings("unchecked")
//                ZapicFutureTask<ZapicChallenge[]> task = (ZapicFutureTask<ZapicChallenge[]>) msg.obj;
//                if (task.isDone()) {
//                    return true;
//                }
//
//                final boolean isFirstTask = this.challengeListRequests.isEmpty();
//                if (!isFirstTask) {
//                    ZapicLog.w(TAG, "Multiple queries are pending for the list of challenges");
//                }
//
//                final String requestId = String.valueOf(this.requestId++);
//
////                if (msg.obj instanceof ZapicCallback) {
////                    challengeListRequests.put(requestId, (ZapicCallback) msg.obj);
////                }
////
//
//
//                return true;
//            case QueryHandler.GET_COMPETITION_LIST:
//                return true;
//            case QueryHandler.GET_PLAYER:
//                return true;
//            case QueryHandler.GET_STATISTIC_LIST:
//                return true;
//            default:
//                return false;
//        }
//    }
//}
