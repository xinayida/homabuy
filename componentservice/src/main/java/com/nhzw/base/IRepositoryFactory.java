package com.nhzw.base;

/**
 * Created by ww on 2017/9/15.
 */

public interface IRepositoryFactory {
    /**
     * 根据传入的 Class 创建对应的仓库
     *
     * @param repository
     * @param <T>
     * @return
     */
    <T extends IModel> T createRepository(Class<T> repository);

    /**
     * 根据传入的 Class 创建对应的 Retrofit service
     *
     * @param service
     * @param <T>
     * @return
     */
    <T> T createRetrofitService(Class<T> service);

    <T> T createRetrofitService(Class<T> service, String domain);

    /**
     * 根据传入的 Class 创建对应的 Cache service
     *
     * @param service
     * @param <T>
     * @return
     */
    <T> T createCacheService(Class<T> service);
}
