package com.zapic.android.sdk;

import android.app.Fragment;

public final class ZapicFragment extends Fragment {
    @Override
    public void onStart() {
        final WebViewManager webViewManager = Zapic.getWebViewManager();
        if (webViewManager != null) {
            webViewManager.onStart(this.getActivity());
        }

        super.onStart();
    }

    @Override
    public void onStop() {
        final WebViewManager webViewManager = Zapic.getWebViewManager();
        if (webViewManager != null) {
            webViewManager.onStop();
        }

        super.onStop();
    }
}
