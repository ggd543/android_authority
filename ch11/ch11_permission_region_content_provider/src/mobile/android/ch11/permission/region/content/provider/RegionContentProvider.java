package mobile.android.ch11.permission.region.content.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.R;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class RegionContentProvider extends ContentProvider
{
	private static UriMatcher uriMatcher;
	private static final String AUTHORITY = "mobile.android.ch11.permission.regioncontentprovider";
	private static final int CITIES = 1;
	private static final int CITY_CODE = 2;
	private static final int CITY_NAME = 3;
	private static final int CITIES_IN_PROVINCE = 4;
	private SQLiteDatabase database;

	static
	{

		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "cities", CITIES);
		uriMatcher.addURI(AUTHORITY, "code/#", CITY_CODE);
		uriMatcher.addURI(AUTHORITY, "name/*", CITY_NAME);
		uriMatcher
				.addURI(AUTHORITY, "cities_in_province/*", CITIES_IN_PROVINCE);

	}

	private SQLiteDatabase openDatabase() 
	{
		try
		{
			String databaseFilename = "/sdcard/region.db";
			if (!(new File(databaseFilename)).exists())
			{
				InputStream is = getContext().getResources().getAssets()
						.open("region.db");
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}
  
				fos.close();    
				is.close();
			}
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		}
		catch (Exception e)
		{
			Log.d("error", e.getMessage());
		}
		return null;
	}

	@Override
	public boolean onCreate()
	{
		database = openDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		Cursor cursor = null;

		switch (uriMatcher.match(uri))
		{
			case CITIES:

				cursor = database.query("v_cities_province", projection,
						selection, selectionArgs, null, null, sortOrder);
				break;
			case CITY_CODE:
				String cityCode = uri.getPathSegments().get(1);
				if (selection == null)
					selection = "city_code='" + cityCode + "'";
				else
					selection += " and (city_code='" + cityCode + "')";
				cursor = database.query("t_cities", projection, selection,
						selectionArgs, null, null, sortOrder);
				break;
			case CITY_NAME:
				String cityName = uri.getPathSegments().get(1);
				if (selection == null)
					selection = "city_name='" + cityName + "'";
				else
					selection += " and (city_name='" + cityName + "')";
				cursor = database.query("t_cities", projection, selection,
						selectionArgs, null, null, sortOrder);

				break;
			case CITIES_IN_PROVINCE:
				String provinceName = uri.getPathSegments().get(1);
				if (selection == null)
					selection = "province_name='" + provinceName + "'";
				else
					selection += " and (province_name='" + provinceName + "')";
				cursor = database.query("v_cities_province", projection, selection,
						selectionArgs, null, null, sortOrder);				
				break;
			
			default:
				throw new IllegalArgumentException("<" + uri + ">格式不正确.");
		}
		return cursor;

	}

	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
