package mobile.android.demo.sina.microblog;

import mobile.android.demo.sina.microblog.ColorPickerDialog.OnColorChangedListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class EditImage extends Activity implements OnColorChangedListener
{
	private MyView myView;
	private Bitmap bitmap1;
	private Bitmap bitmap2;
	private Rect dst = new Rect();
	private int mWidth;
	private int mHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.edit_image, null);
		myView = new MyView(this);
		linearLayout.setGravity(0x11);
		linearLayout.addView(myView);
		setContentView(linearLayout);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xFFFF0000);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(6);

		emboss = new EmbossMaskFilter(new float[]
		{ 1, 1, 1 }, 0.4f, 6, 3.5f);

		blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

	}

	private static Paint paint;
	private static MaskFilter emboss;
	private static MaskFilter blur;

	public void colorChanged(int color)
	{
		paint.setColor(color);
	}

	public class MyView extends View
	{

		private Canvas canvas;
		private Path path;
		private Paint bitmapPaint;

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			mWidth = widthMeasureSpec;
			mHeight = heightMeasureSpec;
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		public void loadBitmap1()
		{
			try
			{
				int canvasWidth = getWindowManager().getDefaultDisplay()
						.getWidth();

				int canvasHeight = getWindowManager().getDefaultDisplay()
						.getHeight();

				bitmap2 = WriteMicroBlog.mCameraBitmap;

				Rect src = new Rect();
				src.left = 0;
				src.top = 0;
				src.right = bitmap2.getWidth();
				src.bottom = bitmap2.getHeight();

				int dstWidth = bitmap2.getWidth();
				int dstHeight = bitmap2.getHeight();
				if ((float) dstWidth / dstHeight > (float) canvasWidth
						/ canvasHeight)
				{
					dst.left = 0;
					dst.right = canvasWidth;
					int height = (int) ((float) canvasWidth * dstHeight / dstWidth);
					dst.top = (canvasHeight - height) / 2;
					dst.bottom = (dst.top + height);

				}
				else
				{
					dst.top = 0;
					dst.bottom = canvasHeight;
					int width = (int) ((float) canvasHeight * dstWidth / dstHeight);
					dst.left = (canvasWidth - width) / 2;
					dst.right = (dst.left + width);

				}
				// bitmap1 = Bitmap.createBitmap(, 100,
				// Bitmap.Config.ARGB_8888);
				canvas = new Canvas(bitmap1);
				canvas.drawBitmap(bitmap2, src, dst, null);

			}
			catch (Exception e)
			{

			}
		}

		public void loadBitmap()
		{
			try
			{
				int canvasWidth = getWindowManager().getDefaultDisplay()
						.getWidth();

				int canvasHeight = getWindowManager().getDefaultDisplay()
						.getHeight();
				if (WriteMicroBlog.mCameraBitmap != null)
				{
					bitmap2 = WriteMicroBlog.mCameraBitmap;

					Rect src = new Rect();
					src.left = 0;
					src.top = 0;
					src.right = bitmap2.getWidth();
					src.bottom = bitmap2.getHeight();

					int dstWidth = bitmap2.getWidth();
					int dstHeight = bitmap2.getHeight();
					if ((float) dstWidth / dstHeight > (float) canvasWidth
							/ canvasHeight)
					{
						dst.left = 0;
						dst.right = canvasWidth;
						int height = (int) ((float) canvasWidth * dstHeight / dstWidth);
						dst.top = 0;
						dst.bottom = height;

					}
					else
					{
						dst.top = 0;
						dst.bottom = canvasHeight;
						int width = (int) ((float) canvasHeight * dstWidth / dstHeight);
						dst.left = 0;
						dst.right = width;

					}
					bitmap1 = Bitmap.createBitmap(dst.right - dst.left,
							dst.bottom - dst.top, Bitmap.Config.ARGB_8888);
					canvas = new Canvas(bitmap1);

					canvas.drawBitmap(bitmap2, src, dst, null);
				}
				else {
					bitmap1 = Bitmap.createBitmap(getWindow().getWindowManager().getDefaultDisplay().getWidth(),
							getWindow().getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(bitmap1);
					
				}

			}
			catch (Exception e)
			{

			}
		}

		public MyView(Context c)
		{
			super(c);

			loadBitmap();
			bitmapPaint = new Paint(Paint.DITHER_FLAG);
			path = new Path();

		}

		public void clear()
		{
			loadBitmap();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			canvas.drawBitmap(bitmap1, 0, 0, bitmapPaint);
			
			canvas.drawPath(path, paint);
		}

		private float mX, mY;

		private void touch_start(float x, float y)
		{
			if (x < dst.left && x > dst.right && y < dst.top && y > dst.bottom)
				return;
			path.moveTo(x, y);

			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y)
		{
			if (x < dst.left && x > dst.right && y < dst.top && y > dst.bottom)
				return;
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);

			path.quadTo(mX, mY, x, y);
			mX = x;
			mY = y;

		}

		private void touch_up()
		{

			canvas.drawPath(path, paint);
			path.reset();
			try
			{
				// FileOutputStream fos = new
				// FileOutputStream("/sdcard/image.png");
				// bitmap1.compress(CompressFormat.PNG, 100, fos);
				// fos.close();
			}
			catch (Exception e)
			{

			}

		}

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					touch_start(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					touch_move(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					touch_up();
					invalidate();
					break;
			}
			return true;
		}
	}

	private final int COLOR_MENU_ID = Menu.FIRST;
	private final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private final int BLUR_MENU_ID = Menu.FIRST + 2;
	private final int CLEAR_MENU_ID = Menu.FIRST + 3;
	private final int SAVE_MENU_ID = Menu.FIRST + 4;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "设置颜色");
		menu.add(0, EMBOSS_MENU_ID, 0, "浮雕效果");
		menu.add(0, BLUR_MENU_ID, 0, "喷涂效果");
		menu.add(0, CLEAR_MENU_ID, 0, "清除图形");
		menu.add(0, SAVE_MENU_ID, 0, "保存");
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case COLOR_MENU_ID:
				new ColorPickerDialog(this, this, paint.getColor()).show();
				return true;
			case EMBOSS_MENU_ID:
				if (paint.getMaskFilter() != emboss)
				{
					paint.setMaskFilter(emboss);
				}
				else
				{
					paint.setMaskFilter(null);
				}
				return true;
			case BLUR_MENU_ID:
				if (paint.getMaskFilter() != blur)
				{
					paint.setMaskFilter(blur);
				}
				else
				{
					paint.setMaskFilter(null);
				}
				return true;
			case CLEAR_MENU_ID:

				myView.clear();
				return true;
			case SAVE_MENU_ID:
				setResult(Activity.RESULT_OK);
				WriteMicroBlog.mCameraBitmap = bitmap1;

				finish();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}
}