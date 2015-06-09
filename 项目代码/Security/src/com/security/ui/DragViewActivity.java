package com.security.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.security.R;

public class DragViewActivity extends Activity implements OnTouchListener
{
	private ImageView iv_drag_location;
	private SharedPreferences sp;
	
	//记录第一次触摸的坐标
	private int startX;
	private int startY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drag_view);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		
		iv_drag_location = (ImageView) findViewById(R.id.iv_drag_location);
		iv_drag_location.setOnTouchListener(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		//加载上次移动的效果
		int x = sp.getInt("lastX", 0);
		int y = sp.getInt("lastY", 0);
		/*iv_drag_location.layout(iv_drag_location.getLeft() + x, iv_drag_location.getTop() + y, 
				iv_drag_location.getRight() + x, iv_drag_location.getBottom() + y);
		iv_drag_location.invalidate();*/
		RelativeLayout.LayoutParams params = (LayoutParams) iv_drag_location.getLayoutParams();
		params.leftMargin = x;
		params.topMargin = y;
		iv_drag_location.setLayoutParams(params);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		switch(v.getId())
		{
			case R.id.iv_drag_location : 
				switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN : 
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
						
					case MotionEvent.ACTION_MOVE : 
						int x = (int) event.getRawX();
						int y = (int) event.getRawY();
						
						//算出移动距离
						int dx = x - startX;
						int dy = y - startY;
						int l = iv_drag_location.getLeft();
						int r = iv_drag_location.getRight();
						int t = iv_drag_location.getTop();
						int b = iv_drag_location.getBottom();
						
						//设置新的布局位置
						iv_drag_location.layout(l + dx, t + dy, r + dx, b + dy);
						
						//重新获取位置
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
						
					case MotionEvent.ACTION_UP : 
						int lastX = iv_drag_location.getLeft();
						int lastY = iv_drag_location.getTop();
						Editor editor = sp.edit();
						editor.putInt("lastX", lastX);
						editor.putInt("lastY", lastY);
						editor.commit();
						break;
						
					default : 
						break;
				}
				break;
				
			default : 
				break;
		}
		return true;
	}

}
