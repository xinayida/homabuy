package com.nhzw.shopping.activity;

import android.os.Bundle;

import com.nhzw.base.BaseActivity;
import com.nhzw.shopping.R;
import com.nhzw.widget.XWebView;
import com.xinayida.lib.annotation.AFInject;

import butterknife.BindView;

/**
 * Created by ww on 2017/11/7.
 */

@AFInject(contentViewId = R.layout.activity_xwebview, enableSlidr = true)
public class XWebViewActivity extends BaseActivity {
    @BindView(R.id.webview)
    XWebView webView;

    @Override
    protected void setup(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}
