package com.nhzw.shopping.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhzw.base.BaseHolder;
import com.nhzw.service.AppService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.entity.GoodsDetail;
import com.nhzw.shopping.viewmodel.DetailViewModel;
import com.nhzw.utils.ImageLoader;
import com.nhzw.utils.UMUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import ezy.ui.view.BannerView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ww on 2017/10/25.
 */

public class DetailInfoHolder extends BaseHolder<GoodsDetail> {
    @BindView(R.id.detail_banner)
    BannerView<String> banner;
    @BindView(R.id.detail_title)
    TextView title;
    @BindView(R.id.detail_shop_icon)
    ImageView shopIcon;
    @BindView(R.id.detail_shop_name)
    TextView shopName;
    @BindView(R.id.detail_dislike)
    View disLike;
    @BindView(R.id.detail_like)
    View like;

    private GoodsDetail goodsDetail;
    private DetailViewModel viewModel;

    public DetailInfoHolder(View itemView, DetailViewModel viewModel) {
        super(itemView);
        this.viewModel = viewModel;
    }

    @Override
    public void setData(GoodsDetail data, int position) {
        goodsDetail = data;
        banner.setViewFactory(new BannerView.ViewFactory<String>() {
            @Override
            public View create(String imgUrl, int position, ViewGroup container) {
                ImageView imageView = new ImageView(itemView.getContext());
                ImageLoader.loadImage(imgUrl, imageView);
                return imageView;
            }
        });
        banner.setDataList(Arrays.asList(data.images));
        banner.start();
        title.setText(data.name);
        shopName.setText(data.shopName);
        ImageLoader.loadImage(data.shopLogo, shopIcon);
    }

    @Override
    protected void onRelease() {
    }

    @OnClick({R.id.detail_like, R.id.detail_dislike})
    public void onClickLike(View v) {
        if (AppService.instance.noNetworkToast())// 判断有无网络。 —— 由姚宇编辑于2017/11/10.
            return;
        switch (v.getId()) {
            case R.id.detail_like:
                UMUtil.submitCustomEvent("goods_detailed_click_like_button");
                AppService.instance.showToast("已加入心愿单");
                animLike(true);
                break;
            case R.id.detail_dislike:
                UMUtil.submitCustomEvent("goods_detailed_click_dislike_button");
                animLike(false);
                AppService.instance.getCurActivity().setResult(RESULT_OK);
                AppService.instance.getCurActivity().finish();
                break;
        }
    }

    private void animLike(boolean lORr) {
        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1f,
                0.2f, 1f);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1,
                1.3f, 1);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1,
                1.3f, 1);
        View v = null;
        if (lORr) {
            v = like;
        } else {
            v = disLike;
        }
        viewModel.like(goodsDetail.id, lORr);
        ObjectAnimator.ofPropertyValuesHolder(v, pvhA, pvhX, pvhY).setDuration(500).start();
    }
}
