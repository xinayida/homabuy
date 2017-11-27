package com.nhzw.shopping.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhzw.base.BaseHolder;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.entity.GoodsInfo;
import com.nhzw.utils.ImageLoader;

import butterknife.BindView;

/**
 * Created by ww on 2017/10/23.
 */

public class LikeItemHolder extends BaseHolder<GoodsInfo> {

    @BindView(R.id.delete)
    View delBtn;

    @BindView(R.id.content)
    View content;

    @BindView(R.id.iv_itemLikeGoods_picture)
    ImageView image;

    @BindView(R.id.tv_itemLikeGoods_goodsName)
    TextView name;

    @BindView(R.id.tv_itemLikeGoods_discountPrice)
    TextView price;

    @BindView(R.id.tv_itemLikeGoods_originalPrice)
    TextView oriPrice;

    @BindView(R.id.win_layout)
    View winLayout;


    public LikeItemHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(GoodsInfo data, int position) {
        name.setText(data.name);
        price.setText("¥" + data.price);
        oriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        oriPrice.setText("¥" + data.originalPrice);
        ImageLoader.loadImage(data.image, image);
        delBtn.setOnClickListener(this);
        content.setOnClickListener(this);
        name.setTextColor(content.getResources().getColor(R.color.commonFontColor_black));
        if ("NORMAL".equalsIgnoreCase(data.winType)) {
            winLayout.setVisibility(View.GONE);
        } else {
            winLayout.setVisibility(View.VISIBLE);
            if ("WIN".equalsIgnoreCase(data.winType)) {
                name.setTextColor(Color.parseColor("#FE765D"));
            }
        }
    }

    @Override
    protected void onRelease() {
        ImageLoader.clear(image);
    }
}
