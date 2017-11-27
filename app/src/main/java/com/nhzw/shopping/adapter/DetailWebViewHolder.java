package com.nhzw.shopping.adapter;

import android.view.View;
import android.webkit.WebView;

import com.nhzw.base.BaseHolder;
import com.nhzw.shopping.model.entity.GoodsDetail;

/**
 * Created by ww on 2017/10/25.
 */

public class DetailWebViewHolder extends BaseHolder<GoodsDetail> {
    public DetailWebViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(GoodsDetail data, int position) {
        ((WebView) itemView).loadUrl(data.detailsUrl);
    }
}
