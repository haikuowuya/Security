package com.security.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.security.domain.TrafficInfo;
import com.security.utils.TextFormater;
import com.security.R;

public class TrafficManagerActivity extends Activity
{
	private TextView tv_traffic_2g_3g;
	private TextView tv_traffic_wifi;
	private ListView lv_traffic_content;
	private TrafficAdapter adapter;

	private List<TrafficInfo> trafficInfos;

	private Timer timer;
	private TimerTask timerTask;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.traffic_manager);

		tv_traffic_2g_3g = (TextView) findViewById(R.id.tv_traffic_2g_3g);
		tv_traffic_wifi = (TextView) findViewById(R.id.tv_traffic_wifi);
		setTotalTraffic();

		trafficInfos = new ArrayList<TrafficInfo>();
		initResolveInfos();

		lv_traffic_content = (ListView) findViewById(R.id.lv_traffic_content);
		adapter = new TrafficAdapter();
		lv_traffic_content.setAdapter(adapter);
	}

	@Override
	protected void onStart()
	{
		timer = new Timer();
		timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				Message msg = Message.obtain();
				handler.sendMessage(msg);
			}
		};
		timer.schedule(timerTask, 1000, 3000);
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
			timerTask = null;
		}
		super.onStop();
	}

	private void setTotalTraffic()
	{
		// 拿到2G和3G的总共接收到的数据大小
		long total_2g_3g_received = TrafficStats.getMobileRxBytes();
		// 拿到2G和3G的总共发送出去的数据大小
		long total_2g_3g_transmitted = TrafficStats.getMobileTxBytes();
		// 拿到2G和3G的总数据大小
		long total_2g_3g = total_2g_3g_received + total_2g_3g_transmitted;

		tv_traffic_2g_3g.setText("2G/3g 总流量："
				+ TextFormater.dataSizeFormat(total_2g_3g));

		// 拿到总共接收到的数据大小
		long total_received = TrafficStats.getTotalRxBytes();
		// 拿到总共发送的数据大小
		long total_transmitted = TrafficStats.getTotalTxBytes();
		// 拿到总数据大小
		long total = total_received + total_transmitted;
		// //拿到wifi的总数据大小
		long total_wifi = total - total_2g_3g;

		tv_traffic_wifi.setText("wifi 总流量："
				+ TextFormater.dataSizeFormat(total_wifi));
	}

	// 拿到所有会产生流量的应用信息
	private void initResolveInfos()
	{
		trafficInfos.clear();
		// 拿到一个包管理器
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent();
		// android.intent.action.MAIN这个action代表的就是应用的入口
		intent.setAction("android.intent.action.MAIN");
		// android.intent.category.LAUNCHER代表的就是在桌面创建一个图标
		intent.addCategory("android.intent.category.LAUNCHER");
		// 这个方法就是根据对应的条件，intent指定条件，然后查询出相应的activity
		// 那么根据我们上面设置的intent，我们就可以知道，我们要查询的是应用的入口activity而且是桌面上有图标的activity
		// 因为这样的应用，才会有可能产生流量的
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(
				intent, 0);

		for (ResolveInfo resolveInfo : resolveInfos)
		{
			// 得到应用的名字
			String name = resolveInfo.loadLabel(packageManager).toString();
			// 得到应用的图标
			Drawable icon = resolveInfo.loadIcon(packageManager);
			// 得到应用的包名
			String packageName = resolveInfo.activityInfo.packageName;
			int uid = 0;
			;
			try
			{
				// 得到应用的packageInfo对象
				PackageInfo packageInfo = packageManager.getPackageInfo(
						packageName, 0);
				// 得到这个应用对应的uid
				uid = packageInfo.applicationInfo.uid;
				// 根据uid得到这个应用的接收数据大小
				long received = TrafficStats.getUidRxBytes(uid);
				// 根据uid得到这个应用的发送数据大小
				long transmitted = TrafficStats.getUidTxBytes(uid);
				// 有些应用不会产生流量信息的，拿到的值就会是-1
				// 不产生流量的，我们就不把它加入到list里面
				if (received == -1 && transmitted == -1)
				{
					continue;
				}
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
			TrafficInfo trafficInfo = new TrafficInfo();
			trafficInfo.setName(name);
			trafficInfo.setIcon(icon);
			trafficInfo.setUid(uid);
			trafficInfos.add(trafficInfo);
		}
	}

	// ============================================================================================

	private class TrafficAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return trafficInfos.size();
		}

		@Override
		public Object getItem(int position)
		{
			return trafficInfos.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view;
			ViewHolder holder;
			TrafficInfo info = trafficInfos.get(position);
			if (convertView == null)
			{
				view = View.inflate(TrafficManagerActivity.this,
						R.layout.traffic_manager_item, null);
				holder = new ViewHolder();
				holder.iv_traffic_icon = (ImageView) view
						.findViewById(R.id.iv_traffic_icon);
				holder.tv_traffic_name = (TextView) view
						.findViewById(R.id.tv_traffic_name);
				holder.tv_traffic_received = (TextView) view
						.findViewById(R.id.tv_traffic_received);
				holder.tv_traffic_transmitted = (TextView) view
						.findViewById(R.id.tv_traffic_transmitted);
				view.setTag(holder);
			}
			else
			{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.iv_traffic_icon.setImageDrawable(info.getIcon());
			holder.tv_traffic_name.setText(info.getName());
			// 根据uid得到这个应用的接收数据大小
			long received = TrafficStats.getUidRxBytes(info.getUid());
			// 根据uid得到这个应用的发送数据大小
			long transmitted = TrafficStats.getUidTxBytes(info.getUid());
			holder.tv_traffic_received.setText(TextFormater
					.dataSizeFormat(received));
			holder.tv_traffic_transmitted.setText(TextFormater
					.dataSizeFormat(transmitted));
			return view;
		}

	}

	private class ViewHolder
	{
		ImageView iv_traffic_icon;
		TextView tv_traffic_name;
		TextView tv_traffic_received;
		TextView tv_traffic_transmitted;
	}

}
