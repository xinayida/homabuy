package com.nhzw.shopping.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhzw.base.BaseHolder;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.entity.DemoUser;
import com.nhzw.utils.ImageLoader;

import butterknife.BindView;

/**
 * Created by ww on 2017/9/18.
 */

public class UserItemHolder extends BaseHolder<DemoUser> {
    @BindView(R.id.iv_avatar)
    ImageView mAvater;
    @BindView(R.id.tv_name)
    TextView mName;

    public UserItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(DemoUser data, int position) {
        mName.setText(data.getLogin());
        ImageLoader.loadImage(data.getAvatarUrl(), mAvater);
    }

    @Override
    protected void onRelease() {
       ImageLoader.clear(mAvater);
    }
}
