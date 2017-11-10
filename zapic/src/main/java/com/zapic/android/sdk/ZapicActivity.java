package com.zapic.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ZapicActivity extends Activity {
    /**
     * Represents the default page (as defined by the web client). This must be the page opened when
     * the Zapic button is selected.
     */
    public static final int DEFAULT_PAGE = 0;

    /**
     * Represents the profile page.
     */
    public static final int PROFILE_PAGE = 1;

    /**
     * The tag used for log messages.
     */
    @NonNull
    private static final String TAG = "ZapicActivity";

//    public void closePage()
//    {
//        finish();
//    }

    @Override
    public void onStop() {
        final WebViewManager webViewManager = Zapic.getWebViewManager();
        if (webViewManager != null) {
            webViewManager.onStop();
        }

        super.onStop();
    }

//    public void openPage()
//    {
//        WebViewManager webViewManager = WebViewManager.getInstance();
//        WebView webView = webViewManager.getWebView();
//        if (webView != null) {
//            setContentView(webView);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Show splash screen or web view.
        super.onCreate(savedInstanceState);

        final WebViewManager webViewManager = Zapic.getWebViewManager();
        if (webViewManager != null) {
            FrameLayout frameLayout = new FrameLayout(this);

            final WebView webView = webViewManager.getWebView();
            if (webView != null) {
                FrameLayout.LayoutParams webViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                webView.setLayoutParams(webViewLayoutParams);
                frameLayout.addView(webView);
            }

            FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            setContentView(frameLayout, frameLayoutParams);
        }
    }

    @Override
    protected void onStart() {
        final WebViewManager webViewManager = Zapic.getWebViewManager();
        if (webViewManager != null) {
            webViewManager.onStart(this);
        }

        super.onStart();
    }

    /**
     * Represents the possible pages.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DEFAULT_PAGE, PROFILE_PAGE})
    public @interface Page {
    }
}
