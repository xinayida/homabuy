package com.nhzw.shopping.adapter;

import android.view.View;

import com.nhzw.base.BaseAdapter;
import com.nhzw.base.BaseHolder;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.entity.DemoUser;

/**
 * Created by ww on 2017/9/18.
 */

public class UserAdapter extends BaseAdapter<DemoUser> {
    public UserAdapter() {
        super();
    }

    @Override
    public BaseHolder<DemoUser> getHolder(View v, int viewType) {
        return new UserItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.demo_list_item;
    }
}
