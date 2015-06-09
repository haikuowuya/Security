package com.jiecxy.security.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.jiecxy.security.dao.AddressDao;

/**
 * Created by apple on 15/2/21.
 */

public class NumberAddressService
{

    public static String getAddress(String number)
    {
        String pattern = "^1[3458]\\d{9}$";
        String address = number;
        if(number.matches(pattern))//手机号码
        {
            address = query("select city from info where mobileprefix = ? ", new String[] {number.substring(0, 7)});
            if(address.equals(""))
            {
                address = number;
            }
        }
        else        //固定电话
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
                    address = query("select city from info where area = ? limit 1", new String[] {number.substring(0, 3)});
                    if(address.equals(""))
                    {
                        address = number;
                    }
                    break;

                case 11 : //3位区号，8位号码  或4位区号，7位号码
                    address = query("select city from info where area = ? limit 1", new String[] {number.substring(0, 3)});
                    if(address.equals(""))
                    {
                        address = query("select city from info where area = ? limit 1", new String[] {number.substring(0, 4)});
                        if(address.equals(""))
                        {
                            address = number;
                        }
                    }
                    break;

                case 12 : //4位区号，8位号码
                    address = query("select city from info where area = ? limit 1", new String[] {number.substring(0, 4)});
                    if(address.equals(""))
                    {
                        address = number;
                    }
                    break;

                default :
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