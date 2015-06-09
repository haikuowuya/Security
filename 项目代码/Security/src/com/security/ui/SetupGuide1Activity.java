package com.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.security.R;

public class SetupGuide1Activity extends Activity implements OnClickListener
{
	private Button next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_guide1);
		
		next = (Button) findViewById(R.id.bt_guide_next);
		next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.bt_guide_next : 
				Intent intent = new Intent(this, SetupGuide2Activity.class);
				finish();
				startActivity(intent);
				//这个是定义activity切换时的动画效果的
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				break;
				
			default : 
				break;
		}
	}

}
