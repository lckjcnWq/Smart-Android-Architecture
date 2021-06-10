package com.example.basic_common.db.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Description:
 * Created by WuQuan on 2021/3/22.
 */
@Entity(tableName = "cache")
public class CacheEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String key;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
