package com.security.domain;

import android.graphics.drawable.Drawable;

public class CacheInfo
{
	private String name;
	private String packageName;
	private Drawable icon;
	//应用大小
	private String codeSize;
	//数据大小
	private String dataSize;
	//缓存大小
	private String cacheSize;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPackageName()
	{
		return packageName;
	}
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public Drawable getIcon()
	{
		return icon;
	}
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	public String getCodeSize()
	{
		return codeSize;
	}
	public void setCodeSize(String codeSize)
	{
		this.codeSize = codeSize;
	}
	public String getDataSize()
	{
		return dataSize;
	}
	public void setDataSize(String dataSize)
	{
		this.dataSize = dataSize;
	}
	public String getCacheSize()
	{
		return cacheSize;
	}
	public void setCacheSize(String cacheSize)
	{
		this.cacheSize = cacheSize;
	}

}
