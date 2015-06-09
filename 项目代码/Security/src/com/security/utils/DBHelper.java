package com.security.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{

	public DBHelper(Context context)
	{
		super(context, "security.db", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20))");
		db.execSQL("create table applock (_id integer primary key autoincrement, packagename varchar(30))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
//		db.execSQL("create table applock (_id integer primary key autoincrement, packagename varchar(30))");
	}

}
