package mobile.android.ch12.call.aidl;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class InCallReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Service.TELEPHONY_SERVICE);
		switch (tm.getCallState())
		{
			case TelephonyManager.CALL_STATE_RINGING: // 响铃
				// 获得来电的电话号
				String incomingNumber = intent
						.getStringExtra("incoming_number");
				if ("12345678".equals(incomingNumber))
				{
					try
					{
						
						
						TelephonyManager telephonyManager = (TelephonyManager) context
								.getSystemService(Service.TELEPHONY_SERVICE);
						Class<TelephonyManager> telephonyManagerClass = TelephonyManager.class;

						Method telephonyMethod = telephonyManagerClass
								.getDeclaredMethod("getITelephony",
										(Class[]) null);
						telephonyMethod.setAccessible(true);
						ITelephony telephony = (com.android.internal.telephony.ITelephony) telephonyMethod
								.invoke(telephonyManager, (Object[]) null);

						
						telephony.endCall();

					}
					catch (Exception e)
					{
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
				break;

		}

	}

}
