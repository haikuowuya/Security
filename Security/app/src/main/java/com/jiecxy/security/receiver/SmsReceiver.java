package com.jiecxy.security.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.jiecxy.security.R;
import com.jiecxy.security.engine.GPSInfoProvider;

/**
 *  拦截到我们发的短信，那么就进行指令的分析还有相应的处理
 * Created by apple on 15/2/20.
 */

public class SmsReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for(Object pdu : pdus)
        {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            //拿到短信内容
            String content = smsMessage.getMessageBody();
            //拿到发送人的电话号码
            String sender  = smsMessage.getOriginatingAddress();

            //这个是通过短信发送指令，然后进行一些操作的
            if(content.equals("#*location*#"))
            {
                abortBroadcast();//终止广播，免得让小偷看到短信

                GPSInfoProvider gpsInfoProvider = GPSInfoProvider.getInstance(context);
                String location = gpsInfoProvider.getLocation();

                System.out.println(location);

                if(!location.equals(""))
                {
                    //发送短信
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, location, null, null);
                }
            }
            else if(content.equals("#*lockscreen*#"))
            {
                DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                //重新设置密码，第一个参数就是密码，第二个参数暂时没有用，但要设置为0
                manager.resetPassword("123", 0);
                //进行锁屏
                manager.lockNow();
                abortBroadcast();
            }
            else if(content.equals("#*wipe*#"))
            {
                DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                //恢复到出厂模式，参数暂时没有用，但要置为0
                manager.wipeData(0);
                abortBroadcast();
            }
            else if(content.equals("#*alarm*#"))
            {
                //这个方法已经调用的prepare这个方法的啦，所以不用自己调用prepare这个方法
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bj);
                //设置声音为最大声
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();
                abortBroadcast();
            }
        }
    }

}