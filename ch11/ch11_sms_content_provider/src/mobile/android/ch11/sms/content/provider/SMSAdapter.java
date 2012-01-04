package mobile.android.ch11.sms.content.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SMSAdapter extends CursorAdapter
{
	private LayoutInflater layoutInflater;

	public SMSAdapter(Context context, Cursor cursor)
	{
		super(context, cursor);

		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		View view = layoutInflater.inflate(R.layout.item, null);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		TextView tvPhoneNumber = (TextView) view
				.findViewById(R.id.tvPhoneNumber);
		TextView tvContent = (TextView) view.findViewById(R.id.tvContent);

		tvPhoneNumber
				.setText(cursor.getString(cursor.getColumnIndex("address")));
		tvContent.setText(cursor.getString(cursor.getColumnIndex("body")));
	}

}
