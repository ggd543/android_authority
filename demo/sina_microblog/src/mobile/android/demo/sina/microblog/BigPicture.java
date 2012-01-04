package mobile.android.demo.sina.microblog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class BigPicture extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bigpicture);
		ImageView ivBigPicture = (ImageView)findViewById(R.id.ivBigPicture);
		
		ivBigPicture.setImageBitmap(MicroBlog.mBitmap);

	
		
		
	} 

}
