package com.zapic.android.sdk;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * An asynchronous task that downloads the web client HTML.
 *
 * @author Kyle Dodson
 * @since 1.0
 */
final class WebClientDownloadTask extends AsyncTask<Void, Void, String> {
    /**
     * The tag used for log messages.
     */
    @NonNull
    private static final String TAG = "WebClientDownloadTask";

    /**
     * The callback handler that receives the web client HTML on the UI thread.
     */
    @NonNull
    private final Handler handler;

    /**
     * The web client URL.
     */
    @NonNull
    private final URL url;

    /**
     * Creates a new instance.
     *
     * @param url     The web client URL.
     * @param handler The callback handler that receives the web client HTML on the UI thread.
     */
    WebClientDownloadTask(final @NonNull URL url, final @NonNull Handler handler) {
        this.handler = handler;
        this.url = url;
    }

    @Override
    protected String doInBackground(final Void... params) {
        String html = this.downloadWebClient();
        if (html != null) {
            html = injectWebClientBridge(html);
        }

        Log.d(TAG, "Downloaded web client and injected Android SDK bridge script");
        Log.d(TAG, html);

        return html;
    }

    @Override
    protected void onPostExecute(final String html) {
        if (html != null) {
            this.handler.onWebClientDownloaded(html);
        }
    }

    /**
     * Downloads the web client.
     *
     * @return The web client HTML or null if the asynchronous task was cancelled.
     */
    @Nullable
    private String downloadWebClient() {
        int failures = 0;
        do {
            URLConnection connection = null;
            BufferedReader reader = null;
            try {
                Log.i(TAG, String.format("Connecting to %s", this.url));
                connection = this.url.openConnection();
                if (this.isCancelled()) {
                    return null;
                }

                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.connect();
                if (this.isCancelled()) {
                    return null;
                }

                Log.d(TAG, "Downloading web client");
                final StringBuilder html = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                if (this.isCancelled()) {
                    return null;
                }

                final char[] buffer = new char[1024 * 4];
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    if (this.isCancelled()) {
                        return null;
                    }

                    html.append(buffer, 0, n);
                }

                return html.toString();
            } catch (IOException e) {
                Log.e(TAG, "Failed to download web client", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close input stream", e);
                    }
                }

                if (connection != null) {
                    if (HttpsURLConnection.class.isInstance(connection)) {
                        ((HttpsURLConnection) connection).disconnect();
                    } else if (HttpURLConnection.class.isInstance(connection)) {
                        ((HttpURLConnection) connection).disconnect();
                    }
                }
            }

            // TODO: (DEV-63) Delay (with exponential backoff) and retry.
            if (failures < 10) {
                ++failures;
            }

            Log.d(TAG, String.format("Retrying download in %d seconds", 0));
        } while (!this.isCancelled());
        return null;
    }

    /**
     * Injects the Android SDK bridge script into the web client HTML.
     *
     * @param html The web client HTML.
     * @return The web client HTML with the Android SDK bridge script injected.
     */
    @NonNull
    private String injectWebClientBridge(@NonNull String html) {
        final int startOfHead = html.indexOf("<head>");
        if (startOfHead == -1) {
            return html;
        }

        final int endOfHead = startOfHead + "<head>".length();
        final String script = "<script>" +
                "window.zapic = {" +
                "  environment: 'webview'," +
                "  version: 1," +
                "  onLoaded: (action$, publishAction) => {" +
                "    window.zapic.dispatch = (action) => {" +
                "      publishAction(action)" +
                "    };" +
                "    action$.subscribe(action => {" +
                "      window.androidWebView.dispatch(JSON.stringify(action))" +
                "    });" +
                "  }" +
                "};" +
                "</script>";

        final StringBuilder htmlBuilder = new StringBuilder(html);
        htmlBuilder.insert(endOfHead, script);
        return htmlBuilder.toString();
    }

    /**
     * Represents a callback handler that receives the web client HTML on the UI thread.
     *
     * @author Kyle Dodson
     * @since 1.0
     */
    interface Handler {
        /**
         * Receives the web client HTML on the UI thread.
         *
         * @param html The web client HTML.
         */
        void onWebClientDownloaded(@NonNull String html);
    }
}
