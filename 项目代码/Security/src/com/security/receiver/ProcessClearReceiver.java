package com.security.receiver;

import com.security.utils.ProcessUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProcessClearReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//清理内存
		ProcessUtil.killAllProcess(context);
	}

}
