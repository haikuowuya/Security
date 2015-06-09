package com.security.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao
{

	//拿到所有的分组信息
	public static List<String> getAllGroup(String path)
	{
		List<String> group = new ArrayList<String>();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("select name from classlist", null);
			while (cursor.moveToNext())
			{
				group.add(cursor.getString(0));
			}
			cursor.close();
		}
		db.close();
		return group;
	}

	//拿到所有的电话信息
	public static List<List<String>> getAllChildren(String path,
			int groupCount)
	{
		StringBuffer sb = new StringBuffer();
		List<List<String>> allChild = new ArrayList<List<String>>();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (db.isOpen())
		{
			for (int i = 1; i <= groupCount; i++)
			{
				List<String> child = new ArrayList<String>();
				// 应为我们的数据库是每一个group里面的条目，都是对应一张表的，
				// 所以我们就可以这样来装拼sql语句啦
				String sql = "select name, number from table" + i;
				Cursor cursor = db.rawQuery(sql, null);
				while (cursor.moveToNext())
				{
					// 把信息拼成name-number的形式，到时再拿出来
					sb.append(cursor.getString(0));
					sb.append("-");
					sb.append(cursor.getString(1));
					child.add(sb.toString());
					// 清空stringbuffer里面的内容
					sb.setLength(0);
				}
				cursor.close();
				allChild.add(child);
			}
		}
		db.close();
		return allChild;
	}

}
