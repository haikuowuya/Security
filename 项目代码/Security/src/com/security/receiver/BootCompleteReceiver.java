package com.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver
{
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isProtected = sp.getBoolean("isProtected", false);
		if (isProtected)
		{
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String currentSim = telephonyManager.getSimSerialNumber();
			String protectedSim = sp.getString("simSerial", "");
			System.out.println("Sim卡已经变更了，手机可能被盗");
			if (!currentSim.equals(protectedSim))
			{
				SmsManager smsManager = SmsManager.getDefault();
				String number = sp.getString("number", "");
				smsManager.sendTextMessage(number, null, "Sim卡已经变更了，手机可能被盗",
						null, null);
			}
		}
	}

}
