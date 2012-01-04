package mobile.android.demo.sina.microblog;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.android.demo.sina.microblog.interfaces.OnLoadBitmapFromNetListener;
import weibo4j.Count;
import weibo4j.Status;
import weibo4j.Weibo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MicroBlogAdapter extends BaseAdapter
{
	private LayoutInflater mLayoutInflater;
	private List<Status> mStatusList;
	private Map<Integer, Bitmap> mImageMap = new HashMap<Integer, Bitmap>();
	private Map<Long, View> mViewMap = new HashMap<Long, View>();
	private Map<Long, Integer> mIdHashcodeMap = new HashMap<Long, Integer>();
	public static Map<Integer, Bitmap> mBigPictureMap = new HashMap<Integer, Bitmap>();
	private Weibo mTwitter;
	private Cache mCache;
	private Context mContext;
	private boolean mPaging;
	private Handler mHandler = new Handler();

	public MicroBlogAdapter(Context context, List<Status> statusList,
			Weibo twitter, Cache cache)
	{
		
		this(context, statusList, twitter, cache, true);
	}

	public MicroBlogAdapter(Context context, List<Status> statusList,
			Weibo twitter, Cache cache, boolean paging)
	{
		mCache = cache;
		mStatusList = statusList;
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTwitter = twitter;
		mPaging = paging;
	}

	@Override
	public int getCount()
	{
		if (mPaging)
			return mStatusList.size() + 1;
		else
			return mStatusList.size();

	}

	@Override
	public Object getItem(int position)
	{
		if (position < mStatusList.size())
			return mStatusList.get(position);
		else
			return null;
	}

	public boolean havePicture(int position)
	{
		if (position < mStatusList.size())
			return ("".equals(mStatusList.get(position).getThumbnailPic()) ? false
					: true);
		else
			return false;
	}

	public int getStatusCount()
	{
		return mStatusList.size();
	}

	@Override
	public long getItemId(int position)
	{
		if (position < mStatusList.size())
			return mStatusList.get(position).getId();
		else
			return 0;
	}

	public long getMinStatusId()
	{

		if (mStatusList.size() > 0)
			return mStatusList.get(mStatusList.size() - 1).getId();
		else
			return 0;
	}

	public long getMaxStatusId()
	{

		if (mStatusList.size() > 0)
			return mStatusList.get(0).getId();
		else
			return Long.MAX_VALUE;
	}

	public String getText(int position)
	{
		if (position < mStatusList.size())
			return mStatusList.get(position).getText();
		else
			return "";
	}

	public String getStatusUserName(int position)
	{
		if (position < mStatusList.size())
			return mStatusList.get(position).getUser().getName();
		else
			return "";
	}

	public boolean haveRetweetDetails(int position)
	{
		if (position < mStatusList.size())
			return (mStatusList.get(position).getRetweetDetails() == null) ? false
					: true;
		else
			return false;
	}

	public List<Status> addMoreMicroBlog(List<Status> statusList, boolean append)
	{
		if (append)
			mStatusList.addAll(statusList);
		else
			mStatusList.addAll(0, statusList);
		notifyDataSetChanged();
		return mStatusList;
	}

	public void setImage(View view, int position)
	{
		Status status = mStatusList.get(position);
		ImageView ivMicroBlogImage = (ImageView) view
				.findViewById(R.id.ivMicroBlogImage);

		if (!"".equals(status.getThumbnailPic()))
		{

			ivMicroBlogImage.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
			loadBitmapFromNet(status.getId(), status.getThumbnailPic(),
					ivMicroBlogImage);
		}

	}

	public void collapse(View view, int position)
	{
		TextView tvCount = (TextView) view.findViewById(R.id.tvCount);
		TextView tvText = (TextView) view.findViewById(R.id.tvText);
		ImageView ivMicroBlogImage = (ImageView) view
				.findViewById(R.id.ivMicroBlogImage);
		TextView tvRepostText = (TextView) view.findViewById(R.id.tvRepostText);
		tvCount.getLayoutParams().height = 0;
		tvText.setLines(2);
		ivMicroBlogImage.getLayoutParams().height = 0;
		tvRepostText.getLayoutParams().height = 0;

	}

	public void expand(View view, int position)
	{
		TextView tvCount = (TextView) view.findViewById(R.id.tvCount);
		TextView tvText = (TextView) view.findViewById(R.id.tvText);
		ImageView ivMicroBlogImage = (ImageView) view
				.findViewById(R.id.ivMicroBlogImage);
		TextView tvRepostText = (TextView) view.findViewById(R.id.tvRepostText);

		tvCount.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
		tvText.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
		tvText.setMaxLines(100);
		ivMicroBlogImage.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
		;
		tvRepostText.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
		;
	}

	public boolean isExpanded(View view, int position)
	{
		TextView tvCount = (TextView) view.findViewById(R.id.tvCount);
		if (tvCount.getLayoutParams().height == 0)
			return false;
		else
		{
			return true;
		}
	}

	public void clearCount(int position)
	{
		mStatusList.get(position).setCount(null);
	}

	public void setCount(View view, int position)
	{
		Status status = mStatusList.get(position);

		if (status.getCount() == null)
		{
			try
			{

				Count count = mTwitter.getCount(status.getId());

				if (count != null)
				{
					status.setCount(count);
					TextView tvCount = (TextView) view
							.findViewById(R.id.tvCount);

					tvCount.setText("转发：" + status.getCount().getRepostCount()
							+ "  评论：" + status.getCount().getCommentCount());
					Log.d("a1", String.valueOf(status.getCount()
							.getCommentCount()));
					tvCount.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
					mIdHashcodeMap.put(status.getId(), 0);
				}

			}
			catch (Exception e)
			{
				Log.d("error", e.getMessage());
			}

		}

	}

	public void setText(View view, int position)
	{
		Status status = mStatusList.get(position);

		try
		{
			TextView tvText = (TextView) view.findViewById(R.id.tvText);
			tvText.setLines(1);
			tvText.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
			tvText.setMaxLines(100);

			tvText.setText(Html.fromHtml(status.getText()));
			if (status.getRetweetDetails() != null)
			{

				TextView tvRepostText = (TextView) view
						.findViewById(R.id.tvRepostText);
				tvRepostText.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				tvRepostText.setText(Html.fromHtml(" <font color='#99CC33'>@"
						+ status.getRetweetDetails().getRetweetingUser()
								.getName() + ":</font>"
						+ status.getRetweetDetails().getText()));
				mIdHashcodeMap.put(status.getId(), 0);
			}
			Log.d("aaa", tvText.getText().toString());

		}
		catch (Exception e)
		{

		}

	}

	// 此方法将围脖的头像保存在数据库中
	private void downloadCacheProfileImage(List<Status> statusList)
			throws Exception
	{
		for (Status status : statusList)
		{
			URL url = status.getUser().getProfileImageURL();

			if (url != null && mImageMap.get(url.toString().hashCode()) == null)
			{
				Bitmap bitmap = loadBitmapFromNet(status.getId(), url
						.toString(), null);
				mImageMap.put(url.toString().hashCode(), bitmap);

			}
		}

	}

	private void downloadCacheProfileImage() throws Exception
	{
		downloadCacheProfileImage(mStatusList);
	}

	public List<Status> getTopStatuses()
	{
		int n = mStatusList.size();
		if (n > 20)
			n = 20;
		List<Status> statuses = new ArrayList<Status>();
		for (int i = 0; i < n; i++)
		{
			statuses.add(mStatusList.get(i));
		}
		return statuses;
	}

	public void loadCacheProfileImageForHomeTimeline()
	{
		mImageMap = mCache.getProfileImageMap(1);

	}

	public void loadCacheProfileImageForPublicTimeline()
	{
		mImageMap = mCache.getProfileImageMap(2);

	}

	public void downloadCacheProfileImageForHomeTimeline() throws Exception
	{
		downloadCacheProfileImage();
		mCache.saveHomeTimelineImage(mImageMap);
	}

	public void downloadCacheProfileImageForHomeTimeline(
			List<Status> statusList, boolean delete) throws Exception
	{
		downloadCacheProfileImage(statusList);
		mCache.saveHomeTimelineImage(mImageMap, delete);
	}

	public void downloadCacheProfileImageForHomeTimeline(List<Status> statusList)
			throws Exception
	{
		downloadCacheProfileImageForHomeTimeline(statusList, true);
	}

	public void downloadCacheProfileImageForPublicTimeline() throws Exception
	{
		downloadCacheProfileImage();
	}

	public void downloadCacheProfileImageForPublicTimeline(
			List<Status> statusList) throws Exception
	{

		downloadCacheProfileImageForPublicTimeline(statusList, true);

	}

	public void downloadCacheProfileImageForPublicTimeline(
			List<Status> statusList, boolean delete) throws Exception
	{
		downloadCacheProfileImage(statusList);
		mCache.savePublicTimelineImage(mImageMap, delete);
	}

	private Bitmap loadBitmapFromNet(long id, String url, ImageView imageView)
	{
		Bitmap bitmap = null;
		try
		{

			int hashcode = url.toString().hashCode();
			bitmap = mImageMap.get(hashcode);
			if (bitmap == null)
			{

				InputStream is = Util.getNetInputStream(url.toString());
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
				mImageMap.put(hashcode, bitmap);
				mIdHashcodeMap.put(id, hashcode);
			}
			if (imageView != null)
				imageView.setImageBitmap(bitmap);
		}
		catch (Exception e)
		{
			// Util.showMsg(mContext, "下载失败");
		}
		return bitmap;
	}

	public void getBigPictureAsync(final int position,
			final OnLoadBitmapFromNetListener listener)
	{
		if (position < mStatusList.size())
		{
			final String url = mStatusList.get(position).getBmiddlePic();
			Thread thread = new Thread(new Runnable()
			{

				@Override
				public void run()
				{

					try
					{
						Bitmap bitmap = null;
						int hashcode = url.toString().hashCode();
						bitmap = mBigPictureMap.get(hashcode);
						if (bitmap == null)
						{

							InputStream is = Util.getNetInputStream(url
									.toString());
							bitmap = BitmapFactory.decodeStream(is);
							is.close();
							mBigPictureMap.put(hashcode, bitmap);

						}

						if (listener != null)
							listener.onObtainBitmap(bitmap, null);
					}
					catch (Exception e)
					{
						mHandler.post(new Runnable()
						{

							@Override
							public void run()
							{

								Util.showMsg(mContext, "大图装载失败");
							}
						});

					}

				}
			});
			thread.start();
		}

	}

	private void loadBitmapFromNetAsync(final long id, final String url,
			final ImageView imageView,
			final OnLoadBitmapFromNetListener listener)
	{

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				try
				{
					Bitmap bitmap;
					int hashcode = url.toString().hashCode();
					bitmap = mImageMap.get(hashcode);

					if (bitmap == null)
					{

						InputStream is = Util.getNetInputStream(url.toString());
						bitmap = BitmapFactory.decodeStream(is);
						is.close();
						mImageMap.put(hashcode, bitmap);
						mIdHashcodeMap.put(id, hashcode);

					}
					if (listener != null)
						listener.onObtainBitmap(bitmap, imageView);
					final Bitmap bitmap1 = bitmap;
					mHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							if (bitmap1 == null)
							{
								imageView.setImageResource(R.drawable.nophoto);
								// Util.showMsg(mContext, "imageview=null");
							}
							else if (listener != null)
							{
								listener.onObtainBitmap(bitmap1, imageView);
							}
						}
					});

				}
				catch (Exception e)
				{
					mHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							imageView.setImageResource(R.drawable.nophoto);
							Util.showMsg(mContext, "下载失败");
						}
					});

				}

			}
		});
		thread.start();

	}
	
	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{

		if (position < mStatusList.size())
		{
			final Status status = mStatusList.get(position);
		
			View myView = mViewMap.get(status.getId());
			if (myView != null)
			{
				TextView tvCreatedAt = (TextView) myView
						.findViewById(R.id.tvCreatedAt);
				String picture = "";
				if (!"".equals(status.getBmiddlePic()))
					picture = "（图）";
				tvCreatedAt.setText(Html.fromHtml("<font color='#11DDFF'>"
						+ Util.getTimeStr(status.getCreatedAt()) + "</font>" + picture));
				return myView;
			}
			View microblogView = mLayoutInflater.inflate(
					R.layout.microblog_item, null);

			final ImageView ivPhoto = (ImageView) microblogView
					.findViewById(R.id.ivPhoto);
			ivPhoto.setImageResource(R.drawable.nophoto);
			TextView tvName = (TextView) microblogView
					.findViewById(R.id.tvName);

			TextView tvCreatedAt = (TextView) microblogView
					.findViewById(R.id.tvCreatedAt);
			TextView tvText = (TextView) microblogView
					.findViewById(R.id.tvText);

			tvName.setText(Html.fromHtml("<font color='#FF0000'>"
					+ status.getUser().getName() + "</font>"));
			if (status.getUser().getProfileImageURL() != null)
			{
				final URL url = status.getUser().getProfileImageURL();

				
				loadBitmapFromNetAsync(status.getId(), url.toString(), ivPhoto,
						new OnLoadBitmapFromNetListener()
						{

							@Override
							public void onObtainBitmap(final Bitmap bitmap,
									final ImageView imageView)
							{
								mHandler.post(new Runnable()
								{

									@Override
									public void run()
									{
										imageView.setImageBitmap(bitmap);

									}
								});

							}
						});
	
			}
			String picture = "";
			if (!"".equals(status.getBmiddlePic()))
				picture = "（图）";

			tvCreatedAt.setText(Html.fromHtml("<font color='#11DDFF'>"
					+ Util.getTimeStr(status.getCreatedAt()) + "</font>" + picture));

			tvText.setText(status.getText());

			Integer hashcode = mIdHashcodeMap.get(status.getId());
			if (hashcode != null)
			{
				if (status.getRetweetDetails() != null)
				{
					tvText.setText(Html.fromHtml(tvText.getText()
							+ " <font color='#0000FF'>@"
							+ status.getRetweetDetails().getRetweetingUser()
									.getName() + ":</font>"
							+ status.getRetweetDetails().getText()));
				}

				if (!"".equals(status.getThumbnailPic()))
				{
					Bitmap bitmap = mImageMap.get(status.getThumbnailPic()
							.hashCode());
					if (bitmap != null)
					{
						ImageView ivMicroBlogImage = (ImageView) microblogView
								.findViewById(R.id.ivMicroBlogImage);

						ivMicroBlogImage.setImageBitmap(bitmap);
						ivMicroBlogImage.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;

					}

				}
				if (status.getCount() != null)
				{
					TextView tvCount = (TextView) microblogView
							.findViewById(R.id.tvCount);
					tvCount.setText("转发：" + status.getCount().getRepostCount()
							+ "  评论：" + status.getCount().getCommentCount());
					tvCount.getLayoutParams().height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;

					setText(microblogView, position);
				}
			}
			mViewMap.put(status.getId(), microblogView);
			return microblogView;
		}
		else
		{
			View moreMicroblog = mLayoutInflater.inflate(
					R.layout.moremicroblog, null);

			return moreMicroblog;
		}
	}

}
