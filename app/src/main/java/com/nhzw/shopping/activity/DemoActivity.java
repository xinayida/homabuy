//package com.nhzw.shopping.activity;
//
//import android.Manifest;
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//
//import com.nhzw.base.BaseActivity;
//import com.nhzw.base.BaseAdapter;
//import com.nhzw.base.BaseViewModel;
//import com.nhzw.service.AppService;
//import com.nhzw.shopping.R;
//import com.nhzw.shopping.adapter.UserAdapter;
//import com.nhzw.shopping.viewmodel.DemoViewModel;
//import com.paginate.Paginate;
//import com.tbruyelle.rxpermissions2.RxPermissions;
//import com.xinayida.lib.annotation.AFInject;
//import com.xinayida.lib.utils.AppUtils;
//
//import butterknife.BindView;
//
///**
// * Created by ww on 2017/9/15.
// */
//
//@AFInject(contentViewId = R.layout.demo_layout)
//public class DemoActivity extends BaseActivity<DemoViewModel> implements SwipeRefreshLayout.OnRefreshListener {
//
//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;
//    @BindView(R.id.swipeRefreshLayout)
//    SwipeRefreshLayout swipeRefreshLayout;
//
//    private Paginate paginate;
//    private UserAdapter adapter;
//    private boolean loadingMore;
//
//    @Override
//    protected void setup(Bundle savedInstanceState) {
//        initRecycleView();
//        initPaginate();
//        setupViewModel();
//        onRefresh();
//    }
//
//    private void setupViewModel() {
//        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(granted -> {
//            if (!granted) {
//                AppService.instance.showSnackbar("Please enable external storage permission", false);
//            }
//        });
//        viewModel.getLoadingStatus().observe(this, status -> {
//            switch (status) {
//                case BaseViewModel.LOADINGMORE:
//                    loadingMore = true;
//                    break;
//                case BaseViewModel.LOADMORE_SUCCESS:
//                case BaseViewModel.LOADMORE_FAILED:
//                    loadingMore = false;
//                    break;
//                case BaseViewModel.REFRESHING:
//                    swipeRefreshLayout.setRefreshing(true);
//                    break;
//                case BaseViewModel.REFRESH_SUCCESS:
//                case BaseViewModel.REFRESH_FAILED:
//                    swipeRefreshLayout.setRefreshing(false);
//                    break;
//            }
//        });
//        viewModel.getUserList().observe(this, users -> {
//            if (viewModel.pullToRefresh) {
//                adapter.update(users);
//            } else {
//                adapter.append(users);
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        BaseAdapter.releaseAllHolder(recyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
//        super.onDestroy();
//        paginate = null;
//    }
//
//    @Override
//    public void onRefresh() {
//        viewModel.requestUser(true, 1);
//    }
//
//    private void initRecycleView() {
//        swipeRefreshLayout.setOnRefreshListener(this);
//        adapter = new UserAdapter();
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//    }
//
//    /**
//     * 初始化Paginate,用于加载更多
//     */
//    private void initPaginate() {
//        if (paginate == null) {
//            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
//                @Override
//                public void onLoadMore() {
////                    mPresenter.requestUsers(Message.obtain(UserActivity.this, new Object[]{false, mRxPermissions}));
//                    int lastid = adapter.getItemCount() > 0 ? adapter.getItem(adapter.getItemCount() - 1).getId() : 1;
//                    viewModel.requestUser(false, lastid);
//                }
//
//                @Override
//                public boolean isLoading() {
//                    return loadingMore;
//                }
//
//                @Override
//                public boolean hasLoadedAllItems() {
//                    return false;
//                }
//            };
//
//            paginate = Paginate.with(recyclerView, callbacks)
//                    .setLoadingTriggerThreshold(0)
//                    .build();
//            paginate.setHasMoreDataToLoad(false);
//        }
//    }
//}
