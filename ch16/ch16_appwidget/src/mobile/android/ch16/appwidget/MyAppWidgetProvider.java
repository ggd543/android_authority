package mobile.android.ch16.appwidget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider
{  

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++)
		{
			int appWidgetId = appWidgetIds[i];
			String titlePrefix = MyAppWidgetConfigure.loadTitlePref(context,
					appWidgetId);
			updateAppWidget(context, appWidgetManager, appWidgetId, titlePrefix);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++)
		{
			MyAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
		}
	}


	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			String titlePrefix)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		CharSequence text = MyAppWidgetConfigure.loadTitlePref(context,
				appWidgetId) + "\n" + simpleDateFormat.format(new Date());

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.appwidget_provider);
		views.setTextViewText(R.id.appwidget_text, text);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
