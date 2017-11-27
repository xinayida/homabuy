package com.nhzw.base;

/**
 * Repository基类
 * Created by ww on 2017/9/18.
 */

public abstract class BaseRepository implements IModel{
    //用来构造各Repository的工厂接口
    protected IRepositoryFactory factory;

    public BaseRepository(IRepositoryFactory repositoryFactory){
        this.factory = repositoryFactory;
    }
}
