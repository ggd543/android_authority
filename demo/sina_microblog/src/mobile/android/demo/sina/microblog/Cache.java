package mobile.android.demo.sina.microblog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weibo4j.Status;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Cache extends Database
{

	private String mPath;

	public Cache(Context context)
	{ 

		mContext = context;
		mPath = android.os.Environment.getDataDirectory().getAbsolutePath()
				+ "/data/" + MicroBlog.class.getPackage().getName()
				+ "/sina_micro_blog_cache/";

		File file = new File(mPath);
		if (!file.exists())
			file.mkdirs();

		mDatabase = SQLiteDatabase.openOrCreateDatabase(mPath + "cache", null);
		if (!tableExists("t_image"))
		{
			mDatabase.execSQL(getSQLFromRaw(R.raw.create_t_image));
		}
	}

	private void saveCacheData(String name,List<Status> statusList) throws Exception
	{
		List<Status> myStatusList = new ArrayList<Status>();
		int n = (statusList.size() >=20)?20:statusList.size();
		for(int i = 0; i < n; i++)
		{
			myStatusList.add(statusList.get(i));
		}
		
		
		FileOutputStream fos = new FileOutputStream(mPath + name);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(myStatusList);
		fos.close();
  
	} 

	public void clearMicroBlogData()
	{ 
		File file = new File(mPath + "status");
		if (file.exists())
			file.delete();
		file = new File(mPath + "bitmap");
		if (file.exists())
			file.delete();
	}

	public void saveMicroBlogData(String status, Bitmap bitmap)

	{
		try
		{
			clearMicroBlogData();
			FileOutputStream fos = null;
			if (!"".equals(status) && status != null) 
			{
				fos = new FileOutputStream(mPath + "status");
				byte[] buffer = status.getBytes("utf-8");
				fos.write(buffer);
				fos.close(); 
			}
			if (bitmap != null)   
			{
				fos = new FileOutputStream(mPath + "bitmap");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				bitmap.compress(CompressFormat.JPEG, 100, baos);
				fos.write(baos.toByteArray());
				baos.close();
				fos.close();
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	public String getStatus()
	{
		File file = new File(mPath + "status");
		if (file.exists())
		{
			return Util.getStringFromFile(mPath + "status");
		}
		return "";
	}

	public Bitmap getBitmap()
	{
		File file = new File(mPath + "bitmap");
		if (file.exists())
		{
			return BitmapFactory.decodeFile(mPath + "bitmap");
		}
		return null;
	}

	private void saveProfileImageToDB(Map<Integer, Bitmap> bitmapMap,
			int imageType, boolean delete)
	{
		if (delete)
			deleteImageForHomeTimeline();
		Set<Integer> keys = bitmapMap.keySet();
		for (Integer key : keys)
		{
			insertImage(key, bitmapMap.get(key), imageType);
		}
	}

	private void saveProfileImageToDB(Map<Integer, Bitmap> bitmapMap,
			int imageType)
	{
		saveProfileImageToDB(bitmapMap, imageType, true);
	}

	public Map<Integer, Bitmap> getProfileImageMap(int imageType)
	{
		Map<Integer, Bitmap> bitmapMap = new HashMap<Integer, Bitmap>();
		String sql = getSQLFromRaw(R.raw.select_image);

		Cursor cursor = mDatabase.rawQuery(sql, new String[]
		{ String.valueOf(imageType) });
		while (cursor.moveToNext())
		{
			byte[] buffer = cursor.getBlob(cursor.getColumnIndex("image"));
			Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0,
					buffer.length);
			bitmapMap.put(cursor.getInt(cursor
					.getColumnIndex("image_url_hashcode")), bitmap);
		}
		return bitmapMap;
	}

	private Object getCacheData(String name)
	{

		try
		{

			if (new File(mPath + name).exists())
			{

				FileInputStream fis = new FileInputStream(mPath + name);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object obj = ois.readObject();
				fis.close();
				return obj;
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			Util.showMsg(mContext, e.getMessage());
			return null;
		}
	}

	public void saveHomeTimeline(List<Status> statusList) throws Exception
	{
		saveCacheData("hometimeline", statusList);
		Log.d("------ddfdf-a", "-------a");
	}

	public void savePublicTimeline(List<Status> statusList) throws Exception
	{
		saveCacheData("publictimeline", statusList);
	}

	public void saveHomeTimelineImage(Map<Integer, Bitmap> bitmapMap,
			boolean delete) throws Exception
	{ 
		saveProfileImageToDB(bitmapMap, 1, delete);
	}
	public void savePublicTimelineImage(Map<Integer, Bitmap> bitmapMap,
			boolean delete) throws Exception
	{ 
		saveProfileImageToDB(bitmapMap, 2, delete);
	}
	public void saveHomeTimelineImage(Map<Integer, Bitmap> bitmapMap)
			throws Exception
	{
		saveProfileImageToDB(bitmapMap, 1);
	}

	public Map<Integer, Bitmap> getHomeTimelineImageMap()
	{
		return getProfileImageMap(1);
	}

	public Map<Integer, Bitmap> getPublicTimelineImageMap()
	{
		return getProfileImageMap(2);
	}

	public Object getHomeTimeline()
	{
		return getCacheData("hometimeline");
	}
	public Object getPublicTimeline()
	{
		return getCacheData("publictimeline");
	}
	public void clear()
	{
		mDatabase.close();
		File file = new File(mPath + "cache");
		file.delete();
		file = new File(mPath + "hometimeline");
		file.delete();
		file = new File(mPath + "publictimeline");
		file.delete();
	}
}
