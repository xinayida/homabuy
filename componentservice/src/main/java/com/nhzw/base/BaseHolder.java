package com.nhzw.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nhzw.utils.BindUtils;

/**
 * Created by ww on 2017/9/15.
 */

public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener{
    protected OnViewClickListener mOnViewClickListener = null;
    protected final String TAG = this.getClass().getSimpleName();

    public BaseHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);//点击事件
        BindUtils.bindTarget(this, itemView);
    }

    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getPosition());
        }
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }

    /**
     * 设置数据
     * 刷新界面
     *
     * @param
     * @param position
     */
    public abstract void setData(T data, int position);

    /**
     * 释放资源
     */
    protected void onRelease() {
        mOnViewClickListener = null;
    }

}
