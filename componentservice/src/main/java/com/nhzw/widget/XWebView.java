package com.nhzw.widget;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nhzw.rx.RxUtils;
import com.nhzw.service.AppService;


/**
 * 自定义WebView
 * Created by ww on 2017/10/25.
 */

public class XWebView extends WebView {
    public XWebView(Context context) {
        super(context);
        initWebView();
    }

    public XWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView();
    }

    public XWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView();
    }

    private void initWebView() {
        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);//default is true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        if (AppService.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//开启chrome调试
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setAllowFileAccessFromFileURLs(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小 (默认)
         * 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
         */
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//http、https混用
        }
        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                RxUtils.download(getContext(), url);
            }
        });
//        CookieSyncManager.createInstance(getContext());
//        CookieSyncManager.getInstance().sync();

//        settings.setAllowContentAccess(true);//default is true
//        settings.setSupportZoom(false);
//        settings.setBuiltInZoomControls(false);
//        settings.setUseWideViewPort(true);// new
//        settings.setSupportMultipleWindows(false);//default is false
//        settings.setLoadWithOverviewMode(true);
//        settings.setAppCacheEnabled(true);// 应用可以有缓存 default is false
//        settings.setDatabaseEnabled(true);//是否开启database storage API default is false
        settings.setDomStorageEnabled(true);// 设置可以使用DOM storage API default is false (影响页面显示！)
//        settings.setGeolocationEnabled(true);// 启用地理定位 default is true
//        settings.setAppCacheMaxSize(Long.MAX_VALUE); // new
//        settings.setAppCachePath(getContext().getDir("appcache", 0).getPath());// new
//        settings.setDatabasePath(getContext().getDir("databases", 0).getPath());// new
//        settings.setGeolocationDatabasePath(mContext.getDir("geolocation", 0).getPath());// new
//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//
        WebViewClient client = new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
////                loadUrl(url);
////                return true;
//                return super.shouldOverrideUrlLoading(view, url);
//            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受信任所有网站的证书
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

//                String js = "var loginf = document.getElementById('login_content_wrapper');";
//                js += "if (loginf){loginf.style.position = 'inherit';}";
//                js += "var areaSelect = document.getElementById('Js-area-select');";
//                js += "if (areaSelect){areaSelect.style.position = 'fixed';}";
//                view.evaluateJavascript(js, null);
            }
        };
        setWebViewClient(client);

        //this is where you fixed your code I guess
//And also by setting a WebClient to catch javascript's console messages :

//        setWebChromeClient(new WebChromeClient() {
//            public boolean onConsoleMessage(ConsoleMessage cm) {
//                Log.d("Stefan", cm.message() + " -- From line "
//                        + cm.lineNumber() + " of "
//                        + cm.sourceId());
//                return true;
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && canGoBack()) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
