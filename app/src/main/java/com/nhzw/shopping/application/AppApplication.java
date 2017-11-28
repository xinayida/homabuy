package com.nhzw.shopping.application;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.kepler.jd.Listener.AsyncInitListener;
import com.kepler.jd.login.KeplerApiManager;
import com.nhzw.base.RepositoryFactory;
import com.nhzw.component.AppComponent;
import com.nhzw.component.DaggerAppComponent;
import com.nhzw.component.lib.route.Router;
import com.nhzw.module.AppModule;
import com.nhzw.module.ClientModule;
import com.nhzw.service.AppService;
import com.nhzw.shopping.BuildConfig;
import com.nhzw.shopping.activity.LoginActivity;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;
import com.tinkerpatch.sdk.server.callback.RollbackCallBack;
import com.tinkerpatch.sdk.server.callback.TinkerPatchRequestCallback;
import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;
import com.tinkerpatch.sdk.tinker.service.TinkerServerResultService;
import com.umeng.commonsdk.UMConfigure;
import com.xinayida.lib.utils.SpUtil;
import com.zhy.autolayout.utils.L;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by ww on 2017/9/12.
 */

public class AppApplication extends MultiDexApplication {

    private static final String TAG = "AppApplication";

    private ApplicationLike tinkerApplicationLike;
    private AppComponent appComponent;

