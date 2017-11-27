package com.nhzw.shopping.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nhzw.base.BaseActivity;
import com.nhzw.component.lib.route.Router;
import com.nhzw.service.AppService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.viewmodel.SettingViewModel;
import com.nhzw.utils.ImageLoader;
import com.xinayida.lib.annotation.AFInject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ww on 2017/10/23.
 */
@AFInject(contentViewId = R.layout.activity_setting, enableSlidr = true, btnBackId = R.id.toolbar_back)
public class SettingActivity extends BaseActivity<SettingViewModel> {

    @BindView(R.id.btn_viewSetting_quitLogin)
    Button btn_viewSetting_quitLogin;

    @BindView(R.id.tv_cache)
    TextView cache;

    @Override
    protected void setup(Bundle savedInstanceState) {
//        if (AppService.instance.isLogined()) {
//            btn_viewSetting_quitLogin.setVisibility(View.VISIBLE);
//        } else {
//            btn_viewSetting_quitLogin.setVisibility(View.GONE);
//        }
        cache.setText(ImageLoader.getCacheSize());
        viewModel.getLogin().observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                btn_viewSetting_quitLogin.setVisibility(View.VISIBLE);
            } else {
                btn_viewSetting_quitLogin.setVisibility(View.GONE);
            }
        });
        viewModel.cacheSize.observe(this, size -> {
            cache.setText(size);
        });
    }

    @OnClick({R.id.ll_viewSetting_myAddress, R.id.ll_viewSetting_password, R.id.btn_viewSetting_quitLogin, R.id.ll_viewSetting_clearCache})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_viewSetting_myAddress:// 跳转地址页。 —— 由姚宇编辑于2017/10/30.
                Router.getInstance().open(this, AddAddressActivity.class);
                break;
            case R.id.ll_viewSetting_password:// 跳转密码页。 —— 由姚宇编辑于2017/10/30.
                Router.getInstance().open(this, EditPasswordActivity.class);
                break;
            case R.id.btn_viewSetting_quitLogin:// 跳转密码页。 —— 由姚宇编辑于2017/10/30.
                viewModel.logout();
                AppService.instance.logout(null);
                finish();
                Router.getInstance().open(this, LoginActivity.class);
                break;
            case R.id.ll_viewSetting_clearCache:
                viewModel.clearCache();
                AppService.instance.showSnackbar(getString(R.string.clear_cache_success), false);
                break;
        }
    }
}
