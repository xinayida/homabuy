package com.nhzw.shopping.model.repository;

import com.nhzw.base.BaseRepository;
import com.nhzw.base.IRepositoryFactory;

/**
 * =================================
 * 描述: 修改密码.<br/><br/>
 * 作者：由姚宇编辑于2017/10/31.<br/><br/>
 * =================================
 */
public class EditPasswordRepository extends BaseRepository {

    public EditPasswordRepository(IRepositoryFactory repositoryFactory) {
        super(repositoryFactory);
    }

    @Override
    public void onDestory() {

    }
}
