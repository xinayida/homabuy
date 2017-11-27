package com.nhzw.shopping.adapter;

import android.view.View;

import com.nhzw.base.BaseAdapter;
import com.nhzw.base.BaseHolder;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.entity.GoodsDetail;
import com.nhzw.shopping.viewmodel.DetailViewModel;

/**
 * Created by ww on 2017/10/25.
 */

public class DetailAdapter extends BaseAdapter<GoodsDetail> {
    DetailViewModel viewModel;

    public DetailAdapter(DetailViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public BaseHolder<GoodsDetail> getHolder(View v, int viewType) {
        if (viewType == 0) {
            return new DetailInfoHolder(v, viewModel);
        } else {
            return new DetailWebViewHolder(v);
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.detail_infoview;
        } else {
            return R.layout.detail_webview;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
