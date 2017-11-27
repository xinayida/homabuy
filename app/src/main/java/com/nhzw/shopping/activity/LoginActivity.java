package com.nhzw.shopping.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nhzw.base.BaseActivity;
import com.nhzw.component.lib.service.ServiceManager;
import com.nhzw.service.AppService;
import com.nhzw.service.LoginService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.viewmodel.MainViewModel;
import com.nhzw.utils.UMUtil;
import com.xinayida.lib.utils.SpUtil;

import java.util.Arrays;

import ezy.ui.view.BannerView;

/**
 * Created by ww on 2017/11/2.
 */

public class LoginActivity extends BaseActivity<MainViewModel> {

    private boolean isFirstLaunch;

    BannerView<Integer> guildBanner;

    private final static Integer[] guildRes = {R.drawable.guild1, R.drawable.guild2, 0};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isFirstLaunch = SpUtil.getInstance().readBoolean("first_launch", true);
        if (isFirstLaunch) {
            setContentView(R.layout.activity_guild);
            guildBanner = findViewById(R.id.guild_banner);
        } else {
            setContentView(R.layout.activity_login);
            findViewById(R.id.login_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UMUtil.submitCustomEvent("login_wechat");
                    login();
                }
            });
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        UMUtil.submitCustomEvent("login_page_show", "展示时间戳", String.valueOf(System.currentTimeMillis()));
    }

    @Override
    protected void setup(Bundle savedInstanceState) {
        if (isFirstLaunch) {
            guildBanner.setViewFactory(new BannerView.ViewFactory<Integer>() {
                @Override
                public View create(Integer integer, int position, ViewGroup container) {
                    View v = null;
                    if (position <= 1) {
                        v = new ImageView(context);
                        ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ((ImageView) v).setImageResource(integer);
                    } else {
                        v = LayoutInflater.from(context).inflate(R.layout.activity_login, null);
                        v.findViewById(R.id.login_layout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SpUtil.getInstance().writeBoolean("first_launch", false);
                                UMUtil.submitCustomEvent("login_wechat");
                                login();
                            }
                        });
                    }
                    return v;
                }
            });
            guildBanner.setDataList(Arrays.asList(guildRes));
            guildBanner.start();
        }

    }

    private void login() {
        if (!AppService.instance.isLogined()) {
            ServiceManager.getInstance().getService(LoginService.class).loginWeichat();
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {//屏蔽菜单按钮
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppService.instance.finishAllActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
