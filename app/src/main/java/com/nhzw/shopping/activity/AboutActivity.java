package com.nhzw.shopping.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nhzw.base.BaseActivity;
import com.nhzw.component.lib.route.Router;
import com.nhzw.rx.RxUtils;
import com.nhzw.service.AppService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.viewmodel.AboutViewModel;
import com.nhzw.utils.UMUtil;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.xinayida.lib.annotation.AFInject;
import com.xinayida.lib.utils.AppUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ww on 2017/10/23.
 */

@AFInject(contentViewId = R.layout.activity_about, enableSlidr = true, btnBackId = R.id.toolbar_back)
public class AboutActivity extends BaseActivity<AboutViewModel> {

    @BindView(R.id.about_version)
    TextView version;

    @Override
    protected void setup(Bundle savedInstanceState) {
        version.setText("V" + AppUtils.getVersionName(this));
        viewModel.updateStatus.observe(this, updateInfo -> {
            Log.d("Stefan", updateInfo.toString());
            if (updateInfo.update) {
                NiceDialog.init().setLayoutId(R.layout.dialog_update).setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.setText(R.id.content, updateInfo.description);
                        holder.setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.btn_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String updateUrl = AppService.DOWNLOAD_LINK;
                                if (!TextUtils.isEmpty(updateInfo.url) && !"null".equalsIgnoreCase(updateInfo.url)) {
                                    updateUrl = updateInfo.url;
                                }
                                RxUtils.download(context, updateUrl);
                                dialog.dismiss();
                            }
                        });
                    }
                }).setOutCancel(false)
                        .setWidth(270)
                        .show(getSupportFragmentManager());
            } else {
                AppService.instance.showSnackbar(updateInfo.description);
            }
        });
    }

    @OnClick({R.id.about_protocal, R.id.about_check})
    public void onClick(View view) {
        if (AppService.instance.noNetworkToast()) return;
        switch (view.getId()) {
            case R.id.about_protocal:
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.homabuy.com/agreement/userAgreement.html");
                Router.getInstance().open(this, XWebViewActivity.class, bundle);
//                协议
                break;
            case R.id.about_check:
//                AppService.instance.showSnackbar("没有新版本");
                UMUtil.submitCustomEvent("personal_center_click_Update_user");
                viewModel.checkUpdate();
//                检查更新
                break;
        }
    }
}
