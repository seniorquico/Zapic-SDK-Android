package com.zapic.android.sdk;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A utility class that manages the web client lifecycle.
 *
 * @author Kyle Dodson
 * @since 1.0
 */
final class WebViewManager implements WebClientDownloadTask.Handler {
    /**
     * The tag used for log messages.
     */
    @NonNull
    private static final String TAG = "WebViewManager";

    /**
     * The web client URL.
     */
    @NonNull
    private static final String WEB_CLIENT_URL = "https://client.zapic.net";

    /**
     * The synchronization lock used to guard reads and writes to all instance variables.
     */
    @NonNull
    private final Object lock = new Object();

    /**
     * The app version.
     */
    @NonNull
    private final String appVersion;

    /**
     * An asynchronous task that downloads the web client HTML.
     */
    @Nullable
    private AsyncTask<Void, Void, String> downloadTask;

    /**
     * The number of registered {@see ZapicActivity} and {@see ZapicFragment} instances.
     */
    private int references;

    /**
     * The web client HTML.
     */
    @Nullable
    private String webClientHTML;

    /**
     * The web view.
     */
    @Nullable
    private WebView webView;

    /**
     * Creates a new instance.
     *
     * @param appVersion The app version.
     */
    WebViewManager(@NonNull String appVersion) {
        this.appVersion = appVersion;
        this.downloadTask = null;
        this.references = 0;
        this.webClientHTML = null;
        this.webView = null;
    }

    /**
     * Receives the web client HTML and loads the web client in the web view.
     *
     * @param html The web client HTML.
     */
    public void onWebClientDownloaded(final @NonNull String html) {
        synchronized (this.lock) {
            if (this.webClientHTML == null) {
                this.webClientHTML = html;
            }

            if (this.downloadTask != null) {
                this.downloadTask = null;
            }

            if (this.webView != null) {
                Log.i(TAG, "Loading web client in web view");
                this.webView.loadDataWithBaseURL(WEB_CLIENT_URL, this.webClientHTML, "text/html", "utf-8", WEB_CLIENT_URL);
            }
        }
    }

    /**
     * Gets the web view.
     *
     * @return The web view.
     */
    @Nullable
    WebView getWebView() {
        return this.webView;
    }

    /**
     * Registers an activity or fragment.
     *
     * @param context The activity or fragment context.
     */
    void onStart(@NonNull Context context) {
        synchronized (this.lock) {
            ++this.references;
            Log.d(TAG, String.format("Incremented reference counter to %d", this.references));

            if (this.webView == null) {
                Log.i(TAG, "Creating WebView");
                Context applicationContext = context.getApplicationContext();
                this.webView = createWebView(applicationContext);
            }

            if (this.webClientHTML == null && this.downloadTask == null) {
                URL url;
                try {
                    url = new URL(WEB_CLIENT_URL);
                } catch (MalformedURLException e) {
                    this.onStop();
                    Log.wtf(TAG, "The web client URL is invalid!?", e);
                    return;
                }

                Log.i(TAG, "Downloading web client");
                this.downloadTask = new WebClientDownloadTask(url, this);
                this.downloadTask.execute();
            }
        }
    }

    /**
     * Unregisters an activity or fragment.
     */
    void onStop() {
        synchronized (this.lock) {
            if (this.references == 0) {
                return;
            }

            --this.references;
            Log.d(TAG, String.format("Decremented reference counter to %d", this.references));

            if (this.references == 0) {
                if (this.downloadTask != null) {
                    Log.i(TAG, "Cancelling downloading web client");
                    this.downloadTask.cancel(true);
                    this.downloadTask = null;
                }

                if (this.webView != null) {
                    Log.i(TAG, "Destroying WebView");
                    this.webView.destroy();
                    this.webView = null;
                }
            }
        }
    }

    /**
     * Creates and configures a web view and associates it with the specified context.
     *
     * @param context The context.
     * @return The web view.
     */
    @NonNull
    @SuppressLint("SetJavaScriptEnabled")
    private static WebView createWebView(@NonNull final Context context) {
        final WebView webView = new WebView(context);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setSupportZoom(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getScheme().equals("market")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Context context = view.getContext();
                        context.startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        return false;
                    }
                }
                return false;
            }
        });

        // TODO: Setup JavaScript bridge.
        // webViewBridge = new WebViewBridge(webView);
        // webView.addJavascriptInterface(webViewBridge, "androidWebView");

        return webView;
    }
}
