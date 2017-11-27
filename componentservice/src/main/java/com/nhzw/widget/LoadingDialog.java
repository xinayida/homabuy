package com.nhzw.widget;

import com.nhzw.common.service.R;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.ViewHolder;
//NiceDialog.init()
//        .setLayoutId(R.layout.dialog)     //设置dialog布局文件
//        .setConvertListener(new ViewConvertListener() {     //进行相关View操作的回调
//@Override
//public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
//
//        }
//        })
//        .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
//        .setShowBottom(true)     //是否在底部显示dialog，默认flase
//        .setMargin()     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
//        .setWidth()     //dialog宽度（单位：dp），默认为屏幕宽度，-1代表WRAP_CONTENT
//        .setHeight()     //dialog高度（单位：dp），默认为WRAP_CONTENT
//        .setOutCancel(false)     //点击dialog外是否可取消，默认true
//        .setAnimStyle(R.style.EnterExitAnimation)     //设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
//        .show(getSupportFragmentManager());     //显示dialog

/**
 * 加载Dialog
 * Created by ww on 2017/10/19.
 */

public class LoadingDialog extends BaseNiceDialog {

    public LoadingDialog() {
        setWidth(100).setHeight(100).setDimAmount(0);
    }

    public static LoadingDialog build() {
        return new LoadingDialog();
    }

    @Override
    public int intLayoutId() {
        return R.layout.loading_layout;
    }

    @Override
    public void convertView(ViewHolder viewHolder, BaseNiceDialog baseNiceDialog) {

    }
}
