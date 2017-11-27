package com.nhzw.shopping.activity;

import android.arch.lifecycle.Observer;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kepler.jd.Listener.OpenAppAction;
import com.kepler.jd.login.KeplerApiManager;
import com.kepler.jd.sdk.bean.KelperTask;
import com.kepler.jd.sdk.exception.KeplerBufferOverflowException;
import com.nhzw.base.BaseActivity;
import com.nhzw.component.lib.route.Router;
import com.nhzw.component.lib.service.ServiceManager;
import com.nhzw.service.AppService;
import com.nhzw.service.LoginService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.adapter.DetailAdapter;
import com.nhzw.shopping.model.entity.GoodsDetail;
import com.nhzw.shopping.viewmodel.DetailViewModel;
import com.nhzw.utils.UMUtil;
import com.xinayida.lib.annotation.AFInject;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ww on 2017/10/19.
 */

@AFInject(contentViewId = R.layout.activity_detail, enableSlidr = true, btnBackId = R.id.toolbar_back)
public class DetailActivity extends BaseActivity<DetailViewModel> {

    @BindView(R.id.detail_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.detail_price)
    TextView price;
    @BindView(R.id.detail_price_sec)
    TextView oriPrice;
    @BindView(R.id.detail_praise_rate)
    TextView praiseRate;

    @BindView(R.id.network_error_detail)
    View netErrorView;

    @BindView(R.id.bottom_layout)
    View bottomLayout;

    private GoodsDetail goodsDetail;
    private KelperTask mKelperTask;

    @Override
    public void onResume() {
        super.onResume();
        UMUtil.submitCustomEvent("goods_detailed_list_page_show", "展示时间戳", String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void onPause() {
        super.onPause();
        UMUtil.submitCustomEvent("goods_detailed_list_page_secede", "退出时间戳", String.valueOf(System.currentTimeMillis()));
    }

    @Override
    protected void setup(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        if (id == null && savedInstanceState != null) {
            id = savedInstanceState.getString("id");
        }
        final DetailAdapter adapter = new DetailAdapter(viewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        showLoading();
        viewModel.getGoodsDetail().observe(this, new Observer<GoodsDetail>() {
            @Override
            public void onChanged(@Nullable GoodsDetail detail) {
                hideLoading();
                if (detail.id == null) {
                    netErrorView.setVisibility(View.VISIBLE);
                } else {
                    bottomLayout.setVisibility(View.VISIBLE);
                    goodsDetail = detail;
                    price.setText("¥" + detail.price);
                    oriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    oriPrice.setText("¥" + detail.originalPrice);
                    praiseRate.setText(detail.goodRateStr + "好评 (" + detail.sourcesTypeStr + ")");

                    ArrayList<GoodsDetail> data = new ArrayList<>();
                    data.add(detail);
                    data.add(detail);
                    adapter.update(data);
                }
            }
        });
        viewModel.getGoodsDetail(id);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (goodsDetail != null) {
            outState.putString("id", goodsDetail.id);
        }
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.detail_buy, R.id.toolbar_right})
    public void Buy(View view) {
        if (goodsDetail == null || goodsDetail.id == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.detail_buy) {
            if (AppService.instance.noNetworkToast()) return;
            if ("CUSTOM".equals(goodsDetail.sourcesType)) {
                Bundle bundle = new Bundle();
                bundle.putString("url", goodsDetail.promotionUrl);
                Router.getInstance().open(this, XWebViewActivity.class, bundle);
            } else {
                UMUtil.submitCustomEvent("goods_detailed_shop");
                try {
//                    KeplerApiManager.getWebViewService().openItemDetailsWebViewPage(goodsDetail.platformId, "hippo");
                    mKelperTask = KeplerApiManager
                            .getWebViewService()
                            .openItemDetailsPage(goodsDetail.platformId,
                                    "hippo", context, new OpenAppAction() {
                                        @Override
                                        public void onStatus(int status) {
                                            if (status == OpenAppAction.OpenAppAction_start) {
                                                showLoading();
                                            } else {
                                                hideLoading();
                                                mKelperTask = null;
                                            }
                                        }
                                    }, 10);
                } catch (KeplerBufferOverflowException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (id == R.id.toolbar_right) {
//            AppUtils.shareMsg(this, null, "分享", goodsDetail.shareUrl, null);
            ServiceManager.getInstance().getService(LoginService.class)
                    .showShare(goodsDetail.shareUrl, goodsDetail.name, getString(R.string.share_detail_msg), BitmapFactory.decodeResource(getResources(), R.drawable.icon));

        }
    }

    @Override
    protected void onDestroy() {
        if (mKelperTask != null) {
            mKelperTask.isCancel();
            mKelperTask = null;
        }
        super.onDestroy();
    }
}
