package com.jiecxy.security.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by apple on 15/2/21.
 */

public class AddressDao
{

    public static SQLiteDatabase getAddressDB(String path)
    {
        //打开那个存放电话号码的数据库
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    }

}