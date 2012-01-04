package mobile.android.demo.smile.llk;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(new GameView(this));
	}
}