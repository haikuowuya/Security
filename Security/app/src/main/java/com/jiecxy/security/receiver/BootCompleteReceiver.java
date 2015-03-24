package com.jiecxy.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.jiecxy.security.ui.SetupGuide4.SetupGuide4Activity;

/**
 * Created by apple on 15/2/9.
 */

public class BootCompleteReceiver extends BroadcastReceiver
{
    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        /**
         * Context.MODE_PRIVATE：指定该SharedPreferences数据只能被本应用程序读、写
         * Context.MODE_WORLD_READABLE：指定该SharedPreferences数据能被其他应用程序读，但不能写
         * Context.MODE_WORLD_WRITEABLE：指定该SharedPreferences数据能被其他应用程序读写
         */
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean isProtected = sp.getBoolean("isProtected", false);   // 如果preference存在，则返回preference的值，否则返回defValue，即false
        //看看是不是开启了保护
        if(isProtected)
        {
            //如果我们的sim卡被人换掉了，那么就会发送一条短信到我们的原来设定的安全号码那里的
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //开机后，拿到当前sim卡的标识，与我们之前存放的标识对比
            String currentSim = telephonyManager.getSimSerialNumber();
            String protectedSim = sp.getString("simSerial", "");

            if (!currentSim.equals(protectedSim) && !currentSim.equals(""))
            {
                //拿到一个短信的管理器，要注意不要导错包，是在android.telephony下的
                SmsManager smsManager = SmsManager.getDefault();
                String number = sp.getString("number", "");
                //发送短信，有5个参数，第一个是要发送到的地址，第二个是发送人，可以设置为null，第三个是要发送的信息，第四个是发送状态，第五个是发送后的，都可以置为null
                smsManager.sendTextMessage(number, null, "Sim卡已经变更了，手机可能被盗", null, null);
            }
        }
    }
}