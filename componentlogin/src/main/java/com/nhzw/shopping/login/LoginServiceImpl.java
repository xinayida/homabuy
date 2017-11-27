package com.nhzw.shopping.login;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nhzw.service.AppService;
import com.nhzw.service.LoginService;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by ww on 2017/9/12.
 */

public class LoginServiceImpl implements LoginService, View.OnClickListener {
    public static final String WECHAT_APP_ID = "wx29ec0380e5f7c692";

    private IWXAPI iwxapi;
    private SendMessageToWX.Req shareReq;
    private BaseNiceDialog shareDialog;

    public LoginServiceImpl() {
        iwxapi = WXAPIFactory.createWXAPI(AppService.instance.getAppContext(), WECHAT_APP_ID, true);
        iwxapi.registerApp(WECHAT_APP_ID);
    }

    @Override
    public void loginWeichat() {
        if (!iwxapi.isWXAppInstalled()) {
            AppService.instance.showToast("尚未安装微信，请先安装微信", false);
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "session_and_random_integer";
        iwxapi.sendReq(req);
    }

    @Override
    public void showShare(String url, String title, String description, Bitmap bitmap) {
        if (!iwxapi.isWXAppInstalled()) {
            AppService.instance.showToast("尚未安装微信，请先安装微信", false);
            return;
        }

        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = description;
        msg.thumbData = bmpToByteArray(bitmap, true);

        shareReq = new SendMessageToWX.Req();
        shareReq.transaction = buildTransaction("transcation");
        shareReq.message = msg;

        shareDialog = NiceDialog.init().setLayoutId(R.layout.dialog_share).setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder viewHolder, BaseNiceDialog baseNiceDialog) {
                viewHolder.setOnClickListener(R.id.wechat, LoginServiceImpl.this);
                viewHolder.setOnClickListener(R.id.wechat_favorite, LoginServiceImpl.this);
                viewHolder.setOnClickListener(R.id.wechat_moments, LoginServiceImpl.this);
                viewHolder.setOnClickListener(R.id.share_cancel, LoginServiceImpl.this);
            }
        })
                .setWidth(360)
                .setShowBottom(true)
                .setOutCancel(true).show(((AppCompatActivity) AppService.instance.getCurActivity()).getSupportFragmentManager());

    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i != R.id.share_cancel) {
            int scene = 0;
            if (i == R.id.wechat) {
                scene = SendMessageToWX.Req.WXSceneSession;
            } else {
                shareReq.message.title = shareReq.message.description;
                if (i == R.id.wechat_favorite) {
                    scene = SendMessageToWX.Req.WXSceneFavorite;
                } else if (i == R.id.wechat_moments) {
                    scene = SendMessageToWX.Req.WXSceneTimeline;
                }
            }
            shareReq.scene = scene;
            iwxapi.sendReq(shareReq);
        }
        if (shareDialog != null) {
            shareDialog.dismissAllowingStateLoss();
            shareDialog = null;
        }
    }
}
