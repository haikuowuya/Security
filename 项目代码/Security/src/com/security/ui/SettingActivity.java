package com.security.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.security.service.WatchDogService;
import com.security.R;

public class SettingActivity extends Activity
{
	private TextView tv_lock_tips;
	private CheckBox cb_lock_state;
	private Intent appLockIntent;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		
		appLockIntent = new Intent(this, WatchDogService.class);
		
		tv_lock_tips = (TextView) findViewById(R.id.tv_lock_tips);
		cb_lock_state = (CheckBox) findViewById(R.id.cb_lock_state);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isAppLockStart = sp.getBoolean("appLock", false);
		if(isAppLockStart)
		{
			tv_lock_tips.setText("服务已经开启");
			cb_lock_state.setChecked(true);
		}
		else
		{
			tv_lock_tips.setText("服务没有开启");
			cb_lock_state.setChecked(false);
		}
		
		cb_lock_state.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				Editor editor = sp.edit();
				if(isChecked)
				{
					startService(appLockIntent);
					tv_lock_tips.setText("服务已经开启");
					editor.putBoolean("appLock", true);
				}
				else
				{
					stopService(appLockIntent);
					tv_lock_tips.setText("服务没有开启");
					editor.putBoolean("appLock", false);
				}
				editor.commit();
			}
		});
	}

}
