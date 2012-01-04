package mobile.android.demo.sina.microblog;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

import mobile.android.demo.sinamb.Const;
import weibo4j.Status;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;


public class Database implements Const
{
	protected Context mContext;
	protected SQLiteDatabase mDatabase;
	private String mDBFilename;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public Database()
	{

	}

	public Database(Context context)
	{
		mContext = context;
		String dbPath = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/" + DATABASE_DEFAULT_DIR;
		File file = new File(dbPath);
		if (!file.exists())
		{
			file.mkdirs();
		}
		mDBFilename = dbPath + "/" + DATABASE_DEFAULT_FILENAME;
		mDatabase = SQLiteDatabase.openOrCreateDatabase(mDBFilename, null);

		if (!tableExists("t_image"))
		{
			mDatabase.execSQL(getSQLFromRaw(R.raw.create_t_image));
		}

		if (!tableExists("t_hometimeline"))
		{
			mDatabase.execSQL(getSQLFromRaw(R.raw.create_t_hometimeline));
		}

	}

	public boolean tableExists(String tableName)
	{
		Cursor cursor = mDatabase.rawQuery(
				"select count(*) from sqlite_master where name=?", new String[]
				{ tableName });
		cursor.moveToFirst();
		if (cursor.getInt(0) > 0)
			return true;
		else
			return false;

	}

	public String getSQLFromRaw(int resourceId)
	{
		StringBuilder sbSQL = new StringBuilder();
		InputStream is = mContext.getResources().openRawResource(resourceId);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String s = "";
		try
		{
			while ((s = br.readLine()) != null)
			{
				sbSQL.append(s);
			}
			is.close();
		}
		catch (Exception e)
		{

		}
		return sbSQL.toString();
	}

	private void insertStatusList(List<Status> statusList, String timelineName)
	{
		String sql = String.format(getSQLFromRaw(R.raw.insert_timeline),
				timelineName);

		mDatabase.execSQL("delete from " + timelineName + ";");
		for (Status status : statusList)
		{
			mDatabase.execSQL(sql,
					new Object[]
					{
							status.getId(),
							status.getUser().getName(),
							mSimpleDateFormat.format(status.getCreatedAt()),
							status.getText(),
							status.getUser().getProfileImageURL().toString()
									.hashCode() });
		}
	}

	public void deleteImageForHomeTimeline()
	{
		mDatabase.execSQL("delete from t_image where image_type=1");

	}

	public void deleteImageForPublicTimeline()
	{
		mDatabase.execSQL("delete from t_image where image_type=2");

	}

	protected void insertImage(int urlHashcode, Bitmap bitmap, int imageType)
	{
		try
		{
			String sql = getSQLFromRaw(R.raw.insert_image);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 80, baos);
			byte[] buffer = baos.toByteArray();
			mDatabase.execSQL(sql, new Object[]
			{ urlHashcode, buffer, imageType });
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	public void insertImageForHomeTimeline(int urlHashcode, Bitmap bitmap)
	{
		// 1表示HomeTimeline
		insertImage(urlHashcode, bitmap, 1);
	}

	public void insertImageForPublicTimeline(int urlHashcode, Bitmap bitmap)
	{
		// 2表示PublicTimeline
		insertImage(urlHashcode, bitmap, 2);
	}

	public void insertStatusListForHomeTimeline(List<Status> statusList)
	{
		insertStatusList(statusList, "t_hometimeline");
	}

	public void insertStatusListForPublicTimeline(List<Status> statusList)
	{
		insertStatusList(statusList, "t_publictimeline");
	}
}
