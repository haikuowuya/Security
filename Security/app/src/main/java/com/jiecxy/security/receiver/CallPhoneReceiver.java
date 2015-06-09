package com.jiecxy.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiecxy.security.ui.LostProtectedActivity;

/**
 * Created by apple on 15-1-28.
 */

public class CallPhoneReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String outPhoneNumber = this.getResultData();
        if(outPhoneNumber.equals("1314"))        //当监听到用户拨打的是1314的时候，就进行下面的操作，你可以把拨打的号码做成参数的形式，让用户可配置
        {
            Intent i = new Intent(context, LostProtectedActivity.class);

            //这个很重要，如果没有这一句，那就会报错，这一句是因为我们是在一个Receiver里面启动一个activity的，但activity的启动，都是放到一个栈里面的，
            //但Receiver里面没有那个栈，所以我们要在这里启动一个activity，那就必须要指定这行代码啦
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);
            setResultData(null);        //这行代码是把广播的数据设置为null，这样就不会把刚刚那个号码拨打出去啦，只会启动我们的activity
        }
    }

}