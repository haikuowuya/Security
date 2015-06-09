package com.security.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.security.adapter.MainUIAdapter;
import com.security.R;

public class MainActivity extends Activity implements OnItemClickListener
{
	private GridView gridView;
	
	private MainUIAdapter adapter ;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		gridView = (GridView) findViewById(R.id.gv_main);
		adapter = new MainUIAdapter(this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id)
			{
				if(position == 0)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("设置");
					builder.setMessage("请输入要改成的名称");
					final EditText et = new EditText(MainActivity.this);
					et.setHint("新名称");
					builder.setView(et);
					builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							String name = et.getText().toString();
							if(name.equals(""))
							{
								Toast.makeText(MainActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
							}
							else
							{
								Editor editor = sp.edit();
								editor.putString("lostName", name);
								editor.commit();
								
								TextView tv = (TextView) view.findViewById(R.id.tv_main_name);
								tv.setText(name);
								adapter.notifyDataSetChanged();
							}
						}
					});
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
						}
					});
					builder.create().show();
				}
				return false;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		switch(position)
		{
			case 0 : //手机防盗
				Intent intent = new Intent(MainActivity.this, LostProtectedActivity.class);
				startActivity(intent);
				break;
				
			case 1 : //通讯卫士
				Intent i = new Intent(this, NumberSecurityActivity.class);
				startActivity(i);
				break;
				
			case 2 : //软件管理
				Intent app_manager_intent = new Intent(this, AppManagerActivity.class);
				startActivity(app_manager_intent);
				break;
			
			case 3 : //流量管理
				Intent trafficIntent = new Intent(this, TrafficManagerActivity.class);
				startActivity(trafficIntent);
				break;
				
			case 4 : //任务管理
				Intent progressManagerIntent = new Intent(this, ProcessManagerActivity.class);
				startActivity(progressManagerIntent);
				break;
				
			case 5 : //系统优化
				Intent optimizeIntent = new Intent(this, CacheClearActivity.class);
				startActivity(optimizeIntent);
				break;
				
			case 6 : //高级工具
				Intent atoolIntent  = new Intent(this, AToolActivity.class);
				startActivity(atoolIntent);
				break;
				
			case 7 : //设置中心
				Intent settingIntent  = new Intent(this, SettingActivity.class);
				startActivity(settingIntent);
				break;
				
			default : 
				break;
		}
	}

}
