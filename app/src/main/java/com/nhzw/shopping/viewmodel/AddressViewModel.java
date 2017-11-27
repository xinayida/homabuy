package com.nhzw.shopping.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.nhzw.base.BaseViewModel;
import com.nhzw.rx.RxObserver;
import com.nhzw.rx.RxTransformer;
import com.nhzw.service.AppService;
import com.nhzw.shopping.model.entity.Contact;
import com.nhzw.shopping.model.repository.AddressRepository;
import com.xinayida.lib.utils.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;

/**
 * =================================
 * 描述: 添加地址.<br/><br/>
 * 作者：由姚宇编辑于2017/10/31.<br/><br/>
 * =================================
 */
public class AddressViewModel extends BaseViewModel<AddressRepository> {

    private MutableLiveData<ArrayList<Contact>> contactLists = new MutableLiveData<>();
    private MutableLiveData<Contact> contact = new MutableLiveData<>();

    @Override
    public void onStart() {
    }

    /**
     * 描述: 保存联系人信息.<br/><br/>
     * 作者：由姚宇编辑于2017/11/2.<br/><br/>
     */
    public Observable saveAddress(String name, String phone, String address) {
        return repository.saveContact(name, phone, address)
                .doOnSubscribe(this::addDispose)
                .compose(RxTransformer.trans());
    }

    /**
     * 描述: 检测联系人信息是否为空.<br/><br/>
     * 作者：由姚宇编辑于2017/11/2.<br/><br/>
     */
    public boolean checkRecipintInfo(String recipient, String phone, String address) {
        if (StringUtils.isEmpty(recipient)) {
            AppService.instance.showToast("请输入收件人名称");
            return false;
        }
        if (StringUtils.isEmpty(phone)) {
            AppService.instance.showToast("请输入收件人电话");
            return false;
        } else if (!isMobile(phone)) {
            AppService.instance.showToast("请输入正确的手机号码");
            return false;
        }
        if (StringUtils.isEmpty(address)) {
            AppService.instance.showToast("请输入收件地址");
            return false;
        }
        return true;
    }

    /**
     * 描述: 网络加载联系人列表.<br/><br/>
     * 作者：由姚宇编辑于2017/11/2.<br/><br/>
     */
    public void loadContactList() {
        repository.contactList()
                .doOnSubscribe(this::addDispose)
                .compose(RxTransformer.trans())
                .subscribe(new RxObserver<ArrayList<Contact>>() {
                    @Override
                    public void onNext(ArrayList<Contact> contacts) {
                        contactLists.postValue(contacts);
                        if (contacts.size() > 0) {
                            contact.setValue(contacts.get(0));
                        }
                    }
                });
    }

    public MutableLiveData<ArrayList<Contact>> getContactLists() {
        return contactLists;
    }

    /**
     * 描述: 获取单个联系人.<br/><br/>
     * 作者：由姚宇编辑于2017/11/2.<br/><br/>
     */
    public MutableLiveData<Contact> getContact() {
        return contact;
    }

    /**
     * 手机号验证
     * @param str
     *
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
}
