package com.nhzw.shopping.adapter;

import android.view.View;

import com.nhzw.base.BaseAdapter;
import com.nhzw.base.BaseHolder;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.entity.GoodsInfo;

/**
 * Created by ww on 2017/10/23.
 */

public class LikeAdapter extends BaseAdapter<GoodsInfo> {
    public LikeAdapter() {
        super();
    }

    @Override
    public BaseHolder<GoodsInfo> getHolder(View v, int viewType) {
        final LikeItemHolder holder = new LikeItemHolder(v);
        return holder;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_like_goods;
    }

    @Override
    public int getDefaultLayoutId() {
        return R.layout.default_like;
    }
}
