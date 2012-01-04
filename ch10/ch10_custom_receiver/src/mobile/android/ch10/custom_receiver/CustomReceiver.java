package mobile.android.ch10.custom_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if ("mobile.android.ch10.MYBROADCAST".equals(intent.getAction()))
		{
			String name = intent.getStringExtra("name");
			Toast.makeText(context, name, Toast.LENGTH_LONG).show();

		}

	}

}
