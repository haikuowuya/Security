package com.security.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.security.receiver.ProcessClearReceiver;
import com.security.utils.ProcessUtil;
import com.security.R;

public class UpdateWidgetService extends Service
{
	private Timer timer;
	private TimerTask timerTask;
	private AppWidgetManager appWidgetManager;
	private ComponentName componentName;
	private RemoteViews remoteViews;
	private Intent intent;
	private PendingIntent pendingIntent;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		// 拿到一个AppWidgetManager对象
		appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());
		// 拿到一个ComponentName对象
		componentName = new ComponentName("com.security",
				"com.security.receiver.ProcessWidget");
		// 就是显示到桌面上的界面啦，所有布局是我们写的那个widget的布局
		remoteViews = new RemoteViews("com.security",
				R.layout.process_widget);
		// 这个intent是用来启动一个广播的，因为我们的winget是一键清理的嘛
		// 所以我们现在只要发送一条广播就可以调用里面的清理方法啦
		// 但我这里就不做啦，大家可以自己试试
		intent = new Intent(UpdateWidgetService.this, ProcessClearReceiver.class);
		// 拿到一个PendingIntent对象，来发送一条广播
		pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
				intent, 0);
		// java传统的计时器
		timer = new Timer();
		timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				//这里就没设置的们的process_widget这个布局文件里面的内容的啦
				//第一个参数就是设置的那个控件的id啦
				remoteViews.setTextViewText(R.id.tv_process_count, "进程数目："
						+ ProcessUtil.getProcessCount(getApplicationContext()));
				remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存："
						+ ProcessUtil.getAvailMemory(getApplicationContext()));
				//这个就是给按钮加上点击事件啦
				remoteViews.setOnClickPendingIntent(R.id.bt_clear, pendingIntent);
				//更新widget里面的内容
				appWidgetManager.updateAppWidget(componentName, remoteViews);
			}
		};
		//开启计时器
		timer.scheduleAtFixedRate(timerTask, 1000, 3000);
		super.onCreate();

	}
	
	@Override
	public void onDestroy()
	{
		//取消计时器
		timer.cancel();
		timer = null;
		timerTask = null;
		super.onDestroy();
	}

}
