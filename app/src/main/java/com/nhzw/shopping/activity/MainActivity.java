package com.nhzw.shopping.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.mcxtzhang.commonadapter.viewgroup.VGUtil;
import com.mcxtzhang.commonadapter.viewgroup.adapter.cache.ViewHolder;
import com.mcxtzhang.commonadapter.viewgroup.adapter.single.SingleAdapter;
import com.nhzw.base.BaseActivity;
import com.nhzw.base.BaseAdapter;
import com.nhzw.base.BaseViewModel;
import com.nhzw.component.lib.route.Router;
import com.nhzw.component.lib.service.ServiceManager;
import com.nhzw.rx.RxUtils;
import com.nhzw.service.AppService;
import com.nhzw.service.LoginService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.adapter.LikeAdapter;
import com.nhzw.shopping.model.entity.GoodsInfo;
import com.nhzw.shopping.viewmodel.MainViewModel;
import com.nhzw.utils.ImageLoader;
import com.nhzw.utils.ScrollUtils;
import com.nhzw.utils.UMUtil;
import com.nhzw.widget.stacklayout.StackLayout;
import com.nhzw.widget.stacklayout.transformer.StackPageTransformer;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.xinayida.lib.annotation.AFInject;
import com.xinayida.lib.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Action;

/**
 * Created by ww on 2017/9/29.
 */

@AFInject(contentViewId = R.layout.activity_main)
public class MainActivity extends BaseActivity<MainViewModel> implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @BindView(R.id.drawer_main)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar_left)
    ImageView left;

    @BindView(R.id.toolbar_right)
    ImageView right;

    @BindView(R.id.toolbar_title)
    TextView title;

    @BindView(R.id.iv_viewUserInfo_icon)
    ImageView userInfoIcon;

    @BindView(R.id.tv_viewUserInfo_userName)
    TextView userInfoName;

    @BindView(R.id.stack_layout)
    StackLayout stackLayout;

    @BindView(R.id.btn_like)
    View like;

    @BindView(R.id.btn_dislike)
    View disLike;

    @BindView(R.id.toolbar_right_anim)
    View navLike;

    @BindView(R.id.rv_viewLikeGoods_list)
    RecyclerView likeRecyclerView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.toolbar_red_point)
    View redPoint;

    @BindView(R.id.fill_contact_info)
    View fillContactInfo;

    @BindView(R.id.default_main_layout)
    View defaultView;

    @BindView(R.id.report)
    TextView report;

    @BindView(R.id.network_error_main)
    View netErrorViewMain;

    @BindView(R.id.network_error_like)
    View netErrorViewWish;

    private SingleAdapter<GoodsInfo> adapter;
    private LikeAdapter likeAdapter;
    private boolean loadingMore;


    //    @Override
