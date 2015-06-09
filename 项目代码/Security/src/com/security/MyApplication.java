package com.security;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.security.domain.TaskInfo;
import com.security.receiver.LockScreenReceiver;

public class MyApplication extends Application
{
	
	private TaskInfo taskInfo;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		intentFilter.setPriority(1000);
		LockScreenReceiver receiver = new LockScreenReceiver();
		registerReceiver(receiver, intentFilter);
	}

	public TaskInfo getTaskInfo()
	{
		return taskInfo;
	}

	public void setTaskInfo(TaskInfo taskInfo)
	{
		this.taskInfo = taskInfo;
	}
	
}
