package mobile.android.demo.snake;

import com.example.android.snake.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class Snake extends Activity
{

	private SnakeView mSnakeView;

	private static String ICICLE_KEY = "snake-view";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.snake_layout);

		mSnakeView = (SnakeView) findViewById(R.id.snake);
		mSnakeView.setTextView((TextView) findViewById(R.id.text));

		if (savedInstanceState == null)
		{
			mSnakeView.setMode(SnakeView.READY);
		}
		else
		{
			Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
			if (map != null)
			{
				mSnakeView.restoreState(map);
			}
			else
			{
				mSnakeView.setMode(SnakeView.PAUSE);
			}
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mSnakeView.setMode(SnakeView.PAUSE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
	}

}
