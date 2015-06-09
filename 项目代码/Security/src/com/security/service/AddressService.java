package com.security.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.security.dao.BlackNumberDao;
import com.security.engine.NumberAddressService;
import com.security.ui.NumberSecurityActivity;
import com.security.R;

public class AddressService extends Service
{
	private TelephonyManager telephonyManager;
	private MyPhoneListener listener;
	private WindowManager windowManager;
	private View view;
	private BlackNumberDao dao;

	private SharedPreferences sp;
	private long start;
	private long end;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		//Toast.makeText(this, "AddressService", Toast.LENGTH_SHORT).show();
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		dao = new BlackNumberDao(this);

		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		listener = new MyPhoneListener();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		// 停止监听
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
	}

	// 挂断电话
	private void endCall()
	{
		try
		{
			// 通过反射拿到android.os.ServiceManager里面的getService这个方法的对象
			Method method = Class.forName("android.os.ServiceManager")
					.getMethod("getService", String.class);
			// 通过反射调用这个getService方法，然后拿到IBinder对象，然后就可以进行aidl啦
			IBinder iBinder = (IBinder) method.invoke(null,
					new Object[] { TELEPHONY_SERVICE });
			ITelephony telephony = ITelephony.Stub.asInterface(iBinder);
			telephony.endCall();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// 清除通话记录
	private void cleanCallLog(String incomingNumber)
	{
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
				" number = ? ", new String[] { incomingNumber }, null);
		if (cursor.moveToNext())
		{
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, " _id = ? ",
					new String[] { id });
		}
	}

	// 显示归属地的窗体
	private void showLocation(String address)
	{
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // 无法获取焦点
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE // 无法点击
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 保持屏幕亮
		params.format = PixelFormat.TRANSLUCENT;// 设置成半透明的
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");

		// 主要是确定坐标系是从左上角开始的，不然呆会设置位置的时候有些麻烦
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);

		view = View.inflate(getApplicationContext(), R.layout.show_location,
				null);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_location);
		int type = sp.getInt("background", 0);
		switch (type)
		{
			case 0:
				ll.setBackgroundResource(R.drawable.call_locate_white);
				break;

			case 1:
				ll.setBackgroundResource(R.drawable.call_locate_orange);
				break;

			case 2:
				ll.setBackgroundResource(R.drawable.call_locate_green);
				break;

			case 3:
				ll.setBackgroundResource(R.drawable.call_locate_blue);
				break;

			case 4:
				ll.setBackgroundResource(R.drawable.call_locate_gray);
				break;

			default:
				break;
		}

		TextView tv = (TextView) view.findViewById(R.id.tv_show_location);
		tv.setText("归属地： " + address);
		windowManager.addView(view, params);
	}

	private void showNotifycation(String number)
	{
		// 拿到Nofitication的管理者
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// new一个Nofitication出来
		Notification notification = new Notification(R.drawable.notification,
				"发现响一声电话", System.currentTimeMillis());
		Context context = getApplicationContext();
		// 设置成一点击就消失
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent = new Intent(context,
				NumberSecurityActivity.class);
		notificationIntent.putExtra("number", number);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification
				.setLatestEventInfo(context, "响一声号码", number, pendingIntent);
		// 激活Nofitication
		notificationManager.notify(0, notification);
	}

	// ========================================================================

	private class MyPhoneListener extends PhoneStateListener
	{
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			super.onCallStateChanged(state, incomingNumber);

			switch (state)
			{
				case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
					end = System.currentTimeMillis();
					if ((end > start) && ((end - start) < 2000))
					{
						start = end = 0;
						showNotifycation(incomingNumber);
					}
					if (view != null)
					{
						windowManager.removeView(view);// 移除显示归属地的那个view
						view = null;
					}
					break;

				case TelephonyManager.CALL_STATE_OFFHOOK: // 接通电话
					if (view != null)
					{
						windowManager.removeView(view);// 移除显示归属地的那个view
						view = null;
					}
					break;

				case TelephonyManager.CALL_STATE_RINGING: // 铃响状态
					start = System.currentTimeMillis();
					String address = NumberAddressService
							.getAddress(incomingNumber);

					Log.i("Security", "-------------   ");
					Log.i("Security", "-------------   " + incomingNumber);
					Log.i("Security", "-------------   ");
					
					
					showLocation(address);
					
					if (dao.find(incomingNumber))
					{
						endCall();
						// 注册一个内容观察者，如果内容发生了改变之后，就执行删除的操作
						getContentResolver().registerContentObserver(
								CallLog.Calls.CONTENT_URI, true,
								new MyObserver(new Handler(), incomingNumber));
					}
					
					break;

				default:
					break;
			}
		}
	}

	private class MyObserver extends ContentObserver
	{
		private String number;

		public MyObserver(Handler handler, String number)
		{
			super(handler);
			this.number = number;
		}

		@Override
		public void onChange(boolean selfChange)
		{
			super.onChange(selfChange);

			cleanCallLog(number);
			getContentResolver().unregisterContentObserver(this);
		}

	}

}
