package com.example.basic_common.db.config;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.blankj.utilcode.util.Utils;
import com.example.basic_common.db.bean.CacheEntity;
import com.example.basic_common.db.dao.CacheDao;

/**
 * Description:
 * Created by WuQuan on 2021/3/22.
 */
@Database(entities = {CacheEntity.class},version = 1,exportSchema = false)
public abstract class DatabaseConfig extends RoomDatabase {
    public abstract CacheDao mCacheDao();

    private static DatabaseConfig databaseConfig;
    public static DatabaseConfig getDatabaseConfig() {
        if (databaseConfig == null) {
            databaseConfig= Room.databaseBuilder(Utils.getApp(),DatabaseConfig.class,"db_native")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }
        return databaseConfig;
    }
          //表升级操作
//          static final Migration MIGRATION_1_2 = new Migration(1, 2)
//          {
//              @Override
//              public void migrate(@NonNull SupportSQLiteDatabase database)
//              {
//                  //do something
//                  Log.d("MyDatabase", "MIGRATION_1_2");
//              }
//          };
//        static final Migration MIGRATION_3_4 = new Migration(3, 4)
//        {
//            @Override
//            public void migrate(SupportSQLiteDatabase database)
//            {
//                Log.d("MyDatabase", "MIGRATION_3_4");
//                database.execSQL("CREATE TABLE temp_Student (" +
//                        "id INTEGER PRIMARY KEY NOT NULL," +
//                        "name TEXT," +
//                        "age TEXT)");
//                database.execSQL("INSERT INTO temp_Student (id, name, age) " +
//                        "SELECT id, name, age FROM Student");
//                database.execSQL("DROP TABLE Student");
//                database.execSQL("ALTER TABLE temp_Student RENAME TO Student");
//            }
//        };
}
