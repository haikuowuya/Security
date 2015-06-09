package com.security.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.security.dao.AppLockDao;
import com.security.iservice.IService;
import com.security.ui.LockActivity;

public class WatchDogService extends Service
{
	private AppLockDao dao;
	private List<String> apps;
	private ActivityManager activityManager;
	private Intent intent;
	private boolean flag = true;
	//存放要停止保护的app
	private List<String> stopApps;
	private MyBinder myBinder;
	
	//键盘的管理器
	private KeyguardManager keyguardManager;

	@Override
	public IBinder onBind(Intent intent)
	{
		return myBinder;
	}
	
	//临时开启对某个应用的保护
	private void invokeMethodStartApp(String packageName)
	{
		if(stopApps.contains(packageName))
		{
			stopApps.remove(packageName);
		}
	}
	
	//临时停止对某个应用的保护
	private void invokeMethodStopApp(String packageName)
	{
		stopApps.add(packageName);
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		keyguardManager = (KeyguardManager) getSystemService(Service.KEYGUARD_SERVICE);
		
		myBinder = new MyBinder();
		dao = new AppLockDao(this);
		apps = dao.getAllPackageName();
		stopApps = new ArrayList<String>();
		activityManager = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);
		
		//注册一个内容观察者
		getContentResolver().registerContentObserver(Uri.parse("content://com.security.applockprovider"), true, new MyObserver(new Handler()));
		
		intent = new Intent(this, LockActivity.class);
		//服务里面是没有任务栈的，所以要指定一个新的任务栈，不然是无法在服务里面启动activity的
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		new Thread()
		{
			public void run() 
			{
				while(flag)
				{
					try
					{
						//当解锁后，就再一次输入密码
						if(keyguardManager.inKeyguardRestrictedInputMode())
						{
							stopApps.clear();
						}
						
						//得到当前运行的任务栈，参数就是得到多少个任务栈，1就是只拿一个任务栈
						//1对应的也就是正在运行的任务栈啦
						List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
						//拿到当前运行的任务栈
						RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
						//拿到要运行的Activity的包名
						String packageName = runningTaskInfo.topActivity.getPackageName();
						System.out.println("当前在运行：" + packageName);
						
						//这样，我们就可以临时的把一个应用取消保护
						if(stopApps.contains(packageName))
						{
							sleep(1000);
							continue;
						}
						
						if(apps.contains(packageName))
						{
							intent.putExtra("packageName", packageName);
							startActivity(intent);
						}
						else
						{
							
						}
						sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		flag = false;
	}
	
	//==================================================================
	
	private class MyBinder extends Binder implements IService
	{

		@Override
		public void startApp(String packageName)
		{
			invokeMethodStartApp(packageName);
		}

		@Override
		public void stopApp(String packageName)
		{
			invokeMethodStopApp(packageName);
		}
		
	}

	private class MyObserver extends ContentObserver
	{

		public MyObserver(Handler handler)
		{
			super(handler);
			//重新更新apps里面的内容
			apps = dao.getAllPackageName();
			System.out.println("数据库的内容发生了改变");
		}
		
	}
	
}
