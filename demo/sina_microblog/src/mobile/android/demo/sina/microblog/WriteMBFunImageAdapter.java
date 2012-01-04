package mobile.android.demo.sina.microblog;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WriteMBFunImageAdapter extends BaseAdapter
{
	private LayoutInflater mLayoutInflater;
	private int mGalleryItemBackground;
	private int[] mFunImageId = new int[]
	{ R.drawable.camera, R.drawable.album,/* R.drawable.select_image,
			R.drawable.web_image,*/ R.drawable.edit_image, R.drawable.smile/*,
			R.drawable.gratulation, R.drawable.poem, R.drawable.more_word*/ };
	private int[] mFunImageId_Gray = new int[]
	{ R.drawable.camera, R.drawable.album,
			/*R.drawable.select_image_gray, R.drawable.web_image_gray,*/
			R.drawable.edit_image, R.drawable.smile/*,
			R.drawable.gratulation_gray,R.drawable.poem_gray, R.drawable.more_word_gray*/ };
	private String[] mFunText = new String[]
	{ "ÅÄÉãÕÕÆ¬", "Ñ¡ÔñÏà²á", /*"Ñ¡ÔñÍ¼Ïñ", "ÍøÂçÍ¼Ïñ",*/ "±à¼­Í¼Ïñ", "±íÇé×Ö·û"/*, "×£ºØÓÃÓï", "Ê«´Ê¸è¸³", "¸ü¶àÎÄ×Ö"*/ };

	public WriteMBFunImageAdapter(Context context)
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

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return mFunImageId[position % mFunImageId.length];
	}
    public int getIndex(int position)
    {
    	return mFunImageId[position % mFunImageId.length];
    }
	public void setImage(View view, int position, View oldView, int oldPosition)
	{
		ImageView ivFunction = (ImageView) view
				.findViewById(R.id.ivWriteMBFunItem);
		ivFunction.setImageResource(mFunImageId[position % mFunImageId.length]);
		if (oldPosition > -1 && oldView != null && oldPosition != position)
		{
			ivFunction = (ImageView) oldView
					.findViewById(R.id.ivWriteMBFunItem);
			ivFunction.setImageResource(mFunImageId_Gray[oldPosition
					% mFunImageId.length]);
		}
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = mLayoutInflater.inflate(R.layout.write_microblog_fun_item,
				null);
		ImageView ivFunction = (ImageView) view
				.findViewById(R.id.ivWriteMBFunItem);  
		TextView tvWriteMBFunItemText = (TextView) view
				.findViewById(R.id.tvWriteMBFunItemText);
		ivFunction.setImageResource(mFunImageId_Gray[position
				% mFunImageId.length]);
		tvWriteMBFunItemText.setText(mFunText[position % mFunImageId.length]);
		ivFunction.setScaleType(ImageView.ScaleType.FIT_XY);
		ivFunction.setBackgroundResource(mGalleryItemBackground);
		return view; 
	}

}
