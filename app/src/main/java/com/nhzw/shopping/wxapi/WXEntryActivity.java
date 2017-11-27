package com.nhzw.shopping.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nhzw.action.ActionType;
import com.nhzw.base.BaseActivity;
import com.nhzw.service.AppService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.model.API;
import com.nhzw.service.AccountInfo;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xinayida.lib.annotation.AFInject;
import com.xinayida.lib.rxflux.Action;
import com.xinayida.lib.rxflux.Dispatcher;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by ww on 2017/10/11.
 */
@AFInject(contentViewId = R.layout.wx_layout)
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    public static final String WECHAT_APP_ID = "wx29ec0380e5f7c692";
    private IWXAPI iwxapi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(AppService.instance.getAppContext(), WECHAT_APP_ID, true);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void setup(Bundle savedInstanceState) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        iwxapi.handleIntent(intent, this);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == 2) {//分享
            finish();
            return;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp instanceof SendAuth.Resp) {
                    String code = ((SendAuth.Resp) resp).code;
                    AppService.instance.asyncHttp(AppService.DOMAIN_LOGIN + "login", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            AppService.instance.showToast("登录失败:" + e, false);
                            finish();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body = response.body().string();
                            try {
                                JSONObject json = new JSONObject(body);
                                String code = json.getString("code");
                                if (API.OK.equals(code)) {
                                    JSONObject data = json.getJSONObject("data");
                                    if (data != null) {
                                        String token = data.getString("token");
                                        AccountInfo account = new AccountInfo();
                                        account.headImg = data.getString("headImg");
                                        account.nickname = data.getString("nickname");
                                        account.uid = data.getInt("uid");
                                        account.token = token;
                                        Dispatcher.get().postAction(Action.type(ActionType.LOGIN).bundle("account", account).build());
                                    }
                                }
                            } catch (Exception e) {
                                AppService.instance.showToast("登录失败:" + e, false);
                            }
                            finish();
                        }
                    }, new FormBody
                            .Builder()
                            .add("openType", "WECHAT")
                            .add("code", code)
                            .build());
                }
                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                AppService.instance.showToast("登录失败：认证被取消", false);
//                finish();
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                AppService.instance.showToast("登录失败：认证失败-未授权", false);
//                finish();
//                break;
            default:
                AppService.instance.showToast("登录失败：认证失败-未授权", false);
                finish();
                break;
        }
    }
}
