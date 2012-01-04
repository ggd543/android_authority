package mobile.android.demo.lunar.lander;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

class LunarView extends SurfaceView implements SurfaceHolder.Callback
{
	class LunarThread extends Thread
	{
		public static final int DIFFICULTY_EASY = 0;
		public static final int DIFFICULTY_HARD = 1;
		public static final int DIFFICULTY_MEDIUM = 2;
		public static final int PHYS_DOWN_ACCEL_SEC = 35;
		public static final int PHYS_FIRE_ACCEL_SEC = 80;
		public static final int PHYS_FUEL_INIT = 60;
		public static final int PHYS_FUEL_MAX = 100;
		public static final int PHYS_FUEL_SEC = 10;
		public static final int PHYS_SLEW_SEC = 120; 
		public static final int PHYS_SPEED_HYPERSPACE = 180;
		public static final int PHYS_SPEED_INIT = 30;
		public static final int PHYS_SPEED_MAX = 120;
		public static final int STATE_LOSE = 1;
		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
		public static final int STATE_WIN = 5;

		public static final int TARGET_ANGLE = 18;
		public static final int TARGET_BOTTOM_PADDING = 17; 
		public static final int TARGET_PAD_HEIGHT = 8; 
		public static final int TARGET_SPEED = 28;
		public static final double TARGET_WIDTH = 1.6; 
		public static final int UI_BAR = 100; 
		public static final int UI_BAR_HEIGHT = 10;
		private static final String KEY_DIFFICULTY = "mDifficulty";
		private static final String KEY_DX = "mDX";

		private static final String KEY_DY = "mDY";
		private static final String KEY_FUEL = "mFuel";
		private static final String KEY_GOAL_ANGLE = "mGoalAngle";
		private static final String KEY_GOAL_SPEED = "mGoalSpeed";
		private static final String KEY_GOAL_WIDTH = "mGoalWidth";

		private static final String KEY_GOAL_X = "mGoalX";
		private static final String KEY_HEADING = "mHeading";
		private static final String KEY_LANDER_HEIGHT = "mLanderHeight";
		private static final String KEY_LANDER_WIDTH = "mLanderWidth";
		private static final String KEY_WINS = "mWinsInARow";

		private static final String KEY_X = "mX";
		private static final String KEY_Y = "mY";

		private Bitmap mBackgroundImage;

		private int mCanvasHeight = 1;

		private int mCanvasWidth = 1;

		private Drawable mCrashedImage;

		private int mDifficulty;

		private double mDX;

		private double mDY;

		private boolean mEngineFiring;

		private Drawable mFiringImage;

		private double mFuel;

		private int mGoalAngle;

		private int mGoalSpeed;

		private int mGoalWidth;

		private int mGoalX;

		private Handler mHandler;

		private double mHeading;

		private int mLanderHeight;

		private Drawable mLanderImage;

		private int mLanderWidth;

		private long mLastTime;

		private Paint mLinePaint;

		private Paint mLinePaintBad;

		private int mMode;

		private int mRotating;

		private boolean mRun = false;

		private RectF mScratchRect;

		private SurfaceHolder mSurfaceHolder;

		private int mWinsInARow;

		private double mX;

		private double mY;

		public LunarThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler)
		{
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;

			Resources res = context.getResources();
			mLanderImage = context.getResources().getDrawable(
					R.drawable.lander_plain);
			mFiringImage = context.getResources().getDrawable(
					R.drawable.lander_firing);
			mCrashedImage = context.getResources().getDrawable(
					R.drawable.lander_crashed);

			mBackgroundImage = BitmapFactory.decodeResource(res,
					R.drawable.earthrise);

			mLanderWidth = mLanderImage.getIntrinsicWidth();
			mLanderHeight = mLanderImage.getIntrinsicHeight();

			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 0, 255, 0);

			mLinePaintBad = new Paint();
			mLinePaintBad.setAntiAlias(true);
			mLinePaintBad.setARGB(255, 120, 180, 0);

			mScratchRect = new RectF(0, 0, 0, 0);

			mWinsInARow = 0;
			mDifficulty = DIFFICULTY_MEDIUM;

