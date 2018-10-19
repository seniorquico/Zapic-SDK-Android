# Keep Zapic JavaScript API
-keepclassmembers class com.zapic.sdk.android.WebViewJavascriptBridge {
  public *;
}

# Keep Java APIs used by reflection
-keepnames class android.support.v4.app.Fragment