    @Inject
    public RepositoryFactory repositoryFactory;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //如果isRegisterCompoAuto为false，则需要通过反射加载组件
//        Server.registerComponent("com.nhzw.shopping.comname.applike.xxxAppLike");
        initTinkerPatch();
        initInjector();
        initKepler();
        SpUtil.getInstance().init(this);
        CrashReport.initCrashReport(getApplicationContext(), "89f6d6fd76", false);
        FeedbackAPI.init(this, "24664084", "c9ff2c14f1184235b1f9b55ec5419985");
        initUM();
//        ServiceManager.getInstance().registerComponent("com.nhzw.shopping.login.applike.LoginAppLike");

//        if (BuildConfig.DEBUG) {
//            AppService.DEBUG = true;
//            AppService.DOMAIN_API = "http://192.168.16.2:7777/api/";
//            AppService.DOMAIN_LOGIN = "http://192.168.16.2:8888/";
//            AppService.DOMAIN_UPDATE = "http://192.168.16.2:7777/app/";
//        } else {
            AppService.DOMAIN_API = "https://api.homabuy.com/api/";
            AppService.DOMAIN_UPDATE = "https://api.homabuy.com/app/";
            AppService.DOMAIN_LOGIN = " https://user.homabuy.com/";
//        }
        Router.getInstance().registerRouter("login", LoginActivity.class);
        L.debug = false;
    }

    /**
     * 描述: 初始化友盟common库.<br/><br/>
     * 作者：由姚宇编辑于2017/11/7.<br/><br/>
     * <p>
     * 参数1:上下文，不能为空<br/><br/>
     * 参数2:友盟 app key，非必须参数，如果Manifest文件中已配置app key，该参数可以传空，则使用Manifest中配置的app key，否则该参数必须传入<br/><br/>
     * 参数3:友盟 channel，非必须参数，如果Manifest文件中已配置channel，该参数可以传空，则使用Manifest中配置的channel，否则该参数必须传入<br/><br/>
     * 参数4:设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机<br/><br/>
     * 参数5:Push推送业务的secret，需要集成Push功能时必须传入Push的secret，否则传空
     */
    private void initUM() {
        UMConfigure.init(this, "5a00155ff29d983966000055", "hippo", UMConfigure.DEVICE_TYPE_PHONE, null);
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
    }

    private void initInjector() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).clientModule(new ClientModule()).build();
        AppService.instance.setAppComponent(appComponent);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                AppService.instance.pushOneActivity(activity);
                if (AppService.DEBUG)
                    Log.d("Stefan", "onActivityCreated " + activity.getClass().getName());
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                AppService.instance.setCurActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppService.instance.popOneActivity(activity);
                if (AppService.DEBUG)
                    Log.d("Stefan", "onActivityDestroyed " + activity.getClass().getName());
            }
        });
    }

    private void initKepler() {
        KeplerApiManager.asyncInitSdk(this, "a04c20a762a74064aa23b8d93e8d8eab", "206a1a168bc34557ac168eba840ecca0",
                new AsyncInitListener() {
                    @Override
                    public void onSuccess() {

                        Log.e("Kepler", "Kepler asyncInitSdk onSuccess ");
                    }

                    @Override
                    public void onFailure() {

                        Log.e("Kepler",
                                "Kepler asyncInitSdk 授权失败，请检查lib 工程资源引用；包名,签名证书是否和注册一致");

                    }
                });
    }


    /**
     * 我们需要确保至少对主进程跟patch进程初始化 TinkerPatch
     */
    private void initTinkerPatch() {
        // 我们可以从这里获得Tinker加载过程的信息
        if (BuildConfig.TINKER_ENABLE) {
            tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
            // 初始化TinkerPatch SDK
            TinkerPatch.init(
                    tinkerApplicationLike
//                new TinkerPatch.Builder(tinkerApplicationLike)
//                    .requestLoader(new OkHttp3Loader())
//                    .build()
            )
                    .reflectPatchLibrary()
//                    .fetchPatchUpdate(true)//for debug
                    .setPatchRollbackOnScreenOff(true)
                    .setPatchRestartOnSrceenOff(true)
                    .setFetchPatchIntervalByHours(3)
            ;
            // 获取当前的补丁版本
            Log.d(TAG, "Current patch version is " + TinkerPatch.with().getPatchVersion());

            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
            // 不同的是，会通过handler的方式去轮询
            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }
    }

    /**
     * 在这里给出TinkerPatch的所有接口解释
     * 更详细的解释请参考:http://tinkerpatch.com/Docs/api
     */
    private void useSample() {
        TinkerPatch.init(tinkerApplicationLike)
                //是否自动反射Library路径,无须手动加载补丁中的So文件
                //注意,调用在反射接口之后才能生效,你也可以使用Tinker的方式加载Library
                .reflectPatchLibrary()
                //向后台获取是否有补丁包更新,默认的访问间隔为3个小时
                //若参数为true,即每次调用都会真正的访问后台配置
                .fetchPatchUpdate(false)
                //设置访问后台补丁包更新配置的时间间隔,默认为3个小时
                .setFetchPatchIntervalByHours(3)
                //向后台获得动态配置,默认的访问间隔为3个小时
                //若参数为true,即每次调用都会真正的访问后台配置
                .fetchDynamicConfig(new ConfigRequestCallback() {
                    @Override
                    public void onSuccess(HashMap<String, String> hashMap) {

                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                }, false)
                //设置访问后台动态配置的时间间隔,默认为3个小时
                .setFetchDynamicConfigIntervalByHours(3)
                //设置当前渠道号,对于某些渠道我们可能会想屏蔽补丁功能
                //设置渠道后,我们就可以使用后台的条件控制渠道更新
                .setAppChannel("default")
                //屏蔽部分渠道的补丁功能
                .addIgnoreAppChannel("googleplay")
                //设置tinkerpatch平台的条件下发参数
                .setPatchCondition("test", "1")
                //设置补丁合成成功后,锁屏重启程序
                //默认是等应用自然重启
                .setPatchRestartOnSrceenOff(true)
                //我们可以通过ResultCallBack设置对合成后的回调
                //例如弹框什么
                //注意，setPatchResultCallback 的回调是运行在 intentService 的线程中
                .setPatchResultCallback(new ResultCallBack() {
                    @Override
                    public void onPatchResult(PatchResult patchResult) {
                        Log.i(TAG, "onPatchResult callback here");
                    }
                })
                //设置收到后台回退要求时,锁屏清除补丁
                //默认是等主进程重启时自动清除
                .setPatchRollbackOnScreenOff(true)
                //我们可以通过RollbackCallBack设置对回退时的回调
                .setPatchRollBackCallback(new RollbackCallBack() {
                    @Override
                    public void onPatchRollback() {
                        Log.i(TAG, "onPatchRollback callback here");
                    }
                });
    }

    /**
     * 自定义Tinker类的高级用法, 使用更灵活，但是需要对tinker有更进一步的了解
     * 更详细的解释请参考:http://tinkerpatch.com/Docs/api
     */
    private void complexSample() {
        //修改tinker的构造函数,自定义类
        TinkerPatch.Builder builder = new TinkerPatch.Builder(tinkerApplicationLike)
                .listener(new DefaultPatchListener(this))
                .loadReporter(new DefaultLoadReporter(this))
                .patchReporter(new DefaultPatchReporter(this))
                .resultServiceClass(TinkerServerResultService.class)
                .upgradePatch(new UpgradePatch())
                .patchRequestCallback(new TinkerPatchRequestCallback());
        //.requestLoader(new OkHttpLoader());

        TinkerPatch.init(builder.build());
    }
}