			mX = mLanderWidth;
			mY = mLanderHeight * 2;
			mFuel = PHYS_FUEL_INIT;
			mDX = 0;
			mDY = 0;
			mHeading = 0;
			mEngineFiring = true;
		}

		public void doStart()
		{
			synchronized (mSurfaceHolder)
			{
				mFuel = PHYS_FUEL_INIT;
				mEngineFiring = false;
				mGoalWidth = (int) (mLanderWidth * TARGET_WIDTH);
				mGoalSpeed = TARGET_SPEED;
				mGoalAngle = TARGET_ANGLE;
				int speedInit = PHYS_SPEED_INIT;

				if (mDifficulty == DIFFICULTY_EASY)
				{
					mFuel = mFuel * 3 / 2;
					mGoalWidth = mGoalWidth * 4 / 3;
					mGoalSpeed = mGoalSpeed * 3 / 2;
					mGoalAngle = mGoalAngle * 4 / 3;
					speedInit = speedInit * 3 / 4;
				}
				else if (mDifficulty == DIFFICULTY_HARD)
				{
					mFuel = mFuel * 7 / 8;
					mGoalWidth = mGoalWidth * 3 / 4;
					mGoalSpeed = mGoalSpeed * 7 / 8;
					speedInit = speedInit * 4 / 3;
				}

				mX = mCanvasWidth / 2;
				mY = mCanvasHeight - mLanderHeight / 2;

				mDY = Math.random() * -speedInit;
				mDX = Math.random() * 2 * speedInit - speedInit;
				mHeading = 0;

				while (true)
				{
					mGoalX = (int) (Math.random() * (mCanvasWidth - mGoalWidth));
					if (Math.abs(mGoalX - (mX - mLanderWidth / 2)) > mCanvasHeight / 6)
						break;
				}

				mLastTime = System.currentTimeMillis() + 100;
				setState(STATE_RUNNING);
			}
		}

		public void pause()
		{
			synchronized (mSurfaceHolder)
			{
				if (mMode == STATE_RUNNING)
					setState(STATE_PAUSE);
			}
		}

		public synchronized void restoreState(Bundle savedState)
		{
			synchronized (mSurfaceHolder)
			{
				setState(STATE_PAUSE);
				mRotating = 0;
				mEngineFiring = false;

				mDifficulty = savedState.getInt(KEY_DIFFICULTY);
				mX = savedState.getDouble(KEY_X);
				mY = savedState.getDouble(KEY_Y);
				mDX = savedState.getDouble(KEY_DX);
				mDY = savedState.getDouble(KEY_DY);
				mHeading = savedState.getDouble(KEY_HEADING);

				mLanderWidth = savedState.getInt(KEY_LANDER_WIDTH);
				mLanderHeight = savedState.getInt(KEY_LANDER_HEIGHT);
				mGoalX = savedState.getInt(KEY_GOAL_X);
				mGoalSpeed = savedState.getInt(KEY_GOAL_SPEED);
				mGoalAngle = savedState.getInt(KEY_GOAL_ANGLE);
				mGoalWidth = savedState.getInt(KEY_GOAL_WIDTH);
				mWinsInARow = savedState.getInt(KEY_WINS);
				mFuel = savedState.getDouble(KEY_FUEL);
			}
		}

		@Override
		public void run()
		{
			while (mRun)
			{
				Canvas c = null;
				try
				{
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder)
					{
						if (mMode == STATE_RUNNING)
							updatePhysics();
						doDraw(c);
					}
				}
				finally
				{
					if (c != null)
					{
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		public Bundle saveState(Bundle map)
		{
			synchronized (mSurfaceHolder)
			{
				if (map != null)
				{
					map.putInt(KEY_DIFFICULTY, Integer.valueOf(mDifficulty));
					map.putDouble(KEY_X, Double.valueOf(mX));
					map.putDouble(KEY_Y, Double.valueOf(mY));
					map.putDouble(KEY_DX, Double.valueOf(mDX));
					map.putDouble(KEY_DY, Double.valueOf(mDY));
					map.putDouble(KEY_HEADING, Double.valueOf(mHeading));
					map.putInt(KEY_LANDER_WIDTH, Integer.valueOf(mLanderWidth));
					map.putInt(KEY_LANDER_HEIGHT,
							Integer.valueOf(mLanderHeight));
					map.putInt(KEY_GOAL_X, Integer.valueOf(mGoalX));
					map.putInt(KEY_GOAL_SPEED, Integer.valueOf(mGoalSpeed));
					map.putInt(KEY_GOAL_ANGLE, Integer.valueOf(mGoalAngle));
					map.putInt(KEY_GOAL_WIDTH, Integer.valueOf(mGoalWidth));
					map.putInt(KEY_WINS, Integer.valueOf(mWinsInARow));
					map.putDouble(KEY_FUEL, Double.valueOf(mFuel));
				}
			}
			return map;
		}

		public void setDifficulty(int difficulty)
		{
			synchronized (mSurfaceHolder)
			{
				mDifficulty = difficulty;
			}
		}

		public void setFiring(boolean firing)
		{
			synchronized (mSurfaceHolder)
			{
				mEngineFiring = firing;
			}
		}

		public void setRunning(boolean b)
		{
			mRun = b;
		}

		public void setState(int mode)
		{
			synchronized (mSurfaceHolder)
			{
				setState(mode, null);
			}
		}

		public void setState(int mode, CharSequence message)
		{
			synchronized (mSurfaceHolder)
			{
				mMode = mode;

				if (mMode == STATE_RUNNING)
				{
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", "");
					b.putInt("viz", View.INVISIBLE);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
				else
				{
					mRotating = 0;
					mEngineFiring = false;
					Resources res = mContext.getResources();
					CharSequence str = "";
					if (mMode == STATE_READY)
						str = res.getText(R.string.mode_ready);
					else if (mMode == STATE_PAUSE)
						str = res.getText(R.string.mode_pause);
					else if (mMode == STATE_LOSE)
						str = res.getText(R.string.mode_lose);
					else if (mMode == STATE_WIN)
						str = res.getString(R.string.mode_win_prefix)
								+ mWinsInARow + " "
								+ res.getString(R.string.mode_win_suffix);

					if (message != null)
					{
						str = message + "\n" + str;
					}

					if (mMode == STATE_LOSE)
						mWinsInARow = 0;

					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", str.toString());
					b.putInt("viz", View.VISIBLE);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}

		public void setSurfaceSize(int width, int height)
		{
			synchronized (mSurfaceHolder)
			{
				mCanvasWidth = width;
				mCanvasHeight = height;

				mBackgroundImage = mBackgroundImage.createScaledBitmap(
						mBackgroundImage, width, height, true);
			}
		}

		public void unpause()
		{
			synchronized (mSurfaceHolder)
			{
				mLastTime = System.currentTimeMillis() + 100;
			}
			setState(STATE_RUNNING);
		}

		boolean doKeyDown(int keyCode, KeyEvent msg)
		{
			synchronized (mSurfaceHolder)
			{
				boolean okStart = false;
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
					okStart = true;
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
					okStart = true;
				if (keyCode == KeyEvent.KEYCODE_S)
					okStart = true;

				boolean center = (keyCode == KeyEvent.KEYCODE_DPAD_UP);

				if (okStart
						&& (mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN))
				{
					doStart();
					return true;
				}
				else if (mMode == STATE_PAUSE && okStart)
				{
					unpause();
					return true;
				}
				else if (mMode == STATE_RUNNING)
				{
					if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
							|| keyCode == KeyEvent.KEYCODE_SPACE)
					{
						setFiring(true);
						return true;
					}
					else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							|| keyCode == KeyEvent.KEYCODE_Q)
					{
						mRotating = -1;
						return true;
					}
					else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							|| keyCode == KeyEvent.KEYCODE_W)
					{
						mRotating = 1;
						return true;
					}
					else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
					{
						pause();
						return true;
					}
				}

				return false;
			}
		}

		boolean doKeyUp(int keyCode, KeyEvent msg)
		{
			boolean handled = false;

			synchronized (mSurfaceHolder)
			{
				if (mMode == STATE_RUNNING)
				{
					if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
							|| keyCode == KeyEvent.KEYCODE_SPACE)
					{
						setFiring(false);
						handled = true;
					}
					else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							|| keyCode == KeyEvent.KEYCODE_Q
							|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							|| keyCode == KeyEvent.KEYCODE_W)
					{
						mRotating = 0;
						handled = true;
					}
				}
			}

			return handled;
		}

		private void doDraw(Canvas canvas)
		{
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);

			int yTop = mCanvasHeight - ((int) mY + mLanderHeight / 2);
			int xLeft = (int) mX - mLanderWidth / 2;

			int fuelWidth = (int) (UI_BAR * mFuel / PHYS_FUEL_MAX);
			mScratchRect.set(4, 4, 4 + fuelWidth, 4 + UI_BAR_HEIGHT);
			canvas.drawRect(mScratchRect, mLinePaint);

			double speed = Math.sqrt(mDX * mDX + mDY * mDY);
			int speedWidth = (int) (UI_BAR * speed / PHYS_SPEED_MAX);

			if (speed <= mGoalSpeed)
			{
				mScratchRect.set(4 + UI_BAR + 4, 4,
						4 + UI_BAR + 4 + speedWidth, 4 + UI_BAR_HEIGHT);
				canvas.drawRect(mScratchRect, mLinePaint);
			}
			else
			{
				mScratchRect.set(4 + UI_BAR + 4, 4,
						4 + UI_BAR + 4 + speedWidth, 4 + UI_BAR_HEIGHT);
				canvas.drawRect(mScratchRect, mLinePaintBad);
				int goalWidth = (UI_BAR * mGoalSpeed / PHYS_SPEED_MAX);
				mScratchRect.set(4 + UI_BAR + 4, 4, 4 + UI_BAR + 4 + goalWidth,
						4 + UI_BAR_HEIGHT);
				canvas.drawRect(mScratchRect, mLinePaint);
			}

			canvas.drawLine(mGoalX, 1 + mCanvasHeight - TARGET_PAD_HEIGHT,
					mGoalX + mGoalWidth, 1 + mCanvasHeight - TARGET_PAD_HEIGHT,
					mLinePaint);

			canvas.save();
			
			canvas.rotate((float) mHeading, (float) mX, mCanvasHeight
					- (float) mY);
			if (mMode == STATE_LOSE)
			{
				mCrashedImage.setBounds(xLeft, yTop, xLeft + mLanderWidth, yTop
						+ mLanderHeight);
				mCrashedImage.draw(canvas);
			}
			else if (mEngineFiring)
			{
				mFiringImage.setBounds(xLeft, yTop, xLeft + mLanderWidth, yTop
						+ mLanderHeight);
				mFiringImage.draw(canvas);
			}
			else
			{
				mLanderImage.setBounds(xLeft, yTop, xLeft + mLanderWidth, yTop
						+ mLanderHeight);
				mLanderImage.draw(canvas);
			}
			canvas.restore();
		}

		private void updatePhysics()
		{
			long now = System.currentTimeMillis();
			if (mLastTime > now)
				return;

			double elapsed = (now - mLastTime) / 1000.0;

			if (mRotating != 0)
			{
				mHeading += mRotating * (PHYS_SLEW_SEC * elapsed);

				if (mHeading < 0)
					mHeading += 360;
				else if (mHeading >= 360)
					mHeading -= 360;
			}

			double ddx = 0.0;
			double ddy = -PHYS_DOWN_ACCEL_SEC * elapsed;

			if (mEngineFiring)
			{
				double elapsedFiring = elapsed;
				double fuelUsed = elapsedFiring * PHYS_FUEL_SEC;

				if (fuelUsed > mFuel)
				{
					elapsedFiring = mFuel / fuelUsed * elapsed;
					fuelUsed = mFuel;
					mEngineFiring = false;
				}

				mFuel -= fuelUsed;

				double accel = PHYS_FIRE_ACCEL_SEC * elapsedFiring;

				double radians = 2 * Math.PI * mHeading / 360;
				ddx = Math.sin(radians) * accel;
				ddy += Math.cos(radians) * accel;
			}

			double dxOld = mDX;
			double dyOld = mDY;

			mDX += ddx;
			mDY += ddy;

			mX += elapsed * (mDX + dxOld) / 2;
			mY += elapsed * (mDY + dyOld) / 2;

			mLastTime = now;

			double yLowerBound = TARGET_PAD_HEIGHT + mLanderHeight / 2
					- TARGET_BOTTOM_PADDING;
			if (mY <= yLowerBound)
			{
				mY = yLowerBound;

				int result = STATE_LOSE;
				CharSequence message = "";
				Resources res = mContext.getResources();
				double speed = Math.sqrt(mDX * mDX + mDY * mDY);
				boolean onGoal = (mGoalX <= mX - mLanderWidth / 2 && mX
						+ mLanderWidth / 2 <= mGoalX + mGoalWidth);
				if (onGoal && Math.abs(mHeading - 180) < mGoalAngle
						&& speed > PHYS_SPEED_HYPERSPACE)
				{
					result = STATE_WIN;
					mWinsInARow++;
					doStart();

					return;
				}
				else if (!onGoal)
				{
					message = res.getText(R.string.message_off_pad);
				}
				else if (!(mHeading <= mGoalAngle || mHeading >= 360 - mGoalAngle))
				{
					message = res.getText(R.string.message_bad_angle);
				}
				else if (speed > mGoalSpeed)
				{
					message = res.getText(R.string.message_too_fast);
				}
				else
				{
					result = STATE_WIN;
					mWinsInARow++;
				}

				setState(result, message);
			}
		}
	}

	private Context mContext;

	private TextView mStatusText;

	private LunarThread thread;

	public LunarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		thread = new LunarThread(holder, context, new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{
				mStatusText.setVisibility(m.getData().getInt("viz"));
				mStatusText.setText(m.getData().getString("text"));
			}
		});

		setFocusable(true); 
	}

	public LunarThread getThread()
	{
		return thread;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		return thread.doKeyDown(keyCode, msg);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent msg)
	{
		return thread.doKeyUp(keyCode, msg);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		if (!hasWindowFocus)
			thread.pause();
	}

	public void setTextView(TextView textView)
	{
		mStatusText = textView;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		boolean retry = true;
		thread.setRunning(false);
		while (retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}
