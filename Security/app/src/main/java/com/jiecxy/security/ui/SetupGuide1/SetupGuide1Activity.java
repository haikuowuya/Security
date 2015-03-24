package com.jiecxy.security.ui.SetupGuide1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jiecxy.security.R;
import com.jiecxy.security.ui.SetupGuide2.SetupGuide2Activity;

/**
 * Created by apple on 15-1-29.
 */

public class SetupGuide1Activity extends Activity implements View.OnClickListener
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