//    protected boolean onInterceptCreate() {
//        String token = SpUtil.getInstance().readString("token");
//        if (token == null) {
//            Router.getInstance().open(this, LoginActivity.class);
//        }
//        return false;
//    }
    @Override
    public void onResume() {
        super.onResume();
        showGuide();
        UMUtil.submitCustomEvent("goods_list_page_show", "展示时间戳", String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void onPause() {
        super.onPause();
        UMUtil.submitCustomEvent("goods_list_page_secede", "退出时间戳", String.valueOf(System.currentTimeMillis()));
    }

    private void showGuide() {
        if (AppService.instance.isLogined() && SpUtil.getInstance().readBoolean("first_main", true)) {
            SpUtil.getInstance().writeBoolean("first_main", false);
            NiceDialog.init().setLayoutId(R.layout.guide_dialog_2).setConvertListener(new ViewConvertListener() {
                @Override
                protected void convertView(com.othershe.nicedialog.ViewHolder viewHolder, BaseNiceDialog dialog) {
                    viewHolder.getConvertView().setOnClickListener(v -> {
                        dialog.dismiss();
                        NiceDialog.init().setLayoutId(R.layout.guide_dialog_1).setConvertListener(new ViewConvertListener() {
                            @Override
                            protected void convertView(com.othershe.nicedialog.ViewHolder viewHolder, BaseNiceDialog dialog) {
                                viewHolder.getConvertView().setOnClickListener(v1 -> dialog.dismiss());
                            }
                        }).setHeight(-2).show(getSupportFragmentManager());
                    });
                }
            }).setHeight(-2).show(getSupportFragmentManager());
        }
    }

    @Override
    protected void setup(Bundle savedInstanceState) {
        initRecycleView();
        setObservers();
        left.setImageResource(R.drawable.nav_menu);
        title.setText("猜你喜欢");
        right.setImageResource(R.drawable.nav_like);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭手势
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (drawerLayout.isDrawerOpen(Gravity.END)) {
                    UMUtil.submitCustomEvent("wish_list_page_show", "展示时间戳", String.valueOf(System.currentTimeMillis()));
                    viewModel.hasContactInfo();
                    viewModel.loadLikeList(true);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (!drawerLayout.isDrawerOpen(Gravity.END)) {
                    fillContactInfo.setVisibility(View.GONE);
                    UMUtil.submitCustomEvent("wish_list_page_secede", "退出时间戳", String.valueOf(System.currentTimeMillis()));
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        stackLayout.addPageTransformer(new StackPageTransformer());
        stackLayout.setOnRemoveListener(new StackLayout.OnRemoveListener() {
            @Override
            public void onRemoved(boolean isSwipeUp, int itemLeft) {
                if (!isSwipeUp) {// 向下划
                    final View lastChild = stackLayout.getChildAt(adapter.getCount() - 1);
                    if (lastChild != null) {
                        ObjectAnimator anim = ObjectAnimator.ofFloat(lastChild.findViewById(R.id.tv_card_title), "alpha", 0.0f, 1.0f);
                        anim.setDuration(500);
                        anim.start();
                        anim = ObjectAnimator.ofFloat(lastChild.findViewById(R.id.layout_bottom), "alpha", 0.0f, 1.0f);
                        anim.setDuration(500);
                        anim.start();
                    }
                } else {
                    navLike.setVisibility(View.VISIBLE);
                    PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1, 1.6f);
                    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1, 1.6f);
                    ObjectAnimator.ofPropertyValuesHolder(navLike, pvhA, pvhX, pvhY).setDuration(500).start();
                }
                if (itemLeft < 6 && !viewModel.goodsListEnd) {
//                    String lastid = adapter.getCount() > 0 ? adapter.getDatas().get(0).id : null;
//                    viewModel.loadGoodsList(lastid, null);//TODO 检查是否还有重复调用问题
                    viewModel.loadGoodsList();
                }
                if (itemLeft <= 0) {
                    showDefaultView();
                }
            }

            @Override
            public void onStart(boolean isSwipeUp) {
//                viewModel.setCurId(adapter.getDatas().get(adapter.getCount() - 1).id, adapter.getCount());
                animLike(!isSwipeUp);
            }

            @Override
            public void onClick() {
                if (drawerLayout.isDrawerOpen(Gravity.START) || drawerLayout.isDrawerOpen(Gravity.END)) {
                    return;
                }
                if (adapter != null && adapter.getCount() > 0) {
                    GoodsInfo goodsInfo = adapter.getDatas().get(adapter.getCount() - 1);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", goodsInfo.id);
                    Router.getInstance().open(MainActivity.this, DetailActivity.class, bundle, 0);
                    UMUtil.submitCustomEvent("click_goods_show");
                }
            }
        });
        adapter = new SingleAdapter<GoodsInfo>(this, new ArrayList<>(), R.layout.item_main) {
            @Override
            public void onBindViewHolder(ViewGroup viewGroup, ViewHolder viewHolder, GoodsInfo info, int i) {
                if (viewHolder.itemView.getTag(R.id.tag_id) == null || !viewHolder.itemView.getTag(R.id.tag_id).equals(info.id)) {
                    ImageLoader.loadImage(info.image, viewHolder.getView(R.id.iv_card));
                    viewHolder.setText(R.id.tv_card_title, info.name);
                    viewHolder.itemView.setTag(R.id.tag_id, info.id);

                    viewHolder.setText(R.id.tv_price, "¥" + info.price);
                    TextView priceSec = viewHolder.getView(R.id.tv_price_sec);
                    priceSec.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    priceSec.setText("¥" + info.originalPrice);
                    viewHolder.setText(R.id.tv_praise_rate, info.goodRateStr + "好评");
                }
            }

            public void recycleView(ViewGroup parent, ViewHolder holder) {
                try {
                    parent.removeView(holder.itemView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.mViewCache.put(holder);
            }
        };
        stackLayout.setAdapter(adapter);
        new VGUtil.Builder().setParent(stackLayout).setAdapter(adapter).build();

        report.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        report.getPaint().setAntiAlias(true);//抗锯齿

    }

    private void initRecycleView() {
        refreshLayout.setOnRefreshListener(this);
        likeAdapter = new LikeAdapter();
        likeRecyclerView.setAdapter(likeAdapter);
        likeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        likeRecyclerView.setHasFixedSize(true);
        likeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        likeAdapter.setOnItemClickListener(new BaseAdapter.OnRecyclerViewItemClickListener<GoodsInfo>() {
            @Override
            public void onItemClick(View view, int viewType, GoodsInfo goodsInfo, int position) {
                if (view.getId() == R.id.content) {
                    if (!GoodsInfo.TYPE_NORMAL.equalsIgnoreCase(goodsInfo.winType)) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("展示时间戳", String.valueOf(System.currentTimeMillis()));
                        map.put("弹窗位置ID", "2");
                        map.put("用户ID", String.valueOf(AppService.instance.getUid()));
                        map.put("商品ID", goodsInfo.id);
                        UMUtil.submitCustomEvent("lottery_Dialogs_show", map);
                        showNotify(goodsInfo);
                        if (GoodsInfo.TYPE_WIN.equalsIgnoreCase(goodsInfo.winType)) {
                            viewModel.notifyWin(goodsInfo.id);
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", goodsInfo.id);
                        Router.getInstance().open(MainActivity.this, DetailActivity.class, bundle, 1);
                    }
                } else if (view.getId() == R.id.delete) {
                    UMUtil.submitCustomEvent("wish_list_page_delete_button_click", "心愿单列表页：点击删除按钮", goodsInfo.id);
                    viewModel.removeLike(goodsInfo.id);
                    likeAdapter.delete(position);
                }
            }
        });
        final ScrollUtils.Callbacks callbacks = new ScrollUtils.Callbacks() {
            @Override
            public void onLoadMore() {
//                Log.d("Stefan", "loadMore " + likeAdapter.getItemCount() + "  " + viewModel.timeStamp);
                if (likeAdapter.getItemCount() > 0 && !viewModel.likeLoadAll) {
                    viewModel.loadLikeList(false);
                }
            }

            @Override
            public boolean isLoading() {
                return loadingMore;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return viewModel.likeLoadAll;
            }
        };
        likeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ScrollUtils.checkEndOffset(likeRecyclerView, callbacks);
            }
        });
    }

    private void showNotify(final GoodsInfo info) {
        NiceDialog.init().setLayoutId(R.layout.dialog_wish)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(com.othershe.nicedialog.ViewHolder viewHolder, BaseNiceDialog dialog) {
                        ImageLoader.loadImage(info.image, viewHolder.getView(R.id.wish_icon));
                        viewHolder.setText(R.id.wish_title, info.name);
                        viewHolder.setText(R.id.wish_price, info.price);
                        TextView orignalPrice = viewHolder.getView(R.id.wish_price_original);
                        orignalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        orignalPrice.setText(info.originalPrice);
                        viewHolder.setOnClickListener(R.id.wish_btn_close, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UMUtil.submitCustomEvent("lottery_Dialogs_click_secede_button", "退出时间戳",
                                        String.valueOf(System.currentTimeMillis()));
                                dialog.dismiss();
                            }
                        });
                        viewHolder.setOnClickListener(R.id.wish_btn_detail, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UMUtil.submitCustomEvent("lottery_Dialogs_click_detailed_button");
                                dialog.dismiss();
                                if (AppService.instance.noNetworkToast(getString(R.string.network_error_simple)))
                                    return;
                                Bundle bundle = new Bundle();
                                bundle.putString("id", info.id);
                                Router.getInstance().open(MainActivity.this, DetailActivity.class, bundle);
                            }
                        });
                        viewHolder.setOnClickListener(R.id.wish_btn_share, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ServiceManager.getInstance().getService(LoginService.class).showShare(info.shareWinUrl, getString(R.string.share_win_title), getString(R.string.share_win_msg), BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                            }
                        });
                    }
                })
                .setWidth(270)
                .setHeight(360)
                .setOutCancel(false)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getSupportFragmentManager());
    }

    private void showDefaultView() {
        defaultView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        netErrorViewMain.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        netErrorViewMain.setVisibility(View.GONE);
    }

    private void showWishError() {
        netErrorViewWish.setVisibility(View.VISIBLE);
    }

    private void hideWishError() {
        netErrorViewWish.setVisibility(View.GONE);
    }

    private boolean loadPrize;

    private void setObservers() {
        viewModel.getAccountInfoMutable().observe(this, accountInfo -> {
            if (accountInfo.token == null) {
                userInfoIcon.setImageResource(R.drawable.icon_head);
                userInfoName.setText("未登录");
            } else {
                ImageLoader.loadCircleImage(accountInfo.headImg, userInfoIcon);
                userInfoName.setText(accountInfo.nickname);
                loadData();
            }
        });
        //主列表
        viewModel.getLoadingStatus().observe(this, state -> {
            switch (state) {
                case BaseViewModel.LOADINGMORE:
                case BaseViewModel.REFRESHING:
                    if (adapter.getCount() == 0) {
                        showLoading();
                    }
                    break;
                case BaseViewModel.LOADMORE_FAILED:
                case BaseViewModel.REFRESH_FAILED:
                    showError();
                default:
                    hideLoading();
                    break;
            }
        });
        viewModel.getGoodsList().observe(this, goods -> {
            if (adapter.getCount() == 0 && goods.size() == 0) {
                showDefaultView();
                return;
            }
            if (!loadPrize) {
                loadPrize = true;
                viewModel.loadPrizeList();
            }
            hideError();
            adapter.setDatas(goods);
            stackLayout.transformPage();
        });
        //心愿列表列表
        viewModel.getLikeLoadingStatus().observe(this, state -> {
            switch (state) {
                case BaseViewModel.LOADINGMORE:
                    loadingMore = true;
                case BaseViewModel.REFRESHING:
                    if (likeAdapter.getItemCount() == 0) {
                        refreshLayout.setRefreshing(true);
                    }
                    break;
                case BaseViewModel.LOADMORE_FAILED:
                case BaseViewModel.REFRESH_FAILED:
                    showWishError();
                default:
                    loadingMore = false;
                    RxUtils.runOnUIDelay(new Action() {
                        @Override
                        public void run() throws Exception {
                            refreshLayout.setRefreshing(false);
                        }
                    }, 1000).subscribe();
                    break;
            }
        });

        viewModel.getLikeListData().observe(this, new Observer<List<GoodsInfo>>() {
            @Override
            public void onChanged(@Nullable List<GoodsInfo> goodsInfos) {
                hideWishError();
                if (goodsInfos.isEmpty()) {
                    return;
                }
                if (viewModel.pullToRefreshLike || likeAdapter.getItemCount() == 0) {
                    likeAdapter.update(goodsInfos);
                } else {
                    likeAdapter.append(goodsInfos);
                }
            }
        });

        viewModel.getHasPrize().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasPrize) {
                if (hasPrize) {
                    redPoint.setVisibility(View.VISIBLE);
                } else {
                    redPoint.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getPrizeList().observe(this, new Observer<List<GoodsInfo>>() {
            @Override
            public void onChanged(@Nullable List<GoodsInfo> goodsInfos) {
                if (goodsInfos.size() > 0) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("展示时间戳", String.valueOf(System.currentTimeMillis()));
                    map.put("弹窗位置ID", "1");
                    map.put("用户ID", String.valueOf(AppService.instance.getUid()));
                    map.put("商品ID", goodsInfos.get(0).id);
                    UMUtil.submitCustomEvent("lottery_Dialogs_show", map);
                    showNotify(goodsInfos.get(0));
                    viewModel.notifyWin(goodsInfos.get(0).id);
                    if (goodsInfos.size() == 1) {
                        redPoint.setVisibility(View.GONE);
                    }
                }
            }
        });
        viewModel.getHasContactInfo().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasContactInfo) {
                if (!hasContactInfo) {
                    fillContactInfo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadData() {
        viewModel.loadGoodsList();
//        viewModel.loadGoodsList(viewModel.getCurId(), viewModel.getItemLeft());
        viewModel.hasPrize();
    }

    @Override
    public void onRefresh() {
        UMUtil.submitCustomEvent("wish_list_page_pull_down_to_refresh");
        viewModel.loadLikeList(true);
    }

    @OnClick({R.id.toolbar_left, R.id.toolbar_right,
//            R.id.iv_viewUserInfo_icon,
            R.id.ll_viewUserInfo_home,
            R.id.ll_viewUserInfo_setting,
//            R.id.ll_viewUserInfo_bonusScene,
            R.id.ll_viewUserInfo_feedback,
            R.id.ll_viewUserInfo_share,
            R.id.ll_viewUserInfo_about,
            R.id.btn_dislike, R.id.btn_like,
            R.id.fill_contact_info,
            R.id.report,
            R.id.micro_shop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_left:// 用户中心按钮。
                if (!drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.openDrawer(Gravity.START);
                }
                break;
            case R.id.toolbar_right:// 心愿单按钮。
                if (!drawerLayout.isDrawerOpen(Gravity.END)) {
                    drawerLayout.openDrawer(Gravity.END);
                }
                redPoint.setVisibility(View.GONE);
                UMUtil.submitCustomEvent("click_wish_button");
                break;
//            case R.id.iv_viewUserInfo_icon://头像
//                break;
            case R.id.ll_viewUserInfo_home://首页
                UMUtil.submitCustomEvent("personal_center_click_home_button");
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.ll_viewUserInfo_setting://设置
                if (AppService.instance.noNetworkToast()) return;
                UMUtil.submitCustomEvent("personal_center_click_Settings_button");
                Router.getInstance().open(this, SettingActivity.class);
                drawerLayout.closeDrawer(Gravity.START);
                break;
//            case R.id.ll_viewUserInfo_bonusScene://菜单管理
//                drawerLayout.closeDrawer(Gravity.START);
//                break;
            case R.id.ll_viewUserInfo_feedback://用户反馈
                if (AppService.instance.noNetworkToast()) return;
                UMUtil.submitCustomEvent("personal_center_click_Contact_user");
                FeedbackAPI.openFeedbackActivity();
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.ll_viewUserInfo_about://关于
                UMUtil.submitCustomEvent("personal_center_click_about_user");
                Router.getInstance().open(this, AboutActivity.class);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.ll_viewUserInfo_share://推荐给好友
                UMUtil.submitCustomEvent("personal_center_click_share_user");
                drawerLayout.closeDrawer(Gravity.START);
                ServiceManager.getInstance().getService(LoginService.class).showShare(AppService.SHARE_LINK, getString(R.string.app_name), getString(R.string.share_to_friend_msg), BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                break;
            case R.id.btn_dislike:
                if (AppService.instance.noNetworkToast()) return;
                stackLayout.slide(false);
                UMUtil.submitCustomEvent("click_dislike_button");
                break;
            case R.id.btn_like:
                if (AppService.instance.noNetworkToast()) return;
                stackLayout.slide(true);
                UMUtil.submitCustomEvent("click_like_button");
                break;
            case R.id.fill_contact_info:
                drawerLayout.closeDrawer(Gravity.END);
                Router.getInstance().open(this, AddAddressActivity.class);
                break;
            case R.id.micro_shop:
                UMUtil.submitCustomEvent("personal_center_click_shop_banner");
                drawerLayout.closeDrawer(Gravity.START);
                Bundle bundle = new Bundle();
                bundle.putString("url", "https://weidian.com/s/1279340023?wfr=c&ifr=shopdetail");
                Router.getInstance().open(MainActivity.this, XWebViewActivity.class, bundle);
                break;
            case R.id.report:
                FeedbackAPI.openFeedbackActivity();
                break;
        }
    }

    @Override
    protected void onBack() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBack();
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
            v = disLike;
        } else {
            v = like;
        }
        if (adapter.getCount() > 0) {
            GoodsInfo goodsInfo = adapter.getDatas().get(adapter.getCount() - 1);
            viewModel.like(goodsInfo.id, !lORr);
        }
        ObjectAnimator.ofPropertyValuesHolder(v, pvhA, pvhX, pvhY).setDuration(500).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            stackLayout.slide(false);
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            viewModel.loadLikeList(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void initVisualPanel() {
//        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
//                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        //判断当前版本在4.0以上并且存在虚拟按键，否则不做操作
//        if (Build.VERSION.SDK_INT >= 19 && checkDeviceHasNavigationBar()) {
//            //一定要判断是否存在按键，否则在没有按键的手机调用会影响别的功能。如之前没有考虑到，导致图传全屏变成小屏显示。
//            decorView.setSystemUiVisibility(flag);
//        }
//    }
//
//    /**
//     * 判断是否存在虚拟按键
//     *
//     * @return
//     */
//    public boolean checkDeviceHasNavigationBar() {
//        boolean hasNavigationBar = false;
//        Resources rs = getResources();
//        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
//        if (id > 0) {
//            hasNavigationBar = rs.getBoolean(id);
//        }
//        try {
//            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
//            Method m = systemPropertiesClass.getMethod("get", String.class);
//            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
//            if ("1".equals(navBarOverride)) {
//                hasNavigationBar = false;
//            } else if ("0".equals(navBarOverride)) {
//                hasNavigationBar = true;
//            }
//        } catch (Exception e) {
//
//        }
//        return hasNavigationBar;
//    }
}
