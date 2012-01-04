package mobile.android.demo.lunar.lander;

import mobile.android.demo.lunar.lander.LunarView.LunarThread;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LunarLander extends Activity
{
	private static final int MENU_EASY = 1;

	private static final int MENU_HARD = 2;

	private static final int MENU_MEDIUM = 3;

	private static final int MENU_PAUSE = 4;

	private static final int MENU_RESUME = 5;

	private static final int MENU_START = 6;

	private static final int MENU_STOP = 7;

	private LunarThread mLunarThread;

	private LunarView mLunarView;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_START, 0, R.string.menu_start);
		menu.add(0, MENU_STOP, 0, R.string.menu_stop);
		menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
		menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
		menu.add(0, MENU_EASY, 0, R.string.menu_easy);
		menu.add(0, MENU_MEDIUM, 0, R.string.menu_medium);
		menu.add(0, MENU_HARD, 0, R.string.menu_hard);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case MENU_START:
				mLunarThread.doStart();
				return true;
			case MENU_STOP:
				mLunarThread.setState(LunarThread.STATE_LOSE,
						getText(R.string.message_stopped));
				return true;
			case MENU_PAUSE:
				mLunarThread.pause();
				return true;
			case MENU_RESUME:
				mLunarThread.unpause();
				return true;
			case MENU_EASY:
				mLunarThread.setDifficulty(LunarThread.DIFFICULTY_EASY);
				return true;
			case MENU_MEDIUM:
				mLunarThread.setDifficulty(LunarThread.DIFFICULTY_MEDIUM);
				return true;
			case MENU_HARD:
				mLunarThread.setDifficulty(LunarThread.DIFFICULTY_HARD);
				return true;
		}

		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lunar_layout);

		mLunarView = (LunarView) findViewById(R.id.lunar);
		mLunarThread = mLunarView.getThread();

		mLunarView.setTextView((TextView) findViewById(R.id.text));

		if (savedInstanceState == null)
		{
			mLunarThread.setState(LunarThread.STATE_READY);
			Log.w(this.getClass().getName(), "SIS is null");
		}
		else
		{
			mLunarThread.restoreState(savedInstanceState);
			Log.w(this.getClass().getName(), "SIS is nonnull");
		}
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		mLunarView.getThread().pause(); // pause game when Activity pauses
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		mLunarThread.saveState(outState);
		Log.w(this.getClass().getName(), "SIS called");
	}
}
