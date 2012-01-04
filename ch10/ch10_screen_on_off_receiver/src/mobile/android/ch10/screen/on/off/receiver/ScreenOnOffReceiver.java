package mobile.android.ch10.screen.on.off.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnOffReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
		{
			Log.d("screen", "ok");
			

		}
		else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()))
		{
			Log.d("screen", "off");
		}
	}

}
