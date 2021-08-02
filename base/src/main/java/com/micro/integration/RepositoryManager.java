
package com.micro.integration;

import android.app.Application;
import android.content.Context;

import com.micro.integration.cache.Cache;
import com.micro.integration.cache.CacheType;
import com.micro.mvp.IModel;
import com.micro.utils.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import retrofit2.Retrofit;

/**
 * ================================================
 * 用来管理网络请求层,以及数据缓存层,以后可能添加数据库请求层
 * 提供给 {@link IModel} 层必要的 Api 做数据处理
 * ================================================
 */
@Singleton
public class RepositoryManager implements IRepositoryManager {
    @Inject
    Lazy<Retrofit> mRetrofit;
    @Inject
    Application mApplication;
    @Inject
    Cache.Factory mCachefactory;

    private Cache<String, Object> mRetrofitServiceCache;

    @Inject
    public RepositoryManager() {
    }

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param service
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T> T obtainRetrofitService(Class<T> service) {
        if (mRetrofitServiceCache == null)
            mRetrofitServiceCache = mCachefactory.build(CacheType.RETROFIT_SERVICE_CACHE);
        Preconditions.checkNotNull(mRetrofitServiceCache, "Cannot return null from a Cache.Factory#build(int) method");
        T retrofitService = (T) mRetrofitServiceCache.get(service.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = mRetrofit.get().create(service);
            mRetrofitServiceCache.put(service.getCanonicalName(), retrofitService);
        }
        return retrofitService;
    }

    @Override
    public Context getContext() {
        return mApplication;
    }
}
