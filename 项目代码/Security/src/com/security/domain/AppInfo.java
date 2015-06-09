package com.security.domain;

import android.graphics.drawable.Drawable;

public class AppInfo
{
	private Drawable icon;
	private String appName;
	private String packageName;
	private boolean isSystemApp;
	
	public Drawable getIcon()
	{
		return icon;
	}
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	public String getAppName()
	{
		return appName;
	}
	public void setAppName(String appName)
	{
		this.appName = appName;
	}
	public String getPackageName()
	{
		return packageName;
	}
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public boolean isSystemApp()
	{
		return isSystemApp;
	}
	public void setSystemApp(boolean isSystemApp)
	{
		this.isSystemApp = isSystemApp;
	}

}
