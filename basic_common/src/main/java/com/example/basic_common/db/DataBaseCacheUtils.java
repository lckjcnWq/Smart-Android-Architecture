package com.example.basic_common.db;

import com.blankj.utilcode.util.GsonUtils;
import com.example.basic_common.db.config.DatabaseConfig;
import com.example.basic_common.db.bean.CacheEntity;
import com.example.basic_common.db.dao.CacheDao;

/**
 * Description:
 * Created by luofei on 2021/3/22.
 */
public class DataBaseCacheUtils {

    private static DatabaseConfig mDataBaseConfig;

    private static CacheDao getRepository() {
        if(mDataBaseConfig==null){
            throw new NullPointerException("database must be  init");
        }
        return mDataBaseConfig.mCacheDao();
    }

    public static void init(){
        mDataBaseConfig=DatabaseConfig.getDatabaseConfig();
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        CacheEntity cacheEntity;
        cacheEntity = getRepository().findByKey(key);
        if (cacheEntity == null) {
            cacheEntity = new CacheEntity();
            cacheEntity.setKey(key);
            cacheEntity.setValue(value);
            getRepository().insertCaches(cacheEntity);
        } else {
            cacheEntity.setValue(value);
            getRepository().updateCaches(cacheEntity);
        }
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        CacheEntity cacheEntity = getRepository().findByKey(key);
        if (cacheEntity == null) {
            cacheEntity = new CacheEntity();
            cacheEntity.setKey(key);
            String jsonValue = GsonUtils.toJson(value);
            cacheEntity.setValue(jsonValue);
            getRepository().insertCaches(cacheEntity);
        } else {
            String jsonValue = GsonUtils.toJson(value);
            cacheEntity.setValue(jsonValue);
            getRepository().updateCaches(cacheEntity);
        }
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        CacheEntity cacheEntity = getRepository().findByKey(key);
        if (cacheEntity == null) {
            return null;
        }
        return cacheEntity.getValue();
    }

    /**
     * 获取缓存对象
     *
     * @param key
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T get(String key, Class<T> classOfT) {
        CacheEntity cacheEntity = getRepository().findByKey(key);
        if (cacheEntity == null) {
            return null;
        }
        String jsonValue = cacheEntity.getValue();
        return GsonUtils.fromJson(jsonValue, classOfT);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void delete(String key) {
        CacheEntity cacheEntity = getRepository().findByKey(key);
        if (cacheEntity != null) {
            getRepository().deleteCaches(cacheEntity);
        }
    }

    /**
     * 删除全部缓存
     */
    public static void clearAll() {
        CacheEntity[] cacheEntities = getRepository().findAll();
        if (cacheEntities != null && cacheEntities.length != 0) {
            getRepository().deleteCaches(cacheEntities);
        }
    }
}
