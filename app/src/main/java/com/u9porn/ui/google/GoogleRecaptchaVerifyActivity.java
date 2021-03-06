package com.u9porn.ui.google;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.BuildConfig;
import com.u9porn.R;
import com.u9porn.ui.MvpActivity;
import com.u9porn.utils.DialogUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoogleRecaptchaVerifyActivity extends MvpActivity<GoogleRecaptchaVerifyView, GoogleRecaptchaVerifyPresenter> implements GoogleRecaptchaVerifyView {

    private final static String TAG = GoogleRecaptchaVerifyActivity.class.getSimpleName();

    @BindView(R.id.btn_refresh)
    AppCompatButton btnRefresh;
    @BindView(R.id.btn_clean)
    AppCompatButton btnClean;
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private CookieManager cookieManager;

    private final Object lock = new Object();

    @Inject
    protected GoogleRecaptchaVerifyPresenter googleRecaptchaVerifyPresenter;

    private AlertDialog verifyAlertDialog;

    private AlertDialog checkNeedVerifyDialog;

    private AlertDialog loadVerifyPageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_recaptcha_verify);
        ButterKnife.bind(this);
        initDialog();
        initToolBar(toolbar);
        initWebView();
    }

    private void initDialog() {
        verifyAlertDialog = DialogUtils.initLoadingDialog(this, "???????????????????????????...");
        checkNeedVerifyDialog = DialogUtils.initLoadingDialog(this, "?????????,?????????...");
        loadVerifyPageDialog = DialogUtils.initLoadingDialog(this, "?????????????????????...");
    }

    @NonNull
    @Override
    public GoogleRecaptchaVerifyPresenter createPresenter() {
        return googleRecaptchaVerifyPresenter;
    }

    private void initWebView() {
        WebSettings mWebSettings = webView.getSettings();
        //??????JavaScript???
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        cookieManager = CookieManager.getInstance();

        webView.setWebViewClient(new MyWebClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    loadVerifyPageSuccess();
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        cookieManager.setAcceptThirdPartyCookies(webView, true);
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void doPost(String action, String r, String id, String recaptcha) {
        presenter.verifyGoogleRecaptcha(presenter.getBaseAddress() + action, r, id, recaptcha);
    }

    private void cleanCache() {
        cookieManager.removeAllCookies(value -> {

        });
        webView.clearCache(true);
    }

    @Override
    protected boolean needGoToCheckGoogleRecaptcha() {
        return false;
    }

    @OnClick({R.id.btn_refresh, R.id.btn_clean})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh:
                //  doCall(a, url);
                presenter.testV9Porn();
                break;
            case R.id.btn_clean:
                cleanCache();
                break;
        }
    }

    @Override
    public void startCheckNeedVerify() {
        if (checkNeedVerifyDialog != null && !checkNeedVerifyDialog.isShowing()) {
            checkNeedVerifyDialog.show();
        }
    }

    @Override
    public void noNeedVerifyRecaptcha() {
        showMessage("????????????????????????????????????", TastyToast.SUCCESS);
        hideDialog();
    }

    @Override
    public void needVerifyRecaptcha(String html) {
        if (loadVerifyPageDialog != null && !loadVerifyPageDialog.isShowing()) {
            loadVerifyPageDialog.show();
        }
        showMessage("?????????????????????????????????", TastyToast.INFO);
        webView.loadDataWithBaseURL(presenter.getBaseAddress(), html, "text/html", "UTF-8", null);
    }

    @Override
    public void loadPageDataFailure() {
        showMessage("?????????????????????????????????????????????????????????", TastyToast.ERROR);
        hideDialog();
    }

    @Override
    public void startVerifyRecaptcha() {
        if (verifyAlertDialog != null && !verifyAlertDialog.isShowing()) {
            verifyAlertDialog.show();
        }
    }

    @Override
    public void verifyRecaptchaSuccess() {
        synchronized (lock) {
            lock.notifyAll();
        }
        showMessage("????????????????????????", TastyToast.SUCCESS);
        hideDialog();
        this.finish();
    }

    @Override
    public void verifyRecaptchaFailure() {
        synchronized (lock) {
            lock.notifyAll();
        }
        showMessage("?????????????????????????????????????????????????????????", TastyToast.ERROR);
        hideDialog();
    }

    private void hideDialog() {
        if (verifyAlertDialog != null && verifyAlertDialog.isShowing()) {
            verifyAlertDialog.dismiss();
        }
        if (checkNeedVerifyDialog != null && checkNeedVerifyDialog.isShowing()) {
            checkNeedVerifyDialog.dismiss();
        }
        if (loadVerifyPageDialog != null && loadVerifyPageDialog.isShowing()) {
            loadVerifyPageDialog.dismiss();
        }
    }

    private void loadVerifyPageSuccess() {
        showMessage("?????????????????????????????????????????????????????????", TastyToast.SUCCESS);
        hideDialog();
        btnRefresh.setText("????????????");
    }

    private void loadVerifyPageFailure() {
        showMessage("??????????????????????????????????????????", TastyToast.ERROR);
        hideDialog();
        btnRefresh.setText("????????????");
    }

    private class MyWebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            GoogleRecaptchaVerifyActivity.this.loadVerifyPageFailure();
            Logger.t(TAG).d("onReceivedError>>>3");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            GoogleRecaptchaVerifyActivity.this.loadVerifyPageFailure();
            Logger.t(TAG).d("onReceivedSslError");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            GoogleRecaptchaVerifyActivity.this.loadVerifyPageFailure();
            Logger.t(TAG).d("onReceivedError>>>4");
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.contains("?__cf_chl_captcha_tk__")) {
                runOnUiThread(() -> webView.evaluateJavascript("javascript:getPostData()", value -> {
                    String postData = value.replace("\"", "");
                    Logger.t(TAG).d(postData);
                    String[] data = postData.split(",");
                    Logger.t(TAG).d(data);
                    if (data.length >= 4) {
                        doPost(data[0], data[1], data[2], data[3]);
                    } else {
                        showMessage("????????????POST????????????????????????", TastyToast.ERROR);
                        btnRefresh.setText("????????????");
                    }
                }));
                synchronized (lock) {
                    try {
                        //??????????????????
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return new WebResourceResponse("", "", null);
            }
            Log.d(TAG, url);
            return super.shouldInterceptRequest(view, request);
        }
    }
}
