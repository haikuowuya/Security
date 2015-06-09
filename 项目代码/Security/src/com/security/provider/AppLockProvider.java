package com.security.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.security.dao.AppLockDao;

public class AppLockProvider extends ContentProvider
{
	//分别定义两个返回值
	private static final int INSERT = 1;
	private static final int DELETE = 0;
	//先new一个UriMatcher出来，参数就是当没有匹配到的时候，返回的值是什么
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static Uri URI = Uri.parse("content://com.security.applockprovider");
	private AppLockDao dao;
	static
	{
		matcher.addURI("com.security.applockprovider", "insert", INSERT);
		matcher.addURI("com.security.applockprovider", "delete", DELETE);
	}

	@Override
	public boolean onCreate()
	{
		dao = new AppLockDao(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		return null;
	}

	@Override
	public String getType(Uri uri)
	{
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		//上面定义的返回值
		int result = matcher.match(uri);
		if(result == INSERT)
		{
			String packageName = values.getAsString("packageName");
			dao.add(packageName);
			//如果数据发生了改变就通知
			getContext().getContentResolver().notifyChange(URI, null);
		}
		else
		{
			new IllegalArgumentException("URI地址不正确");
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		//上面定义的返回值
		int result = matcher.match(uri);
		if(result == DELETE)
		{
			String packageName = selectionArgs[0];
			dao.delete(packageName);
			//如果数据发生了改变就通知
			getContext().getContentResolver().notifyChange(URI, null);
		}
		else
		{
			new IllegalArgumentException("URI地址不正确");
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		return 0;
	}

}
