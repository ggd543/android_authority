package mobile.android.demo.sina.microblog;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class FaceList extends Activity implements OnItemClickListener,
		OnItemSelectedListener
{
	private List<Integer> resIds = new ArrayList<Integer>();
	private GridView mGridView;
	private String[] faceDescription = new String[]
	{ "[¹þ¹þ]", "[ºÇºÇ]", "[Àá]", "[º¹]", "[°®Äã]", "[ÎûÎû]", "[ºß]", "[ÐÄ]", "[ÔÎ]", "[Å­]",
			"[µ°¸â]", "[»¨]", "[×¥¿ñ]", "[À§]", "[¸É±­]", "[Ì«Ñô]", "[ÏÂÓê]", "[ÉËÐÄ]",
			"[ÔÂÁÁ]", "[ÖíÍ·]" };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facelist);
		try
		{
			for (int i = 1; i <= 20; i++)
			{
				Field field = R.drawable.class.getField("face_"
						+ Util.fillZero(i, 3));

				resIds.add(field.getInt(null));

			}
			List<Map<String, Object>> cells = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < resIds.size(); i++)
			{
				Map<String, Object> cell = new HashMap<String, Object>();
				cell.put("ivFace", resIds.get(i));
				cells.add(cell);
			}
			SimpleAdapter simpleAdapter = new SimpleAdapter(this, cells,
					R.layout.face, new String[]
					{ "ivFace" }, new int[]
					{ R.id.ivFace });
			mGridView = (GridView) findViewById(R.id.gvFaceList);
			mGridView.setAdapter(simpleAdapter);
			mGridView.setOnItemClickListener(this);
			mGridView.setOnItemSelectedListener(this);
		}
		catch (Exception e)
		{
			Log.d("error", e.getMessage());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id)
	{
		Intent intent = new Intent();
		intent.putExtra("face", faceDescription[position]);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long id)
	{
		setTitle("Ñ¡Ôñ±íÇé" + faceDescription[position]);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
		// TODO Auto-generated method stub

	}

}
