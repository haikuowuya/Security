package com.security.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.security.R;

public class MyToast
{
	
	//自定义Toast
	public static void showToast(Context context, int id, String text)
	{
		View view = View.inflate(context, R.layout.mytoast, null);
		TextView tv_toast_msg = (TextView) view.findViewById(R.id.tv_toast_msg);
		Drawable drawable = context.getResources().getDrawable(id);
		//在左边设置一张图片，对应android:drawableLeft这个属性
		tv_toast_msg.setCompoundDrawables(drawable, null, null, null);
		tv_toast_msg.setText(text);
		
		Toast toast = new Toast(context);
		toast.setDuration(0);
		toast.setView(view);
		toast.show();
	}

}
