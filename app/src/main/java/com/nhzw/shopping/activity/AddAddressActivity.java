package com.nhzw.shopping.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nhzw.base.BaseActivity;
import com.nhzw.rx.RxObserver;
import com.nhzw.service.AppService;
import com.nhzw.shopping.R;
import com.nhzw.shopping.viewmodel.AddressViewModel;
import com.xinayida.lib.annotation.AFInject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述: 地址编辑页面.<br/><br/>
 * 作者：由姚宇编辑于2017/10/30.<br/><br/>
 */
@AFInject(contentViewId = R.layout.activity_my_address, enableSlidr = true, btnBackId = R.id.toolbar_back)
public class AddAddressActivity extends BaseActivity<AddressViewModel> {

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    /**
     * 收件人。 —— 由姚宇编辑于2017/11/1.<br/><br/>
     */
    @BindView(R.id.et_viewMyAddress_recipient)
    EditText et_viewMyAddress_recipient;
    /**
     * 电话。 —— 由姚宇编辑于2017/11/1.<br/><br/>
     */
    @BindView(R.id.et_viewMyAddress_phone)
    EditText et_viewMyAddress_phone;
    /**
     * 地址。 —— 由姚宇编辑于2017/11/1.<br/><br/>
     */
    @BindView(R.id.et_viewMyAddress_address)
    EditText et_viewMyAddress_address;

    @Override
    protected void setup(Bundle savedInstanceState) {
        toolbar_title.setText("新增地址");
        viewModel.loadContactList();
        viewModel.getContact().observe(this, contact -> {
            if (contact != null) {
                et_viewMyAddress_recipient.setText(contact.getName());
                et_viewMyAddress_phone.setText(contact.getPhone());
                et_viewMyAddress_address.setText(contact.getAddress());
            }
        });

    }

    @OnClick({R.id.btn_viewSetting_save})
    public void onClick(View v) {
        if (AppService.instance.noNetworkToast(getString(R.string.network_error_simple))) return;
        switch (v.getId()) {
            case R.id.btn_viewSetting_save:
                String recipient = et_viewMyAddress_recipient.getText().toString();
                String phone = et_viewMyAddress_phone.getText().toString();
                String address = et_viewMyAddress_address.getText().toString();
                if (viewModel.checkRecipintInfo(recipient, phone, address)) {
                    viewModel.saveAddress(recipient, phone, address).subscribe(new RxObserver() {
                        @Override
                        public void onNext(Object o) {
                            AppService.instance.showToast("保存成功");
                            finish();
                        }

                        @Override
                        public void onError(Throwable t) {
                            AppService.instance.showToast(getString(R.string.network_error_simple));
                        }
                    });
                }
                break;
        }
    }

}
