package com.security.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.security.utils.DBHelper;

public class BlackNumberDao
{
	private DBHelper dbHelper;
	
	public BlackNumberDao(Context context)
	{
		dbHelper = new DBHelper(context);
	}
	
	public boolean find(String number)
	{
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if(db.isOpen())
		{
			Cursor cursor = db.rawQuery("select number from blacknumber where number = ? ", new String[] {number});
			if(cursor.moveToNext())
			{
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}
	
	public void add(String number)
	{
		find(number);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen())
		{
			db.execSQL("insert into blacknumber (number) values(?)", new Object[] {number});
			db.close();
		}
	}
	
	public void delete(String number)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen())
		{
			db.execSQL("delete from blacknumber where number = ? ", new Object[] {number});
			db.close();
		}
	}
	
	public void update(String oldNumber, String newNumber)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen())
		{
			db.execSQL("update blacknumber set number = ? where number = ? ", new Object[] {newNumber, oldNumber});
			db.close();
		}
	}
	
	public List<String> findAll()
	{
		List<String> numbers = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if(db.isOpen())
		{
			Cursor cursor = db.rawQuery("select number from blacknumber", null);
			while(cursor.moveToNext())
			{
				numbers.add(cursor.getString(0));
			}
			cursor.close();
			db.close();
		}
		return numbers;
	}

}
