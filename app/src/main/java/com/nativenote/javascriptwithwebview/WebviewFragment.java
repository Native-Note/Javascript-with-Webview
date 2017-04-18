package com.nativenote.javascriptwithwebview;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class WebviewFragment extends Fragment {

    private String strUrl = "https://www.google.com/";
    private static final String TAG = WebviewFragment.class.getSimpleName();
    private String mCurrentUrl;

    private WebView webView;
    private myWebViewClient mWebViewClient;
    View view;

//    public static WebviewFragment newInstance(String strUrl) {
//        WebviewFragment webview = new WebviewFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(String.class.getName(), strUrl);
//        webview.setArguments(bundle);
//        return webview;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        strUrl = getArguments().getString(String.class.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.webview, container, false);
        webView = (WebView) view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSaveFormData(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        mWebViewClient = new myWebViewClient();
        webView.setWebViewClient(mWebViewClient);

        if (strUrl != null && strUrl.length() > 0) {
            webView.loadUrl(strUrl);
        } else
            Toast.makeText(WebviewFragment.this.getActivity(), "Sorry! Unable to load URL. Please Sync and try again.", Toast.LENGTH_LONG).show();

        return view;
    }

    private void injectScriptFile(WebView view, String strId) {
        try {
            view.loadUrl("javascript:(function() {" +
                    "var elementId = document.getElementById('" + strId + "');" +
                    "if(elementId) elementId.style.display = 'none';" +
                    "var elems = document.getElementsByTagName('*'), i;" +
                    "for (i in elems) {" +
                    "   if(('' + elems[i].className + '').indexOf('" + strId + "') > -1) {" +
                    "       elems[i].style.display = 'none';" +
                    "   }" +
                    " }" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (webView != null && webView.getVisibility() == View.VISIBLE)
            webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null && webView.getVisibility() == View.VISIBLE)
            webView.onPause();
    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mCurrentUrl != null && url != null && url.equals(mCurrentUrl)) {
                if(canGoBack()) goBack();
                return true;
            }

            view.loadUrl(url);
            mCurrentUrl = url;
            webView.setVisibility(View.GONE);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            showProgressDialog(getActivity().getResources().getString(R.string.loading_msg));
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String[] strIds = {"hplogo", "gb"};
            for (String str : strIds) {
                injectScriptFile(view, str.trim());
            }
            hideProgressDialog();
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public boolean canGoBack() {
        if (webView != null && webView.getVisibility() == View.VISIBLE)
            return webView.canGoBack();
        return false;
    }

    public void goBack() {
        if (webView.getVisibility() == View.VISIBLE)
            webView.goBack();
    }

    public ProgressDialog mProgressDialog;

    public void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this.getActivity());
            mProgressDialog.setMessage(text);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
