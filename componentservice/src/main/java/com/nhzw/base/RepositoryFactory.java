package com.nhzw.base;

import com.google.gson.Gson;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rx_cache2.internal.RxCache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ww on 2017/9/14.
 */
@Singleton
public class RepositoryFactory implements IRepositoryFactory {

    @Inject
    Retrofit retrofitNormal;

    @Inject
    RxCache rxCache;

    @Inject
    Gson gson;

    @Inject
    OkHttpClient okHttpClient;

//    @Named("cache")
//    @Inject
//    Retrofit retrofitCache;

    private final Map<String, IModel> repositoryCache = new HashMap<>();
    private final Map<String, Object> retrofitServiceCache = new HashMap<>();
    private final Map<String, Object> cacheServiceCache = new HashMap<>();

    @Inject
    public RepositoryFactory() {
    }

    /**
     * 根据传入的 Class 创建对应的仓库
     *
     * @param repository
     * @param <T>
     * @return
     */
    @Override
    public <T extends IModel> T createRepository(Class<T> repository) {
        T repositoryInstance;
        synchronized (repositoryCache) {
            repositoryInstance = (T) repositoryCache.get(repository.getName());
            if (repositoryInstance == null) {
                Constructor<? extends IModel> constructor = findConstructorForClass(repository);
                try {
                    repositoryInstance = (T) constructor.newInstance(this);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Unable to invoke " + constructor, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to invoke " + constructor, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("create repository error", e);
                }
                repositoryCache.put(repository.getName(), repositoryInstance);
            }
        }
        return repositoryInstance;
    }

    /**
     * 根据传入的 Class 创建对应的 Retrofit service
     *
     * @param service
     * @param <T>
     * @return
     */
    @Override
    public <T> T createRetrofitService(Class<T> service) {
        T retrofitService;
        synchronized (retrofitServiceCache) {
            retrofitService = (T) retrofitServiceCache.get(service.getName());
            if (retrofitService == null) {
                retrofitService = retrofitNormal.create(service);
                retrofitServiceCache.put(service.getName(), retrofitService);
            }
        }
        return retrofitService;
    }

    @Override
    public <T> T createRetrofitService(Class<T> service, String domain) {
        T retrofitService;
        synchronized (retrofitServiceCache) {
            retrofitService = (T) retrofitServiceCache.get(domain);
            if (retrofitService == null) {
                Retrofit retrofit = buildRetrofit(domain);
                retrofitService = retrofit.create(service);
                retrofitServiceCache.put(domain, retrofitService);
            }
        }
        return retrofitService;
    }

    @Override
    public <T> T createCacheService(Class<T> service) {
        T cacheService;
        synchronized (cacheServiceCache) {
            cacheService = (T) cacheServiceCache.get(service.getName());
            if (cacheService == null) {
                cacheService = rxCache.using(service);
                cacheServiceCache.put(service.getName(), cacheService);
            }
        }
        return cacheService;
    }

    private static Constructor<? extends IModel> findConstructorForClass(Class<?> cls) {
        Constructor<? extends IModel> bindingCtor;

        String clsName = cls.getName();

        try {
            //noinspection unchecked
            bindingCtor = (Constructor<? extends IModel>) cls.getConstructor(IRepositoryFactory.class);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find constructor for " + clsName, e);
        }

        return bindingCtor;
    }

    Retrofit buildRetrofit(String Domain) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Domain)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}
