package com.zapic.sdk.android;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.*;
import android.view.View;
import android.webkit.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final class WebViewManager {
    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "WebViewManager";

    /**
     * The JavaScript startup timeout (in milliseconds).
     */
    private static final int TIMEOUT = 10000;

    /**
     * The variable name of the legacy message channel in the JavaScript context.
     */
    @NonNull
    private static final String VARIABLE_NAME = "androidWebViewChannel";

    /**
     * The global application context.
     */
    @NonNull
    private final Context applicationContext;

    @NonNull
    private final WebViewManager.MessageChannel messageChannel;

    /**
     * The status of Google Safe Browsing.
     */
    @WebViewManager.SafeBrowsingStatusDef
    private int webViewSafeBrowsingStatus;

    /**
     * The {@link WebView} instance.
     */
    @Nullable
    private WebView webView;

    /**
     * Creates a new {@link WebViewManager} instance.
     *
     * @param context The global application context or an activity context.
     */
    WebViewManager(@NonNull final Context context) {
        // final String packageName = activity.getPackageName();
        // Bundle metaData;
        // try {
            // final ApplicationInfo info = activity.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            // metaData = info.metaData;
        // } catch (PackageManager.NameNotFoundException e) {
            // throw new RuntimeException("Using Zapic requires a metadata tag with the name \"com.google.android.gms.games.APP_ID\" in the application tag of the manifest for " + packageName);
        // }

        // String appId = metaData.getString("com.google.android.gms.games.APP_ID");
        // if (appId != null) {
            // appId = appId.trim();
            // if (appId.length() == 0) {
                // appId = null;
            // }
        // }

        // if (appId == null) {
            // throw new RuntimeException("Using Zapic requires a metadata tag with the name \"com.google.android.gms.games.APP_ID\" in the application tag of the manifest for " + packageName);
        // }

        // String webClientId = metaData.getString("com.google.android.gms.games.WEB_CLIENT_ID");
        // if (webClientId != null) {
            // webClientId = webClientId.trim();
            // if (webClientId.length() == 0) {
                // webClientId = null;
            // }
        // }

        // if (webClientId == null) {
            // throw new RuntimeException("Using Zapic requires a metadata tag with the name \"com.google.android.gms.games.WEB_CLIENT_ID\" in the application tag of the manifest for " + packageName);
        // }

        // GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                // .requestServerAuthCode(webClientId)
                // .build();
        // return GoogleSignIn.getClient(activity, options);

        final ValueCallback<JSONObject> onMessage = new ValueCallback<JSONObject>() {
            @Override
            public void onReceiveValue(final JSONObject value) {
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.messageChannel = new WebViewManager.ModernMessageChannel(onMessage);
        } else {
            this.messageChannel = new WebViewManager.LegacyMessageChannel(onMessage);
        }

        this.applicationContext = context.getApplicationContext();
        this.webView = null;
    }

    /**
     * Starts Google Safe Browsing. This calls {@link #startWebView()} after setting {@link #webViewSafeBrowsingStatus}.
     */
    @MainThread
    private void startSafeBrowsing() {
        if (this.webViewSafeBrowsingStatus == WebViewManager.SafeBrowsingStatus.NOT_STARTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                WebView.startSafeBrowsing(this.applicationContext, new ValueCallback<Boolean>() {
                    @MainThread
                    @Override
                    public void onReceiveValue(@Nullable final Boolean value) {
                        if (value == null || !value) {
                            ZapicLog.i(WebViewManager.TAG, "Safe Browsing is not supported");
                            WebViewManager.this.webViewSafeBrowsingStatus = WebViewManager.SafeBrowsingStatus.NOT_SUPPORTED;
                        } else {
                            ZapicLog.i(WebViewManager.TAG, "Safe Browsing is supported and started");
                            WebViewManager.this.webViewSafeBrowsingStatus = WebViewManager.SafeBrowsingStatus.STARTED;
                        }

                        WebViewManager.this.startWebView();
                    }
                });
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ZapicLog.i(WebViewManager.TAG, "Safe Browsing is assumed to be supported on API 26");
                WebViewManager.this.webViewSafeBrowsingStatus = WebViewManager.SafeBrowsingStatus.STARTED;
                this.startWebView();
            } else {
                ZapicLog.i(WebViewManager.TAG, "Safe Browsing is not supported on API 25 and lower");
                WebViewManager.this.webViewSafeBrowsingStatus = WebViewManager.SafeBrowsingStatus.NOT_SUPPORTED;
                this.startWebView();
            }
        } else {
            this.startWebView();
        }
    }

    @MainThread
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void startWebView() {
        if (this.webViewSafeBrowsingStatus == WebViewManager.SafeBrowsingStatus.NOT_STARTED) {
            return;
        }

        if (this.webView == null) {
            this.webView = new WebView(new MutableContextWrapper(this.applicationContext));
            this.webView.setVisibility(View.GONE);
            this.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            this.webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            this.webView.setWebChromeClient(new ChromeClient());
            this.webView.setWebViewClient(new ViewClient());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                this.messageChannel.bind(this.webView);
                this.webView.addJavascriptInterface(this.messageChannel, WebViewManager.VARIABLE_NAME);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.webView.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_BOUND, true);
            }

            WebSettings webSettings = this.webView.getSettings();
            webSettings.setAllowContentAccess(false);
            webSettings.setAllowFileAccess(false);
            webSettings.setAllowFileAccessFromFileURLs(false);
            webSettings.setAllowUniversalAccessFromFileURLs(false);
            webSettings.setAppCacheEnabled(false);
            webSettings.setBlockNetworkImage(false);
            webSettings.setBlockNetworkLoads(false);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setDatabaseEnabled(false);
            webSettings.setDisplayZoomControls(false);
            webSettings.setDomStorageEnabled(true);
            webSettings.setGeolocationEnabled(false);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setLoadWithOverviewMode(false);
            webSettings.setMediaPlaybackRequiresUserGesture(false);
            webSettings.setSaveFormData(false);
            webSettings.setSupportMultipleWindows(false);
            webSettings.setSupportZoom(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                webSettings.setOffscreenPreRaster(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                webSettings.setDisabledActionModeMenuItems(WebSettings.MENU_ITEM_PROCESS_TEXT | WebSettings.MENU_ITEM_SHARE | WebSettings.MENU_ITEM_WEB_SEARCH);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webSettings.setSafeBrowsingEnabled(this.webViewSafeBrowsingStatus == WebViewManager.SafeBrowsingStatus.STARTED);
            }
        }

        final String html = "<html>" +
                "<head>" +
                "<script>" +
                ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        ?
                        "  window.onmessage = function (event) {" +
                        "    if (event.origin !== 'https://app.zapic.net') {" +
                        "      return;" +
                        "    }" +
                        "    window." + WebViewManager.VARIABLE_NAME + " = event.ports[0];" +
                        "    window.zapic.onLoaded();" +
                        "  };"
                        :
                        "  window.onload = function () {" +
                        "    window.setTimeout(function () {" +
                        "      window.zapic.onLoaded();" +
                        "    }, 0);" +
                        "  };"
                ) +
                "  window.androidWebViewWatchdog = window.setTimeout(function () {" +
                "    window." + WebViewManager.VARIABLE_NAME + ".postMessage('{\"type\":\"APP_FAILED\"}');" +
                "  }, " + Integer.toString(WebViewManager.TIMEOUT, 10) + ");" +
                "  window.zapic = {" +
                "    environment: 'webview'," +
                "    version: 3," +
                "    onLoaded: function () {" +
                "      window.clearTimeout(window.androidWebViewWatchdog);" +
                "      delete window.androidWebViewWatchdog;" +
                "      window.zapic.dispatch = function (action) {" +
                "        console.log('Action: ' + action);" +
                "      };" +
                "    }," +
                "  };" +
                "  window.setTimeout(function () {" +
                "    window." + WebViewManager.VARIABLE_NAME + ".postMessage('{\"type\":\"APP_STARTED\"}');" +
                "  };" +
                "</script>" +
                "</head>" +
                "</html>".replaceAll(" +", " ");
        this.webView.loadDataWithBaseURL("https://app.zapic.net/", html, "text/html", "utf-8", "https://app.zapic.net/");
    }

    private interface MessageChannel {
        /**
         * Binds the message channel to the specified web view.
         *
         * @param webView The web view.
         * @throws IllegalStateException If the message channel is already bound to a web view.
         */
        @MainThread
        void bind(@NonNull final WebView webView);

        /**
         * Sends a JSON-encoded action object to the JavaScript context.
         *
         * @param action The JSON-encoded action object.
         * @throws IllegalStateException If the message channel is not bound to a web view.
         */
        @MainThread
        void send(@NonNull final String action);

        /**
         * Unbinds the message channel from the web view.
         *
         * @throws IllegalStateException If the message channel is not bound to a web view.
         */
        @MainThread
        void unbind();
    }

    private static final class LegacyMessageChannel implements WebViewManager.MessageChannel {
        /**
         * The callback invoked when a message is received.
         */
        @NonNull
        private final ValueCallback<JSONObject> onReceive;

        /**
         * The web view.
         */
        @Nullable
        private WebView webView;

        /**
         * Creates a new {@link WebViewManager.LegacyMessageChannel} instance.
         *
         * @param onReceive The callback invoked when a message is received.
         */
        @MainThread
        private LegacyMessageChannel(@NonNull final ValueCallback<JSONObject> onReceive) {
            this.onReceive = onReceive;
        }

        /**
         * Binds the message channel to the specified web view.
         *
         * @param webView The web view.
         * @throws IllegalStateException If the message channel is already bound to a web view.
         */
        @MainThread
        @Override
        public void bind(@NonNull final WebView webView)
        {
            if (this.webView != null) {
                throw new IllegalStateException("The message channel is already bound to a web view");
            }

            this.webView = webView;
        }

        /**
         * Receives a message.
         *
         * @param message The message.
         */
        @JavascriptInterface
        @WorkerThread
        public void onMessage(@Nullable final String message) {
            if (message == null) {
                return;
            }

            final JSONObject action;
            try {
                action = new JSONObject(message);
            } catch (JSONException e) {
                ZapicLog.e(WebViewManager.TAG, e, "Failed to parse JavaScript action object: %s", message);
                return;
            }

            ZapicLog.i(WebViewManager.TAG, "Received: %s", action);
            this.onReceive.onReceiveValue(action);
        }

        /**
         * Sends a JSON-encoded action object to the JavaScript context.
         *
         * @param action The JSON-encoded action object.
         * @throws IllegalStateException If the message channel is not bound to a web view.
         */
        @MainThread
        @Override
        public void send(@NonNull final String action) {
            if (this.webView == null) {
                throw new IllegalStateException("The message channel is not bound to a web view");
            }

            this.webView.evaluateJavascript(String.format("window.zapic.dispatch(%s);", action), null);
            ZapicLog.i(WebViewManager.TAG, "Sent: %s", action);
        }

        /**
         * Unbinds the message channel from the web view.
         *
         * @throws IllegalStateException If the message channel is not bound to a web view.
         */
        @MainThread
        @Override
        public void unbind() {
            if (this.webView == null) {
                throw new IllegalStateException("The message channel is not bound to a web view");
            }

            this.webView = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static final class ModernMessageChannel extends WebMessagePort.WebMessageCallback
            implements WebViewManager.MessageChannel {
        /**
         * The callback invoked when a message is received.
         */
        @NonNull
        private final ValueCallback<JSONObject> onReceive;

        /**
         * The message port.
         */
        @Nullable
        private WebMessagePort port;

        /**
         * Creates a new {@link WebViewManager.ModernMessageChannel} instance.
         *
         * @param onReceive The callback invoked when a message is received.
         */
        ModernMessageChannel(@NonNull final ValueCallback<JSONObject> onReceive) {
            this.onReceive = onReceive;
        }

        /**
         * Binds the message channel to the specified web view.
         *
         * @param webView The web view.
         * @throws IllegalStateException If the message channel is already bound to a web view.
         */
        @MainThread
        @Override
        public void bind(@NonNull final WebView webView)
        {
            if (this.port != null) {
                throw new IllegalStateException("The message channel is already bound to a web view");
            }

            final WebMessagePort[] ports = webView.createWebMessageChannel();

            // Use port 0 for sending/receiving on the native side
            this.port = ports[0];
            this.port.setWebMessageCallback(this);

            // Use port 1 for sending/receiving on the JavaScript side
            webView.postWebMessage(new WebMessage("", new WebMessagePort[] { ports[1] }), Uri.parse("https://app.zapic.net"));
        }

        /**
         * Receives a message with a JSON-encoded action object.
         *
         * @param port    The message port.
         * @param message The message.
         */
        @WorkerThread
        @Override
        public void onMessage(@Nullable final WebMessagePort port, @Nullable final WebMessage message) {
            if (port == null || message == null) {
                return;
            }

            final String data = message.getData();
            final JSONObject action;
            try {
                action = new JSONObject(data);
            } catch (JSONException e) {
                ZapicLog.e(WebViewManager.TAG, e, "Failed to parse JavaScript action object: %s", data);
                return;
            }

            ZapicLog.i(WebViewManager.TAG, "Received: %s", action);
            this.onReceive.onReceiveValue(action);
        }

        /**
         * Sends a JSON-encoded action object to the JavaScript context.
         *
         * @param action The JSON-encoded action object.
         * @throws IllegalStateException If the message channel is not bound to a web view.
         */
        @MainThread
        @Override
        public void send(@NonNull final String action) {
            if (this.port == null) {
                throw new IllegalStateException("The message channel is not bound to a web view");
            }

            this.port.postMessage(new WebMessage(action));
            ZapicLog.i(WebViewManager.TAG, "Sent: %s", action);
        }

        /**
         * Unbinds the message channel from the web view.
         *
         * @throws IllegalStateException If the message channel is not bound to a web view.
         */
        @MainThread
        @Override
        public void unbind() {
            if (this.port == null) {
                throw new IllegalStateException("The message channel is not bound to a web view");
            }

            this.port.close();
            this.port = null;
        }
    }

    private final class ChromeClient extends WebChromeClient
    {
    }

    @IntDef({
            WebViewManager.SafeBrowsingStatus.NOT_STARTED,
            WebViewManager.SafeBrowsingStatus.NOT_SUPPORTED,
            WebViewManager.SafeBrowsingStatus.STARTED
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface SafeBrowsingStatusDef {}

    private static final class SafeBrowsingStatus {
        static final int NOT_STARTED = 0;

        static final int NOT_SUPPORTED = 1;

        static final int STARTED = 2;
    }

    private final class ViewClient extends WebViewClient {
        @Override
        public void onPageFinished(final WebView view, final String url) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                WebViewManager.this.messageChannel.bind(view);
            }
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
            // TODO: Show an error and retry page.
        }

        @Override
        @RequiresApi(Build.VERSION_CODES.M)
        public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {
            // TODO: Show an error and retry page.
        }

        @Override
        @RequiresApi(Build.VERSION_CODES.O)
        public boolean onRenderProcessGone(@NonNull final WebView view, @NonNull final RenderProcessGoneDetail detail) {
            if (!view.equals(WebViewManager.this.webView)) {
                return false;
            }

            WebViewManager.this.messageChannel.unbind();
            WebViewManager.this.webView.destroy();
            WebViewManager.this.webView = null;

            final boolean crashed = detail.didCrash();
            if (crashed) {
                ZapicLog.e(WebViewManager.TAG, "The Zapic application crashed");
                WebViewManager.this.startSafeBrowsing();
            } else {
                ZapicLog.e(WebViewManager.TAG, "The Zapic application was stopped to free memory");
            }

            return true;
        }

        @Override
        @RequiresApi(Build.VERSION_CODES.O_MR1)
        public void onSafeBrowsingHit(final WebView view, final WebResourceRequest request, final int threatType, final SafeBrowsingResponse callback) {
            final String url = request.getUrl().toString().toLowerCase();
            if (url.startsWith("https://api.zapic.net/")) {
                callback.proceed(false);
            } else {
                super.onSafeBrowsingHit(view, request, threatType, callback);
            }
        }

        @Nullable
        @Override
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(final WebView view, final WebResourceRequest request) {
            return this.shouldInterceptRequestImpl(request.getMethod(), request.getUrl().toString());
        }

        @Nullable
        @Override
        @SuppressWarnings("deprecation")
        public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return this.shouldInterceptRequestImpl("GET", url);
            } else {
                return null;
            }
        }

        @CheckResult
        @Nullable
        private WebResourceResponse shouldInterceptRequestImpl(@NonNull final String method, @NonNull String url) {
            url = url.toLowerCase();
            if ("GET".equalsIgnoreCase(method) && (url.startsWith("https://api.zapic.net/") && !url.startsWith("https://api.zapic.net/api/"))) {
                InputStream data;
                Map<String, String> headers;
                String reasonPhrase;
                int statusCode;
//                if (mWebPage == null) {
                //noinspection ZeroLengthArrayAllocation
                data = new ByteArrayInputStream(new byte[0]);
                    headers = new HashMap<>();
                    reasonPhrase = "Not Found";
                    statusCode = HttpsURLConnection.HTTP_NOT_FOUND;
//                } else {
//                    data = new ByteArrayInputStream(mWebPage.getHtml().getBytes(StandardCharsets.UTF_8));
//                    headers = mWebPage.getHeaders();
//                    reasonPhrase = "OK";
//                    statusCode = HttpsURLConnection.HTTP_OK;
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return new WebResourceResponse("text/html", "utf-8", statusCode, reasonPhrase, headers, data);
                } else {
                    return new WebResourceResponse("text/html", "utf-8", data);
                }
            }

            return null;
        }

        @Override
        @RequiresApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {
            return this.shouldOverrideUrlLoadingImpl(view, request.getMethod(), request.getUrl());
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return this.shouldOverrideUrlLoadingImpl(view, "GET", Uri.parse(url));
            } else {
                return false;
            }
        }

        @CheckResult
        private boolean shouldOverrideUrlLoadingImpl(@NonNull final WebView view, @NonNull final String method, @NonNull final Uri uri) {
            final String url = uri.toString().toLowerCase();
            if ("GET".equalsIgnoreCase(method) && ("https://api.zapic.net".equals(url) ||
                    (url.startsWith("https://api.zapic.net/") && !url.startsWith("https://api.zapic.net/api/")))) {
                // Allow the WebView to navigate to the Zapic web page.
                return false;
            }

            final String scheme = uri.getScheme();
            if ("mailto".equalsIgnoreCase(scheme)) {
                try {
                    // Create an email app intent.
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(uri);

                    // Open the email app.
                    final Context context = view.getContext();
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    ZapicLog.e(WebViewManager.TAG, "mailto: link not supported", e);
                }
            } else if ("tel".equalsIgnoreCase(scheme)) {
                try {
                    // Create a phone app intent.
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(uri);

                    // Open the phone app.
                    final Context context = view.getContext();
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    ZapicLog.e(WebViewManager.TAG, "tel: link not supported", e);
                }
            } else {
                try {
                    // Create a generic app intent (Chrome, Google Play Store, etc.).
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);

                    // Open the app.
                    final Context context = view.getContext();
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    ZapicLog.e(WebViewManager.TAG, String.format("%s: link not supported", scheme), e);
                }
            }

            // Prevent the WebView from navigating to an invalid URL.
            return true;
        }
    }
}
