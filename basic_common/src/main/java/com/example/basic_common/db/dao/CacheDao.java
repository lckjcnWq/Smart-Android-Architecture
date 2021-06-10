package com.example.basic_common.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.basic_common.db.bean.CacheEntity;

/**
 * Description:
 * Created by WuQuan on 2021/3/22.
 */
@Dao
public interface CacheDao {
    @Insert
    void insertCaches(CacheEntity... cacheEntities);

    @Update
    void updateCaches(CacheEntity... cacheEntities);

    @Query("SELECT * FROM cache WHERE `key` = :key LIMIT 0,1")
    CacheEntity findByKey(String key);

    @Delete
    void deleteCaches(CacheEntity... cacheEntities);

    @Query("SELECT * FROM cache")
    CacheEntity[] findAll();
}

