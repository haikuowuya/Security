package com.security.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.security.dao.AddressDao;

public class NumberAddressService
{
	
	public static String getAddress(String number)
	{
		String pattern = "^1[3458]\\d{9}$";
		String address = "";
		if(number.matches(pattern))//手机号码
		{
			String outkey = query("select outkey from numinfo where mobileprefix = ? ", new String[] {number.substring(0, 7)});
			address = query("select city from address_tb where _id = ? ", new String[] {outkey})
					+ "\n" + query("select cardtype from address_tb where _id = ? ", new String[] {outkey});
			if(address.equals("\n"))
			{
				address = "未知错误！";
			}
		}
		else	//固定电话
		{
			int len = number.length();
			switch(len)
			{
				case 4 : //模拟器
					address = "模拟器";
					break;
					
				case 7 : //本地号码
					address = "本地号码";
					break;
					
				case 8 : //本地号码
					address = "本地号码";
					break;
					
				case 10 : //3位区号，7位号码
					address = query("select city from address_tb where area = ? limit 1", new String[] {number.substring(0, 3)})
							+ "\n" + query("select cardtype from address_tb where area = ? limit 1", new String[] {number.substring(0, 3)});
					if(address.equals("\n"))
					{
						address = "未知错误！";
					}
					break;
					
				case 11 : //3位区号，8位号码  或4位区号，7位号码
					address = query("select city from address_tb where area = ? limit 1", new String[] {number.substring(0, 3)})
						+ "\n" + query("select cardtype from address_tb where area = ? limit 1", new String[] {number.substring(0, 3)});
					if(address.equals("\n"))
					{
						address = query("select city from address_tb where area = ? limit 1", new String[] {number.substring(0, 4)})
								+ "\n" + query("select cardtype from address_tb where area = ? limit 1", new String[] {number.substring(0, 4)});
						if(address.equals("\n"))
						{
							address = "未知错误！";
						}
					}
					break;
					
				case 12 : //4位区号，8位号码
					address = query("select city from address_tb where area = ? limit 1", new String[] {number.substring(0, 4)})
						+ "\n" + query("select cardtype from address_tb where area = ? limit 1", new String[] {number.substring(0, 4)});
					if(address.equals("\n"))
					{
						address = "未知错误！";
					}
					break;
					
				default : 
					address = "未知错误！";
					break;
			}
		}
		return address;
	}
	
	private static String query(String sql, String[] selectionArgs)
	{
		String result = "";
		String path = Environment.getExternalStorageDirectory() + "/security/db/data.db";
		SQLiteDatabase db = AddressDao.getAddressDB(path);
		if(db.isOpen())
		{
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if(cursor.moveToNext())
			{
				result = cursor.getString(0);
			}
			cursor.close();
			db.close();
		}
		return result;
	}

}
