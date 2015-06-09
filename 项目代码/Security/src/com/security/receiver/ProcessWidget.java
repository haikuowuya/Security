package com.security.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.security.service.UpdateWidgetService;

public class ProcessWidget extends AppWidgetProvider
{
	private Intent intent;

	@Override
	public void onEnabled(Context context)
	{
		super.onEnabled(context);
		//开启服务
		intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		super.onDeleted(context, appWidgetIds);
		//停止服务
		intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}

}
