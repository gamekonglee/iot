package com.juhao.home.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.apiclient.CommonRequest;
import com.aliyun.alink.apiclient.CommonResponse;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;

import java.util.Map;

public class WebViewActivity extends BaseActivity {
    private static final int RESULT_CODE = 222;
    String url = "https://oauth.taobao.com/authorize?response_type=code&client_id="+ DemoApplication.app_key +"&redirect_uri=http://www.juhao.com&view=wap";
    private WebView web_view;
    private TextView topbar;
    private String mAuthCode;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_web_view);
        web_view = findViewById(R.id.web_view);
        WebSettings webSettings= web_view.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        web_view.setDrawingCacheEnabled(true);

        topbar = findViewById(R.id.topbar);
        web_view.setWebViewClient(new WebViewClient() {
            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                topbar.setText(view.getTitle());
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isTokenUrl(url)) {
                    Intent intent = new Intent();
                    intent.putExtra("AuthCode", mAuthCode);
                    setResult(RESULT_CODE, intent);
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return false;
            }
        });
        web_view.loadUrl(url);

    }
    private boolean isTokenUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if ( url.contains("code=")) {
                String[] urlArray = url.split("code=");
                if (urlArray.length > 1) {
                    String[] paramArray = urlArray[1].split("&");
                    if (paramArray.length > 1) {
                        mAuthCode = paramArray[0];
                        return true;
                    }
                }
            }
        }
        return false;
    }



    @Override
    protected void initData() {

    }
}
