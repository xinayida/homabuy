package com.nhzw.shopping.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.nhzw.base.BaseActivity;
import com.nhzw.shopping.R;
import com.nhzw.shopping.viewmodel.EditPasswordViewModel;
import com.xinayida.lib.annotation.AFInject;

import butterknife.BindView;


/**
 * 描述: 密码编辑页面.<br/><br/>
 * 作者：由姚宇编辑于2017/10/30.<br/><br/>
 */
@AFInject(contentViewId = R.layout.activity_password, enableSlidr = true, btnBackId = R.id.toolbar_back)
public class EditPasswordActivity extends BaseActivity<EditPasswordViewModel> {

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @Override
    protected void setup(Bundle savedInstanceState) {
        toolbar_title.setText("设置密码");
    }
}
