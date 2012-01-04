package mobile.android.ch06_ucweb.menu;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public GridAdapter(Context context)
	{
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return Const.GRID_ITEM_ICON_ID_LIST.length;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
			convertView = mLayoutInflater.inflate(R.layout.grid_item, null);
		ImageView ivGridItemIcon = (ImageView) convertView
				.findViewById(R.id.ivGridItemIcon);
		TextView tvGridItemText = (TextView) convertView
				.findViewById(R.id.tvGridItemText);
		ivGridItemIcon.setImageResource(Const.GRID_ITEM_ICON_ID_LIST[position]);
		tvGridItemText.setText(Const.GRID_ITEM_TEXT_LIST[position]);
		

		return convertView;
	}

}
