package com.security.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.security.dao.CommonNumberDao;
import com.security.R;

public class CommonNumberActivity extends Activity
{
	private static final String DBPATH = Environment
			.getExternalStorageDirectory() + "/security/db/commonnum.db";

	private ExpandableListView elv_list;
	private MyBaseExpandableListAdapter adapter;
	private List<String> group;
	private List<List<String>> childs;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			// 拿到数据库里面的内容
			group = CommonNumberDao.getAllGroup(DBPATH);
			childs = CommonNumberDao.getAllChildren(DBPATH, group.size());
			// 当移动成功数据库之后，就把数据显示出来
			adapter = new MyBaseExpandableListAdapter();
			elv_list.setAdapter(adapter);
			// 给子列表添加点击事件
			elv_list.setOnChildClickListener(new OnChildClickListener()
			{
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id)
				{
					String str = childs.get(groupPosition).get(childPosition);
					// 拿到电话号码
					String number = str.split("-")[1];
					// 设置打电话的intent
					Intent intent = new Intent("android.intent.action.CALL",
							Uri.parse("tel:" + number));
					startActivity(intent);
					return false;
				}
			});
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_number);

		elv_list = (ExpandableListView) findViewById(R.id.elv_list);

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			File file = new File(DBPATH);
			// 如果数据库文件已经存在，就不用移动啦
			if (file.exists())
			{
				handler.sendEmptyMessage(1);
			}
			else
			{
				// 把数据库从assets里面读取出来
				moveDatabase(file);
			}
		}
		else
		{
			Toast.makeText(this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
		}
	}

	// 把数据库从assets里面读取到sd卡里面,开启了一条线程进程操作，等读取完成之后，发送一个消息给handler处理
	private void moveDatabase(File file)
	{
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/security/db");
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		else
		{
			try
			{
				final InputStream is = getAssets().open("commonnum.db");
				final FileOutputStream fos = new FileOutputStream(file);
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							int len = 0;
							byte[] buffer = new byte[1024];
							while ((len = is.read(buffer)) != -1)
							{
								fos.write(buffer, 0, len);
							}
							fos.flush();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						finally
						{
							if (is != null)
							{
								try
								{
									is.close();
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							}
							if (fos != null)
							{
								try
								{
									fos.close();
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						}
						handler.sendEmptyMessage(1);
					}
				}).start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	// ========================================================================

	private class MyBaseExpandableListAdapter extends BaseExpandableListAdapter
	{

		// 有多少个分组
		@Override
		public int getGroupCount()
		{
			return group.size();
		}

		// 对应分组的条目个数
		@Override
		public int getChildrenCount(int groupPosition)
		{
			return childs.get(groupPosition).size();
		}

		// 拿到第几个分组对应的对象
		@Override
		public Object getGroup(int groupPosition)
		{
			return group.get(groupPosition);
		}

		// 拿到第几个分组对应的第几个条目
		@Override
		public Object getChild(int groupPosition, int childPosition)
		{

			return childs.get(groupPosition).get(childPosition);
		}

		// 拿到第几个分组对应的位置
		@Override
		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		// //拿到第几个分组对应的第几个条目位置
		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		// 这个具体的作用，我也还没弄清楚，各位有了解的，不访告诉我们一声
		// 官方文档是这样说的：组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
		// 返回一个Boolean类型的值，如果为TRUE，意味着相同的ID永远引用相同的对象。
		// 具体有什么用，我就弄不懂了
		@Override
		public boolean hasStableIds()
		{
			return false;
		}

		// 返回对应的分组的view
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent)
		{
			View view;
			ViewHolder holder;
			if (convertView == null)
			{
				view = View.inflate(CommonNumberActivity.this,
						R.layout.expandable_group, null);
				TextView textView = (TextView) view
						.findViewById(R.id.expandable_group);
				holder = new ViewHolder();
				holder.tv_group = textView;
				view.setTag(holder);
			}
			else
			{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.tv_group.setText(group.get(groupPosition));
			return view;
		}

		// 返回对应分组的对应条目的view
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent)
		{
			View view;
			ViewHolder holder;
			if (convertView == null)
			{
				view = View.inflate(CommonNumberActivity.this,
						R.layout.expandable_children, null);
				TextView tv_name = (TextView) view
						.findViewById(R.id.expandable_child_name);
				TextView tv_number = (TextView) view
						.findViewById(R.id.expandable_child_num);
				holder = new ViewHolder();
				holder.tv_name = tv_name;
				holder.tv_number = tv_number;
				view.setTag(holder);
			}
			else
			{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			String str = childs.get(groupPosition).get(childPosition);
			// 分割出来
			String[] msg = str.split("-");
			holder.tv_name.setText(msg[0]);
			holder.tv_number.setText(msg[1]);
			return view;
		}

		// 是不是允许分组里面的条目接收点击事件，false为不允许，true为允许
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

	}

	private class ViewHolder
	{
		TextView tv_group;
		TextView tv_name;
		TextView tv_number;
	}

}
