package com.jiecxy.security.ui.SetupGuide2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jiecxy.security.R;
import com.jiecxy.security.ui.SetupGuide1.SetupGuide1Activity;
import com.jiecxy.security.ui.SetupGuide3.SetupGuide3Activity;

/**
 * Created by apple on 15-1-29.
 */

public class SetupGuide2Activity extends Activity implements View.OnClickListener
{
    private Button bt_bind;
    private Button bt_next;
    private Button bt_pervious;
    private CheckBox cb_bind;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_guide2);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);

        bt_bind = (Button) findViewById(R.id.bt_guide_bind);
        bt_next = (Button) findViewById(R.id.bt_guide_next);
        bt_pervious = (Button) findViewById(R.id.bt_guide_pervious);
        bt_bind.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_pervious.setOnClickListener(this);

        cb_bind = (CheckBox) findViewById(R.id.cb_guide_check);
        //初始化CheckBox状态
        String sim = sp.getString("simSerial", null);
        if(sim != null)
        {
            cb_bind.setText("已经绑定");
            cb_bind.setChecked(true);
        }
        else
        {
            cb_bind.setText("没有绑定");
            cb_bind.setChecked(false);
            resetSimInfo();
        }
        cb_bind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //那个解除绑定的我还没做的呢，各位可以自己去完成，就是把那个SharedPreferences里面的值设置一下就行啦
                if(isChecked)
                {
                    cb_bind.setText("已经绑定");
                    setSimInfo();
                }
                else
                {
                    cb_bind.setText("没有绑定");
                    resetSimInfo();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.bt_guide_bind :
                setSimInfo();
                cb_bind.setText("已经绑定");
                cb_bind.setChecked(true);
                break;

            case R.id.bt_guide_next :
                Intent intent = new Intent(this, SetupGuide3Activity.class);
                finish();
                startActivity(intent);
                //这个是定义activity切换时的动画效果的
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            case R.id.bt_guide_pervious :

                Intent i = new Intent(this, SetupGuide1Activity.class);
                finish();
                startActivity(i);
                //这个是定义activity切换时的动画效果的
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            default :
                break;
        }
    }

    private void setSimInfo()
    {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simSerial = telephonyManager.getSimSerialNumber();//拿到sim卡的序列号，是唯一的
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("simSerial", simSerial);
        editor.commit();
    }

    private void resetSimInfo()        //解除绑定
    {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("simSerial", null);
        editor.commit();
    }
}