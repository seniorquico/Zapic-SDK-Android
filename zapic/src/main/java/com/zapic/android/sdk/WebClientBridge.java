package com.zapic.android.sdk;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

final class WebClientBridge implements Handler.Callback {
    private static final String TAG = "ZAPIC-BRIDGE";

    private final Handler handler;
    private final WebView webView;

    public WebClientBridge(WebView webView) {
        this.handler = new Handler(Looper.getMainLooper(), this);
        this.webView = webView;
    }

    @JavascriptInterface
    public void dispatch(String json) {
        try {
            JSONObject action = new JSONObject(json);
            String actionType = action.getString("type");
            Log.d(TAG, "Received action of type \"" + actionType + "\"");
            switch (actionType) {
                case "LOGIN":
                    this.dispatchMessage("{ type: 'LOGIN_WITH_PLAY_GAME_SERVICES', payload: { serverAuthCode: 'my-server-auth-code' } }");
                    break;
                case "APP_STARTED":
                    this.dispatchMessage("{ type: 'OPEN_PAGE', payload: 'default' }");
                    break;
                case "PAGE_READY":
                    this.showWebView();
                    break;
                case "CLOSE_PAGE_REQUESTED":
                    this.closeWebView();
                    this.dispatchMessage("{ type: 'CLOSE_PAGE' }");
                default:
                    break;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
    }

    public void closeWebView() {
        Message message = this.handler.obtainMessage(3);
        message.sendToTarget();
    }

    public void showWebView() {
        Message message = this.handler.obtainMessage(2);
        message.sendToTarget();
    }

    public void dispatchMessage(String json) {
        String code = "window.zapic.dispatch(" + json + ")";
        Message message = this.handler.obtainMessage(1, code);
        message.sendToTarget();
    }

    @Override
    public boolean handleMessage(Message inputMessage) {
        switch (inputMessage.what) {
            case 1:
                String code = (String)inputMessage.obj;
                this.webView.evaluateJavascript(code, null);
                return false;
            case 2:
//                WeakReference<ZapicActivity> instanceReference = ZapicFragment.activityInstanceReference;
//                if (instanceReference != null) {
//                    ZapicActivity instance = instanceReference.get();
//                    if (instance != null) {
//                        instance.loadPage();
//                    }
//                }
                return false;
            case 3:
//                WeakReference<ZapicActivity> instanceReference2 = ZapicFragment.activityInstanceReference;
//                if (instanceReference2 != null) {
//                    ZapicActivity instance = instanceReference2.get();
//                    if (instance != null) {
//                        instance.closePage();
//                    }
//                }
                return false;
            default:
                return true;
        }
    }
}