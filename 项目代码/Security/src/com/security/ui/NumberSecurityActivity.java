package com.security.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.security.dao.BlackNumberDao;
import com.security.R;

public class NumberSecurityActivity extends Activity
{
	private ListView lv_number;
	private Button bt_number_add;
	private BlackNumberDao dao;
	private List<String> numbers;
	private NumberAdapter adapter;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.number_security);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		dao = new BlackNumberDao(this);
		numbers = dao.findAll();
		adapter = new NumberAdapter();
		lv_number = (ListView) findViewById(R.id.lv_number);
		lv_number.setAdapter(adapter);
		//给listview注册一个上下文菜单
		registerForContextMenu(lv_number);
		
		bt_number_add = (Button) findViewById(R.id.bt_number_add);
		bt_number_add.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				addNumber(null);
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Intent intent = getIntent();
		String number = intent.getStringExtra("number");
		if(number != null)
		{
			addNumber(number);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.number, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		//拿到点击的菜单的信息
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId())
		{
			case R.id.update_number : 
				String oldNumber = numbers.get((int) info.id);
				updateNumber(oldNumber);
				break;
				
			case R.id.delete_number : 
				int id = (int) info.id;
				String number = numbers.get(id);
				dao.delete(number);
				numbers = dao.findAll();
				adapter.notifyDataSetChanged();
				break;
				
			default : 
				break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void addNumber(String number)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(NumberSecurityActivity.this);
		builder.setTitle("添加黑名单");
		final EditText et_number = new EditText(NumberSecurityActivity.this);
		et_number.setInputType(InputType.TYPE_CLASS_PHONE);
		et_number.setHint("请输入黑名单号码");
		if(number != null)
		{
			et_number.setText(number);
		}
		builder.setView(et_number);
		builder.setPositiveButton("添加", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String number = et_number.getText().toString().trim();
				if(TextUtils.isEmpty(number))
				{
					Toast.makeText(NumberSecurityActivity.this, "添加号码不能为空", Toast.LENGTH_SHORT).show();
				}
				else
				{
					boolean addressService = sp.getBoolean("AddressService", false);
					if (!addressService)
						Toast.makeText(NumberSecurityActivity.this, "请在高级工具中开启通讯服务!", Toast.LENGTH_LONG).show();
					dao.add(number);
					numbers = dao.findAll();
					adapter.notifyDataSetChanged();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				
			}
		});
		builder.create().show();
	}
	
	private void updateNumber(final String oldNumber)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(NumberSecurityActivity.this);
		builder.setTitle("更新黑名单");
		final EditText et_number = new EditText(NumberSecurityActivity.this);
		et_number.setInputType(InputType.TYPE_CLASS_PHONE);
		et_number.setHint("请输入新的号码");
		builder.setView(et_number);
		builder.setPositiveButton("修改", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String number = et_number.getText().toString().trim();
				if(TextUtils.isEmpty(number))
				{
					Toast.makeText(NumberSecurityActivity.this, "添加号码不能为空", Toast.LENGTH_SHORT).show();
				}
				else
				{
					dao.update(oldNumber, number);
					numbers = dao.findAll();
					adapter.notifyDataSetChanged();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				
			}
		});
		builder.create().show();
	}
	
	//==================================================================
	
	private class NumberAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return numbers.size();
		}

		@Override
		public Object getItem(int position)
		{
			return numbers.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				View view = View.inflate(NumberSecurityActivity.this, R.layout.number_security_item, null);
				TextView tv_item = (TextView) view.findViewById(R.id.tv_number_item);
				tv_item.setText(numbers.get(position));
				return view;
			}
			else
			{
				TextView tv_item = (TextView) convertView.findViewById(R.id.tv_number_item);
				tv_item.setText(numbers.get(position));
				return convertView;
			}
		}
	}

}
