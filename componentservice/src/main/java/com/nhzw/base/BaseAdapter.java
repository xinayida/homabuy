package com.nhzw.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ww on 2017/9/15.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder<T>> {
    protected final int DEFAULT_ITEM_TYPE = 1000;
    protected List<T> infos = new ArrayList<T>();
    protected OnRecyclerViewItemClickListener onItemClickListener = null;
    private BaseHolder<T> holder;
    private boolean isEmpty;

    public void update(List<T> infos) {
        this.infos = new ArrayList<T>(infos);
        isEmpty = this.infos.isEmpty();
        notifyDataSetChanged();
    }

    public void append(List<T> infos) {
        int preIndex = this.infos.size();
        this.infos = new ArrayList<T>(infos);
        isEmpty = this.infos.isEmpty();
        int count = infos.size() - preIndex;
        notifyItemRangeInserted(preIndex, count);
    }

    public void delete(int position) {
        if (infos.size() > 0 && position >= 0 && position < infos.size()) {
            infos.remove(position);
            isEmpty = infos.isEmpty();
            if (isEmpty) {
                notifyDataSetChanged();
            } else {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, infos.size());
            }
        }
    }

    /**
     * 创建Hodler
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseHolder<T> onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == DEFAULT_ITEM_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(getDefaultLayoutId(), parent, false);
            holder = new DefaultHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
            holder = getHolder(view, viewType);
            if (onItemClickListener != null) {
                holder.setOnItemClickListener(new BaseHolder.OnViewClickListener() {//设置Item点击事件
                    @Override
                    public void onViewClick(View view, int position) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(view, viewType, infos.get(position), position);
                        }
                    }
                });
            }
        }

        return holder;
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseHolder<T> holder, int position) {
        if (!isEmpty)
            holder.setData(infos.get(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getDefaultLayoutId() != 0 && isEmpty && position == 0) {
            return DEFAULT_ITEM_TYPE;
        }
        return super.getItemViewType(position);
    }

    /**
     * 数据的个数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (isEmpty && getDefaultLayoutId() != 0) {
            return 1;
        }
        return infos == null ? 0 : infos.size();
    }


    public List<T> getInfos() {
        return infos;
    }

    /**
     * 获得item的数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        if (isEmpty) {
            return null;
        }
        return infos == null ? null : infos.get(position);
    }

    /**
     * 子类实现提供holder
     *
     * @param v
     * @param viewType
     * @return
     */
    public abstract BaseHolder<T> getHolder(View v, int viewType);

    /**
     * 提供Item的布局
     *
     * @param viewType
     * @return
     */
    public abstract int getLayoutId(int viewType);

    public int getDefaultLayoutId() {
        return 0;
    }


    /**
     * 使用原生方法来释放holder的资源
     * @param holder
     */
    @Override
    public void onViewRecycled(BaseHolder<T> holder) {
//        Log.d("Stefan", "onViewRecycled " + holder.toString());
        holder.onRelease();
    }

    public interface OnRecyclerViewItemClickListener<T> {
        void onItemClick(View view, int viewType, T data, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
