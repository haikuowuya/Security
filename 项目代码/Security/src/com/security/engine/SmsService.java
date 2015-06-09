package com.security.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import com.security.domain.SmsInfo;

public class SmsService
{
	private Context context;
	
	public SmsService(Context context)
	{
		this.context = context;
	}
	
	public List<SmsInfo> getSmsInfo()
	{
		List<SmsInfo> infos = new ArrayList<SmsInfo>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] {"_id", "address", "date", "type", "body"}, null, null, " date desc ");
		SmsInfo info;
		while(cursor.moveToNext())
		{
			info = new SmsInfo();
			String id = cursor.getString(0);
			String address = cursor.getString(1);
			String date = cursor.getString(2);
			int type = cursor.getInt(3);
			String body = cursor.getString(4);
			info.setId(id);
			info.setAddress(address);
			info.setDate(date);
			info.setType(type);
			info.setBody(body);
			infos.add(info);
		}
		cursor.close();
		return infos;
	}
	
	//还原短信  path为文件路径
	public void restore(String path, ProgressDialog pd) throws Exception
	{
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		ContentValues values = null;
		parser.setInput(fis, "utf-8");
		int type = parser.getEventType();
		int index = 0;
		while(type != XmlPullParser.END_DOCUMENT)
		{
			switch(type)
			{
				case XmlPullParser.START_TAG : 
					if("count".equals(parser.getName()))
					{
						int count = Integer.parseInt(parser.nextText());
						pd.setMax(count);
					}
					
					if("sms".equals(parser.getName()))
					{
						values = new ContentValues();
					}
					else if("address".equals(parser.getName()))
					{
						values.put("address", parser.nextText());
					}
					else if("date".equals(parser.getName()))
					{
						values.put("date", parser.nextText());
					}
					else if("type".equals(parser.getName()))
					{
						values.put("type", parser.nextText());
					}
					else if("body".equals(parser.getName()))
					{
						values.put("body", parser.nextText());
					}
					break;
					
				case XmlPullParser.END_TAG : 
					if("sms".equals(parser.getName()))
					{
						ContentResolver resolver = context.getContentResolver();
						resolver.insert(Uri.parse("content://sms/"), values);
						values = null;
						index++;
						pd.setProgress(index);
					}
					break;
					
				default : 
					break;
			}
			type = parser.next();
		}
	}

}
