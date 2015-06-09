package com.security.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.security.domain.ContactInfo;
import com.security.engine.ContactInfoService;
import com.security.R;

public class SelectContactActivity extends Activity
{
	private ListView lv;
	private List<ContactInfo> infos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_contact);
		
		infos = new ContactInfoService(this).getContactInfos();
		
		lv = (ListView) findViewById(R.id.lv_select_contact);
		lv.setAdapter(new SelectContactAdapter());
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				String number = infos.get(position).getPhone();
				Intent intent = new Intent();
				intent.putExtra("number", number);
				setResult(1, intent);
				finish();
			}
		});
	}
	
	//=================================================================================
	
	private class SelectContactAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return infos.size();
		}

		@Override
		public Object getItem(int position)
		{
			return infos.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ContactInfo info = infos.get(position);
			View view;
			ContactViews views;
			if(convertView == null)
			{
				views = new ContactViews();
				view = View.inflate(SelectContactActivity.this, R.layout.contact_item, null);
				views.tv_name = (TextView) view.findViewById(R.id.tv_contact_name);
				views.tv_number = (TextView) view.findViewById(R.id.tv_contact_number);
				views.tv_name.setText("联系人：" + info.getName());
				views.tv_number.setText("联系电话：" + info.getPhone());
				
				view.setTag(views);
			}
			else
			{
				view = convertView;
			}
			return view;
		}
		
	}
	
	private class ContactViews
	{
		TextView tv_name;
		TextView tv_number;
	}

}
