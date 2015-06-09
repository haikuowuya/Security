package com.security.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.security.iservice.IService;
import com.security.service.WatchDogService;
import com.security.utils.MD5Encoder;
import com.security.R;

public class LockActivity extends Activity
{
	private ImageView iv_app_icon;
	private TextView tv_app_name;
	private EditText et_app_pwd;
	private String password;

	private MyConnection connection;
	private IService iService;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock);

		connection = new MyConnection();
		// 绑定服务，主要是为了能够调用服务里面的方法
		Intent intent = new Intent(this, WatchDogService.class);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);

		iv_app_icon = (ImageView) findViewById(R.id.iv_lock_app_icon);
		tv_app_name = (TextView) findViewById(R.id.tv_lock_app_name);
		et_app_pwd = (EditText) findViewById(R.id.et_lock_pwd);

		// 拿到真实的密码
		// 这里我们为了方便，就使用了手机防盗里面的那个密码，大家可以自己做成可以让用户设置的功能
		password = getSharedPreferences("config", Context.MODE_PRIVATE)
				.getString("password", "");

		try
		{
			packageName = getIntent().getStringExtra("packageName");
			// 通过包名拿到applicationInfo
			ApplicationInfo appInfo = getPackageManager().getPackageInfo(
					packageName, 0).applicationInfo;
			// 应用图标
			Drawable app_icon = appInfo.loadIcon(getPackageManager());
			// 应用的名字
			String app_name = appInfo.loadLabel(getPackageManager()).toString();

			iv_app_icon.setImageDrawable(app_icon);
			tv_app_name.setText(app_name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// 按钮的点击事件
	public void confirm(View v)
	{
		String input = et_app_pwd.getText().toString().trim();
		if (TextUtils.isEmpty(password))
		{
			Toast.makeText(this, "您的密码还没有设置，请进入手机防盗进行设定", Toast.LENGTH_SHORT)
					.show();
		}
		else if (TextUtils.isEmpty(input))
		{
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
		}
		else if (password.equals(MD5Encoder.encode(input)))
		{
			finish();
			iService.stopApp(packageName);
		}
		else
		{
			Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// 屏蔽后退键
		if (KeyEvent.KEYCODE_BACK == event.getKeyCode())
		{
			return true;// 阻止事件继续向下分发
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		if (connection != null)
		{
			unbindService(connection);
			connection = null;
		}
		super.onDestroy();
	}

	// ===========================================================================

	private class MyConnection implements ServiceConnection
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			// 我们之前在Service里面已经实现了IService接口了
			iService = (IService) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}

	}

}
