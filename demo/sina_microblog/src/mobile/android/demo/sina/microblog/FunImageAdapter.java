package mobile.android.demo.sina.microblog;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FunImageAdapter extends BaseAdapter
{
	private LayoutInflater mLayoutInflater;
	private int mGalleryItemBackground;
	private int[] mFunImageId = new int[]
	{ R.drawable.home, R.drawable.stroll, R.drawable.me, R.drawable.refresh, };

	private String[] mFunText = new String[]
	{ "首页", "逛逛", "我的", "刷新", };

	public FunImageAdapter(Context context)
	{
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TypedArray typedArray = context
				.obtainStyledAttributes(R.styleable.Gallery);

		mGalleryItemBackground = typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);

	}

	@Override
	public int getCount()
	{
		return Integer.MAX_VALUE;
	}

	public int getFunCount()
	{
		return mFunImageId.length;
	}

	public int getIndex(int position)
	{
		return mFunImageId[position % mFunImageId.length];
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View view = mLayoutInflater.inflate(R.layout.fun_item, null);
		ImageView ivFunction = (ImageView) view.findViewById(R.id.ivFunction);
		TextView tvFunItemText = (TextView) view
				.findViewById(R.id.tvFunItemText);
		ivFunction.setImageResource(mFunImageId[position % mFunImageId.length]);
		tvFunItemText.setText(mFunText[position % mFunImageId.length]);
		ivFunction.setScaleType(ImageView.ScaleType.FIT_XY);
		ivFunction.setBackgroundResource(mGalleryItemBackground);
		return view;

	}

}
