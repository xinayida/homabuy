package com.nhzw.shopping.model.repository;

import com.nhzw.base.BaseRepository;
import com.nhzw.base.IRepositoryFactory;
import com.nhzw.rx.ResponseVoid;
import com.nhzw.shopping.model.API;
import com.nhzw.shopping.model.ContactListResult;

import io.reactivex.Observable;

/**
 * =================================
 * 描述: 添加地址.<br/><br/>
 * 作者：由姚宇编辑于2017/10/31.<br/><br/>
 * =================================
 */
public class AddressRepository extends BaseRepository {

    public AddressRepository(IRepositoryFactory repositoryFactory) {
        super(repositoryFactory);
    }

    @Override
    public void onDestory() {

    }

    /**
     * 描述: 保存地址.<br/><br/>
     * 作者：由姚宇编辑于2017/11/1.<br/><br/>
     */
    public Observable<ResponseVoid> saveContact(String name, String phone, String address) {
        return factory.createRetrofitService(API.class).saveContact(name, phone, address);
    }

    /**
     * 描述: 获取地址列表.<br/><br/>
     * 作者：由姚宇编辑于2017/11/1.<br/><br/>
     */
    public Observable<ContactListResult> contactList() {
        return factory.createRetrofitService(API.class).contactList();
    }
}
