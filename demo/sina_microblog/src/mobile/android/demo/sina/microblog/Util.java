package mobile.android.demo.sina.microblog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobile.android.demo.sinamb.Const;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class Util implements Const, Serializable
{
	private Context mContext;
	private SharedPreferences mSharedPreferences;

	public Util(Context context)
	{
		mContext = context;
		mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	public void showMsg(String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showMsg(Context context, String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public void showMsg(Object msg)
	{
		Toast.makeText(mContext, String.valueOf(msg), Toast.LENGTH_SHORT)
				.show();
	}

	public static void showMsg(Context context, Object msg)
	{
		Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_SHORT).show();
	}

	public static void showMsgInThread(final Context context, Object msg)
	{
		Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				showMsg(context, msg);
			}

		};
		handler.sendEmptyMessage(0);

	}

	public static void cancelProgressDialog(final ProgressDialog progressDialog)
	{
		Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				progressDialog.cancel();
			}

		};
		handler.sendEmptyMessage(0);

	}

	public void saveString(String key, String value)
	{
		mSharedPreferences.edit().putString(key, value).commit();
	}

	public void saveBoolean(String key, boolean value)
	{
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public String getString(String key, String... defValue)
	{
		if (defValue.length > 0)
			return mSharedPreferences.getString(key, defValue[0]);
		else
			return mSharedPreferences.getString(key, "");

	}

	public boolean getBoolean(String key, boolean... defValue)
	{
		if (defValue.length > 0)
			return mSharedPreferences.getBoolean(key, defValue[0]);
		else
			return mSharedPreferences.getBoolean(key, false);

	}

	public static String getLeftString(String s, String separator)
	{
		int index = s.indexOf(separator);
		if (index > -1)
			return s.substring(0, index);
		else
			return s;
	}

	public static InputStream getNetInputStream(String urlStr)
	{
		try
		{
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			return is;
		}
		catch (Exception e)
		{

		}
		return null;
	}

	public static byte[] readFileImage(InputStream is) throws IOException
	{

		int len = is.available();
		byte[] bytes = new byte[len];
		int r = is.read(bytes);
		if (len != r)
		{
			bytes = null;
			throw new IOException("读取文件不正确");
		}
		is.close();
		return bytes;
	}

	public static byte[] bitmapToByteArray(Bitmap bitmap)
	{

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 80, baos);
		return baos.toByteArray();
	}

	public static String getStoragePath()
	{
		String path = android.os.Environment.getDataDirectory()
				.getAbsolutePath()
				+ "/"
				+ MicroBlog.class.getPackage().getName()
				+ "/storage/";
		File file = new File(path);
		if (!file.exists())
		{
			file.mkdir();
		}
		return path;

	}

	public static ProgressDialog showSpinnerProgressDialog(Context context,
			String msg)
	{
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(msg);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
		return progressDialog;
	}

	public ProgressDialog showSpinnerProgressDialog(String msg)
	{

		return showSpinnerProgressDialog(mContext, msg);
	}

	public static int getRemainWordCount(String s)
	{

		// s = s.replaceAll("[^\\x00-\\xff]", "**");
		int length = s.length();

		return 140 - length;
	}

	public static String getStringFromFile(String filename)
	{
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			byte[] buffer = new byte[8192];
			int n = fis.read(buffer);
			fis.close();
			
			return new String(buffer, 0, n, "utf-8");
		}
		catch (Exception e)
		{
			return "";
		}
	}
    public static String fillZero(int n, int length)
    {
    	String s = String.valueOf(n);
    	int fillNumber = length - s.length();
    	for(int i=0; i < fillNumber; i++)
    	{
    		s = "0" + s;
    	}
    	return s;
    }
    //  用于decodeFile方法返回一个较小的Bitmap对象
    public static int getSampleSize(String path)
    {
    	File file = new File(path);
    	if(file.exists())
    	{
    		return (int)(file.length() / (200 * 1024));
    	}
    	return 0;
    }
    public static String getTimeStr(Date date)
    {
    	Date currentDate = new Date();
    	long time1 = currentDate.getTime();
		
		long time2 = date.getTime();

		long time = (time1 - time2) / 1000;
		String FormattedCreatedAt = "";
		if (time >= 0 && time < 60)
		{
			return  "刚才";
		}
		else if (time >= 60 && time < 3600)
		{
			return time / 60 + "分钟前";
		}
		else if (time >= 3600 && time < 3600 * 24)
		{
			return time / 3600 + "小时前";  
		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");
			return sdf.format(date);
		}
    }
}
