package mobile.android.demo.smile.llk;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View
{

	private int w;
	private int h;
	private int clientTop;
	private int clientRight;
	private int clientBottom;
	private int clientLeft;
	private Rect clientRect;
	private Paint clientRectPaint;
	private boolean displayMoreInfo;
	private boolean isInited;
	private Paint paint;
	private Paint paintLine;
	private Paint paintLine1;
	private Paint paintText;
	private Paint paintPross1;
	private Paint paintPross;
	private int pointW;
	private int pointH;
	private Rect btnRect1;
	private Rect btnRect2;
	private Rect btnRectClose;
	private Bitmap bitmapButton;
	private Rect prossRect;
	private Rect prossRect1;
	private Rect tRect;
	private Rect tRect1;

	private boolean ispause = false;
	private boolean isstart = false;
	private String pauseStr = "暂停中。";

	private Bitmap buff;
	private Bitmap bitmapPoint;
	private Bitmap bitmapHint;
	private Canvas buffCanvas;
	private int flevel;
	private int ffen = 0;
	private int timecount = 0;
	private int timecountper_l = 480;
	private boolean isdrawing = false;
	private Random random = new Random();
	private Resources res;
	private Point[] pointlistTemp;
	private int pointlistTemp_count = 0;
	private RefreshHandler mRedrawHandler = new RefreshHandler();
	private Point mousept;
	private Point lastpt;
	private Point hintpt1;
	private Point hintpt2;
	private boolean isontimering = false;
	private boolean isonmousedown = false;
	private int ontimercount = 0;

	class RefreshHandler extends Handler
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			GameView.this.ontimer();
			nexttimer(240);
		}

		public void nexttimer(long delayMillis)
		{
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	private Pointlist fpointlist = new Pointlist();

	class Pointlist
	{
		private int fxcount;
		private int fycount;
		private int[] l1;
		private int[] l2;
		private PointData[][] fpointreclist;

		class PointData
		{
			public int x;
			public int y;
			public boolean value;
			public Object data;
			public Rect rect;
			public int stat;
			public int imgidx;

		}

		private Point[] plist_1;
		private int plist_1_len;
		private Point[] plist_2;
		private int plist_2_len;
		private Point[] pathlist;
		public int pcount = 0;

		public void init(int xcount, int ycount)
		{
			fxcount = xcount;
			fycount = ycount;
			fpointreclist = new PointData[fxcount + 2][fycount + 2];
			for (int x_i = 0; x_i < fpointreclist.length; x_i++)
			{
				for (int y_i = 0; y_i < fpointreclist[x_i].length; y_i++)
				{
					fpointreclist[x_i][y_i] = new PointData();
					fpointreclist[x_i][y_i].x = x_i;
					fpointreclist[x_i][y_i].y = y_i;
					fpointreclist[x_i][y_i].value = false;
					fpointreclist[x_i][y_i].data = null;
					fpointreclist[x_i][y_i].rect = new Rect(0, 0, 0, 0);
					fpointreclist[x_i][y_i].stat = -1;
					fpointreclist[x_i][y_i].imgidx = -1;
				}
			}
			plist_1 = new Point[(fxcount + fycount + 2) * 4];
			for (int i = 0; i < plist_1.length; i++)
			{
				plist_1[i] = new Point(0, 0);
			}
			plist_1_len = 0;
			plist_2 = new Point[(fxcount + fycount + 2) * 4];
			for (int i = 0; i < plist_2.length; i++)
			{
				plist_2[i] = new Point(0, 0);
			}
			plist_2_len = 0;
			pathlist = new Point[2];
			for (int i = 0; i < pathlist.length; i++)
			{
				pathlist[i] = new Point(0, 0);
			}
			l1 = new int[fxcount * 2];
			l2 = new int[fxcount * 2];
		}

		public void setValue(int x, int y, Boolean value)
		{
			fpointreclist[x][y].value = value;
		}

		public boolean getValue(int x, int y)
		{
			return fpointreclist[x][y].value;
		}

		public void setData(int x, int y, Object data)
		{
			fpointreclist[x][y].data = data;
		}

		public void setrect(int x, int y, Rect r)
		{
			fpointreclist[x][y].rect.left = r.left;
			fpointreclist[x][y].rect.top = r.top;
			fpointreclist[x][y].rect.right = r.right;
			fpointreclist[x][y].rect.bottom = r.bottom;
		}

		public Rect getrect(int x, int y)
		{
			return fpointreclist[x][y].rect;
		}

		public void setstat(int x, int y, int v)
		{
			fpointreclist[x][y].stat = v;
		}

		public int getstat(int x, int y)
		{
			return fpointreclist[x][y].stat;
		}

		public void setimgidx(int x, int y, int v)
		{
			fpointreclist[x][y].imgidx = v;
		}

		public int getimgidx(int x, int y)
		{
			return fpointreclist[x][y].imgidx;
		}

		public boolean checkpp(Point p1, Point p2)
		{
			boolean result = false;
			if ((p1.x == p2.x) && (p1.y == p2.y))
			{
				return true;
			}
			if (p1.y == p2.y)
			{
				int x1 = p1.x < p2.x ? p1.x : p2.x;
				int x2 = p1.x > p2.x ? p1.x : p2.x;
				for (int i = x1 + 1; i <= x2 - 1; i++)
				{
					if (fpointreclist[i][p1.y].value)
					{
						return false;
					}
				}
				result = true;
			}
			if (p1.x == p2.x)
			{
				int y1 = p1.y < p2.y ? p1.y : p2.y;
				int y2 = p1.y > p2.y ? p1.y : p2.y;
				for (int i = y1 + 1; i <= y2 - 1; i++)
				{
					if (fpointreclist[p1.x][i].value)
					{
						return false;
					}
				}
				result = true;
			}
			return result;
		}

		public void mousetoxy(int x, int y, Point pt)
		{
			for (int x_i = 1; x_i <= fxcount; x_i++)
			{
				for (int y_i = 1; y_i <= fycount; y_i++)
				{
					if (fpointreclist[x_i][y_i].rect.contains(x, y))
					{
						pt.x = x_i;
						pt.y = y_i;
						return;
					}
				}
			}
			pt.x = -1;
			pt.y = -1;
		}

		public boolean isallok()
		{
			for (int x_i = 1; x_i <= fxcount; x_i++)
			{
				for (int y_i = 1; y_i <= fycount; y_i++)
				{
					if (fpointreclist[x_i][y_i].stat >= 0)
					{
						return false;
					}
				}
			}
			return true;
		}

		public void changeplace(int v)
		{
			int idx = 0;
			if ((v == 2) || (v == 6))
			{
				for (int i = 1; i <= fxcount; i++)
				{
					idx = 0;
					for (int j = 1; j <= fycount; j++)
					{
						if (getstat(i, j) >= 0)
						{
							idx = idx + 1;
							l1[idx] = getimgidx(i, j);
							l2[idx] = getstat(i, j);
						}
					}
					for (int j = 1; j <= fycount; j++)
					{
						if (j > idx)
						{
							setimgidx(i, j, -1);
							setstat(i, j, -1);
							setValue(i, j, false);
						}
						else
						{
							setimgidx(i, j, l1[j]);
							setstat(i, j, l2[j]);
							setValue(i, j, true);
						}
					}
				}
			}
			if ((v == 3) || (v == 7))
			{
				for (int i = 1; i <= fxcount; i++)
				{
					idx = fycount + 1;
					for (int j = fycount; j >= 1; j--)
					{
						if (getstat(i, j) >= 0)
						{
							idx = idx - 1;
							l1[idx] = getimgidx(i, j);
							l2[idx] = getstat(i, j);
						}
					}
					for (int j = fycount; j >= 1; j--)
					{
						if (j < idx)
						{
							setimgidx(i, j, -1);
							setstat(i, j, -1);
							setValue(i, j, false);
						}
						else
						{
							setimgidx(i, j, l1[j]);
							setstat(i, j, l2[j]);
							setValue(i, j, true);
						}
					}
				}
			}
			if ((v == 4) || (v == 8))
			{
				for (int j = 1; j <= fycount; j++)
				{
					idx = 0;
					for (int i = 1; i <= fxcount; i++)
					{
						if (getstat(i, j) >= 0)
						{
							idx = idx + 1;
							l1[idx] = getimgidx(i, j);
							l2[idx] = getstat(i, j);
						}
					}
					for (int i = 1; i <= fxcount; i++)
					{
						if (i > idx)
						{
							setimgidx(i, j, -1);
							setstat(i, j, -1);
							setValue(i, j, false);
						}
						else
						{
							setimgidx(i, j, l1[i]);
							setstat(i, j, l2[i]);
							setValue(i, j, true);
						}
					}
				}
			}

			if ((v == 5) || (v == 9))
			{
				for (int j = 1; j <= fycount; j++)
				{
					idx = fxcount + 1;
					for (int i = fxcount; i >= 1; i--)
					{
						if (getstat(i, j) >= 0)
						{
							idx = idx - 1;
							l1[idx] = getimgidx(i, j);
							l2[idx] = getstat(i, j);
						}
					}
					for (int i = fxcount; i >= 1; i--)
					{
						if (i < idx)
						{
							setimgidx(i, j, -1);
							setstat(i, j, -1);
							setValue(i, j, false);
						}
						else
						{
							setimgidx(i, j, l1[i]);
							setstat(i, j, l2[i]);
							setValue(i, j, true);
						}
					}
				}
			}
		}

		public void findstat(int v, Point pt)
		{
			for (int x_i = 0; x_i < fpointreclist.length; x_i++)
			{
				for (int y_i = 0; y_i < fpointreclist[x_i].length; y_i++)
				{
					if (fpointreclist[x_i][y_i].stat == v)
					{
						pt.x = x_i;
						pt.y = y_i;
						return;
					}
				}
			}
			pt.x = -1;
			pt.y = -1;
		}

		public boolean getpath(int x1, int y1, int x2, int y2)
		{
			boolean result = false;
			pcount = 0;
			if ((x1 == x2) && (y1 == y2))
			{
				return false;
			}
			plist_1[0].x = x1;
			plist_1[0].y = y1;
			plist_1_len = 1;

			plist_2[0].x = x2;
			plist_2[0].y = y2;
			plist_2_len = 1;
			int x;
			int y;

			for (int i = y1 - 1; i >= 0; i--)
			{
				x = x1;
				y = i;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_1[plist_1_len].x = x;
					plist_1[plist_1_len].y = y;
					plist_1_len = plist_1_len + 1;
				}
			}

			for (int i = y1 + 1; i <= fycount + 1; i++)
			{
				x = x1;
				y = i;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_1[plist_1_len].x = x;
					plist_1[plist_1_len].y = y;
					plist_1_len = plist_1_len + 1;
				}
			}
			for (int i = x1 - 1; i >= 0; i--)
			{
				x = i;
				y = y1;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_1[plist_1_len].x = x;
					plist_1[plist_1_len].y = y;
					plist_1_len = plist_1_len + 1;
				}
			}
			for (int i = x1 + 1; i <= fxcount + 1; i++)
			{
				x = i;
				y = y1;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_1[plist_1_len].x = x;
					plist_1[plist_1_len].y = y;
					plist_1_len = plist_1_len + 1;

				}
			}

			for (int i = y2 - 1; i >= 0; i--)
			{
				x = x2;
				y = i;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_2[plist_2_len].x = x;
					plist_2[plist_2_len].y = y;
					plist_2_len = plist_2_len + 1;
				}
			}
			for (int i = y2 + 1; i <= fycount + 1; i++)
			{
				x = x2;
				y = i;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_2[plist_2_len].x = x;
					plist_2[plist_2_len].y = y;
					plist_2_len = plist_2_len + 1;
				}
			}
			for (int i = x2 - 1; i >= 0; i--)
			{
				x = i;
				y = y2;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_2[plist_2_len].x = x;
					plist_2[plist_2_len].y = y;
					plist_2_len = plist_2_len + 1;
				}
			}

			for (int i = x2 + 1; i <= fxcount + 1; i++)
			{
				x = i;
				y = y2;
				if (fpointreclist[x][y].value)
				{
					break;
				}
				else
				{
					plist_2[plist_2_len].x = x;
					plist_2[plist_2_len].y = y;
					plist_2_len = plist_2_len + 1;
				}
			}
			pcount = 0;
			for (int i = 0; i < plist_1_len; i++)
			{
				for (int j = 0; j < plist_2_len; j++)
				{
					if (checkpp(plist_1[i], plist_2[j]))
					{
						result = true;
						if (i > 0)
						{
							pathlist[pcount].x = plist_1[i].x;
							pathlist[pcount].y = plist_1[i].y;
							pcount = pcount + 1;
						}

						if (j > 0)
						{
							pathlist[pcount].x = plist_2[j].x;
							pathlist[pcount].y = plist_2[j].y;
							pcount = pcount + 1;
						}
						return result;
					}
				}
			}
			return result;
		}

		private boolean getPHint(Point p1, Point p2)
		{
			for (int i_1 = 1; i_1 <= fxcount; i_1++)
			{
				for (int j_1 = 1; j_1 <= fycount; j_1++)
				{
					p1.x = i_1;
					p1.y = j_1;

					if (getimgidx(p1.x, p1.y) < 0)
						continue;
					if (getstat(p1.x, p1.y) < 0)
						continue;

					for (int i_2 = i_1; i_2 <= fxcount; i_2++)
					{
						for (int j_2 = 1; j_2 <= fycount; j_2++)
						{
							if ((i_2 == i_1) && (j_2 <= j_1))
								continue;

							p2.x = i_2;
							p2.y = j_2;

							if (getimgidx(p2.x, p2.y) < 0)
								continue;
							if (getstat(p2.x, p2.y) < 0)
								continue;

							if (getimgidx(p1.x, p1.y) != getimgidx(p2.x, p2.y))
								continue;

							if (getpath(p1.x, p1.y, p2.x, p2.y))
							{
								return true;
							}
						}
					}
				}
			}
			p1.x = -1;
			p1.y = -1;
			p2.x = -1;
			p2.y = -1;

			return false;
		}
	}

	public GameView(Context context)
	{
		super(context);
		displayMoreInfo = false;

		mousept = new Point(0, 0);
		lastpt = new Point(0, 0);
		paint = new Paint();
		paintLine = new Paint();
		paintLine.setARGB(255, 255, 0, 0);
		paintLine1 = new Paint();
		paintLine1.setARGB(255, 0, 0, 255);
		paintText = new Paint();
		paintText.setARGB(255, 0, 0, 0);
		paintText.setTextSize(13);
		paintText.setFlags(Paint.ANTI_ALIAS_FLAG);
		paintPross1 = new Paint();
		paintPross1.setARGB(255, 210, 210, 210);
		paintPross = new Paint();
		paintPross.setARGB(255, 0, 0, 210);
		clientRectPaint = new Paint();
		clientRectPaint.setARGB(255, 255, 255, 255);
		hintpt1 = new Point(-1, -1);
		hintpt2 = new Point(-1, -1);

		res = context.getResources();
		isInited = false;
		setFocusable(true);
	}

	public void ontimer()
	{
		if (isontimering)
		{
			return;
		}
		if (isonmousedown)
		{
			return;
		}
		isontimering = true;

		if ((isstart) && (!ispause))
		{
			if (timecount > 0)
			{
				timecount = timecount - 1;
			}
			else
			{
				beginlevel(flevel);
			}
		}
		if (ontimercount > 0)
		{
			ontimercount = ontimercount - 1;
		}

		if (ontimercount <= 0)
		{
			drawall();
			invalidate();
			ontimercount = 10;
		}

		isontimering = false;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		

		super.onDraw(canvas);
		canvas.drawBitmap(buff, 1, 1, paint);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		
		super.onLayout(changed, left, top, right, bottom);
		init(right - left, bottom - top);
	}

	private void drawblank()
	{
		buffCanvas.drawRect(clientRect, clientRectPaint);
	}

	private void drawtext()
	{
		buffCanvas.drawBitmap(bitmapButton, btnRect1.left, btnRect1.top, paint);
		if (!isstart)
		{
			buffCanvas.drawText("开始", btnRect1.left + 14, btnRect1.top + 14,
					paintText);
		}
		else
		{
			if (ispause)
			{
				buffCanvas.drawText("继续", btnRect1.left + 14,
						btnRect1.top + 14, paintText);
				buffCanvas.drawText(pauseStr, clientLeft, clientTop + 20,
						paintText);
			}
			else
			{
				buffCanvas.drawText("暂停", btnRect1.left + 14,
						btnRect1.top + 14, paintText);
			}
		} 

		buffCanvas.drawRect(tRect, clientRectPaint);
		buffCanvas.drawRect(tRect1, clientRectPaint);
		buffCanvas.drawText("级数:" + String.valueOf(flevel), tRect.left,
				tRect.bottom, paintText);
		buffCanvas.drawText("分数:" + String.valueOf(ffen), tRect1.left,
				tRect1.bottom, paintText);
	}

	private void drawpross()
	{
		buffCanvas.drawRect(prossRect, clientRectPaint);
		buffCanvas.drawLine(prossRect.left, prossRect.top, prossRect.right,
				prossRect.top, paintPross1);
		buffCanvas.drawLine(prossRect.right, prossRect.top, prossRect.right,
				prossRect.bottom, paintPross1);
		buffCanvas.drawLine(prossRect.right, prossRect.bottom, prossRect.left,
				prossRect.bottom, paintPross1);
		buffCanvas.drawLine(prossRect.left, prossRect.bottom, prossRect.left,
				prossRect.top, paintPross1);
		prossRect1.right = prossRect1.left
				+ (prossRect.right - prossRect.left - 1) * timecount
				/ timecountper_l;
		buffCanvas.drawRect(prossRect1, paintPross);

	}

	private void drawPoint()
	{

		Rect dstRect;
		Rect src_r = new Rect(0, 0, 0, 0);
		for (int i = 1; i <= fpointlist.fxcount; i++)
		{
			for (int j = 1; j <= fpointlist.fycount; j++)
			{
				if (fpointlist.getstat(i, j) < 0)
					continue;
				if (fpointlist.getimgidx(i, j) < 0)
					continue;
				dstRect = fpointlist.getrect(i, j);
				getbitmapPointRect(fpointlist.getimgidx(i, j),
						fpointlist.getstat(i, j), src_r);
				buffCanvas.drawBitmap(bitmapPoint, src_r, dstRect, paint);
				if (((hintpt1.x == i) && (hintpt1.y == j))
						|| ((hintpt2.x == i) && (hintpt2.y == j)))
				{
					buffCanvas.drawBitmap(bitmapHint, dstRect.left + 10,
							dstRect.bottom - 40, paint);
				}
			}
		}
	}

	private void drawall()
	{
		if (isdrawing)
		{
			return;
		}
		isdrawing = true;
		drawblank();
		drawtext();
		drawpross();
		if (isstart)
		{
			if (!ispause)
			{
				drawPoint();

			}
		}

		if (displayMoreInfo)
		{
			String s = String.valueOf(w) + "*" + String.valueOf(h);
			paint.setARGB(255, 0, 0, 255);
			buffCanvas.drawText(s, 1, 50, paintText);

			s = String.valueOf(System.currentTimeMillis());
			buffCanvas.drawText(s, 1, 100, paintText);

		}
		isdrawing = false;
	}

	private void getbitmapPointRect(int idx, int stat, Rect r)
	{
		r.left = 0 + (pointW) * stat;
		r.right = r.left + pointW;
		r.top = 0 + (pointH) * idx;
		r.bottom = r.top + pointH;
	}

	private void init(int w, int h)
	{
		if (isInited)
		{
			return;
		}
		isInited = true;
		w = w;
		h = h;
		if (buff != null)
		{
			buff = null;
		}
		Bitmap b_ground;
		int topleft_w = 0;
		int topleft_h = 0;
		int topright_w = 0;
		int topright_h = 0;
		int bottomleft_w = 0;
		int bottomleft_h = 0;
		int bottomright_w = 0;
		int bottomright_h = 0;
		int btn_left = 0;
		Rect src = null;
		Rect dst = null;
		Config config = Config.RGB_565;
		buff = Bitmap.createBitmap(w, h, config);
		buffCanvas = new Canvas(buff);
		b_ground = Bitmap.createBitmap(w, h, config);
		Canvas b_ground_Canvas = new Canvas(b_ground);

		Bitmap tmp = BitmapFactory.decodeResource(res, R.drawable.topleft);
		Paint tmppaint = new Paint();
		b_ground_Canvas.drawBitmap(tmp, 0, 0, tmppaint);
		topleft_w = tmp.getWidth();
		topleft_h = tmp.getHeight();
		btn_left = topleft_w - 1;
		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.topright);
		topright_w = tmp.getWidth();
		topright_h = tmp.getHeight();
		b_ground_Canvas.drawBitmap(tmp, w - topright_w, 0, tmppaint);
		btnRectClose = new Rect(w - topright_w + 18, 12, w - topright_w + 33,
				28);

		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.top);
		src = new Rect(0, 0, tmp.getWidth(), tmp.getHeight());
		dst = new Rect(topleft_w, 0, w - topright_w, tmp.getHeight());
		b_ground_Canvas.drawBitmap(tmp, src, dst, tmppaint);
		clientTop = tmp.getHeight();
		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.bottomleft);
		bottomleft_w = tmp.getWidth();
		bottomleft_h = tmp.getHeight();
		b_ground_Canvas.drawBitmap(tmp, 0, h - bottomleft_h, tmppaint);
		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.bottomright);
		bottomright_w = tmp.getWidth();
		bottomright_h = tmp.getHeight();
		b_ground_Canvas.drawBitmap(tmp, w - bottomright_w, h - bottomright_h,
				tmppaint);
		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.bottom);
		src = new Rect(0, 0, tmp.getWidth(), tmp.getHeight());
		dst = new Rect(bottomleft_w, h - tmp.getHeight(), w - bottomright_w, h);
		b_ground_Canvas.drawBitmap(tmp, src, dst, tmppaint);
		clientBottom = h - tmp.getHeight();
		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.left);
		src = new Rect(0, 0, tmp.getWidth(), tmp.getHeight());
		dst = new Rect(0, topleft_h, tmp.getWidth(), h - bottomleft_h);
		b_ground_Canvas.drawBitmap(tmp, src, dst, tmppaint);
		clientLeft = tmp.getWidth();
		tmp = null;
		tmp = BitmapFactory.decodeResource(res, R.drawable.right);
		src = new Rect(0, 0, tmp.getWidth(), tmp.getHeight());
		dst = new Rect(w - tmp.getWidth(), topright_h, w, h - bottomright_h);
		b_ground_Canvas.drawBitmap(tmp, src, dst, tmppaint);
		clientRight = w - tmp.getWidth();

		tmp = null;
		bitmapButton = BitmapFactory.decodeResource(res, R.drawable.btn);
		tmp = BitmapFactory.decodeResource(res, R.drawable.btn);
		src = new Rect(0, 0, tmp.getWidth(), tmp.getHeight());
		btn_left = btn_left - 2;
		btnRect1 = new Rect(btn_left, 15, btn_left + tmp.getWidth(),
				15 + tmp.getHeight());
		b_ground_Canvas.drawBitmap(tmp, src, btnRect1, tmppaint);
		btn_left = btnRect1.right;
		tmp = null;
		

		clientRect = new Rect(clientLeft, clientTop, clientRight, clientBottom);
		prossRect = new Rect(clientLeft - 1, clientTop - 15, clientRight - 140,
				clientTop - 4);
		prossRect1 = new Rect(prossRect.left + 1, prossRect.top + 1,
				prossRect.right - 1, prossRect.bottom);
		tRect = new Rect(prossRect.right + 5, prossRect.top - 2,
				prossRect.right + 55, prossRect.bottom);
		tRect1 = new Rect(tRect.right + 5, prossRect.top - 2, clientRight + 10,
				prossRect.bottom);

		pointW = 45;
		pointH = 51;

		int w_client = clientRight - clientLeft;
		w_client = w_client / pointW;

		int h_client = clientBottom - clientTop;
		h_client = h_client / pointH;
		if (w_client * h_client % 2 == 1)
		{
			h_client = h_client - 1;
		}

		fpointlist.init(w_client, h_client);
		pointlistTemp = new Point[w_client * h_client];
		for (int i = 0; i < w_client * h_client; i++)
		{
			pointlistTemp[i] = new Point(0, 0);
		}
		int x0 = clientLeft
				+ ((clientRight - clientLeft) - (pointW * w_client)) / 2 + 1;
		int y0 = clientTop + ((clientBottom - clientTop) - (pointH * h_client))
				/ 2 + 1;
		Rect r = new Rect(0, 0, 0, 0);
		for (int i = 1; i <= w_client; i++)
		{
			for (int j = 1; j <= h_client; j++)
			{
				r.top = y0 + (j - 1) * pointH;
				r.bottom = r.top + pointH;
				r.left = x0 + (i - 1) * pointW;
				r.right = r.left + pointW;
				fpointlist.setrect(i, j, r);
				fpointlist.setstat(i, j, -1);
				fpointlist.setimgidx(i, j, -1);
			}
		}
		Rect r_tmp;
		for (int i = 1; i <= w_client; i++)
		{
			r_tmp = fpointlist.getrect(i, 1);
			r.top = r_tmp.top - 1 - (r_tmp.bottom - r_tmp.top);
			r.bottom = r_tmp.top - 1;
			r.left = r_tmp.left;
			r.right = r_tmp.right;
			fpointlist.setrect(i, 0, r);
			fpointlist.setstat(i, 0, -1);
			fpointlist.setimgidx(i, 0, -1);

			r_tmp = fpointlist.getrect(i, h_client);
			r.bottom = r_tmp.bottom + 1 + (r_tmp.bottom - r_tmp.top);
			r.top = r_tmp.bottom + 1;
			r.left = r_tmp.left;
			r.right = r_tmp.right;
			fpointlist.setrect(i, h_client + 1, r);
			fpointlist.setstat(i, h_client + 1, -1);
			fpointlist.setimgidx(i, h_client + 1, -1);
		}
		for (int j = 0; j <= h_client + 1; j++)
		{
			r_tmp = fpointlist.getrect(1, j);
			r.left = r_tmp.left - 1 - (r_tmp.right - r_tmp.left);
			r.right = r_tmp.left - 1;
			r.top = r_tmp.top;
			r.bottom = r_tmp.bottom;
			fpointlist.setrect(0, j, r);
			fpointlist.setstat(0, j, -1);
			fpointlist.setimgidx(0, j, -1);

			r_tmp = fpointlist.getrect(w_client, j);
			r.right = r_tmp.right + 1 + (r_tmp.right - r_tmp.left);
			r.left = r_tmp.right + 1;
			r.top = r_tmp.top;
			r.bottom = r_tmp.bottom;
			fpointlist.setrect(w_client + 1, j, r);
			fpointlist.setstat(w_client + 1, j, -1);
			fpointlist.setimgidx(w_client + 1, j, -1);
		}

		bitmapPoint = BitmapFactory.decodeResource(res, R.drawable.point);
		bitmapHint = BitmapFactory.decodeResource(res, R.drawable.hint);
		buffCanvas.drawBitmap(b_ground, 0, 0, tmppaint);
		
		mRedrawHandler.nexttimer(300);
		b_ground = null;

	}

	private void setispause(boolean v)
	{
		ispause = v;
		if (ispause)
		{
			
		}
		else
		{
			
		}

	}

	private void setimgidx(int imgidx)
	{
		int idx = random.nextInt(pointlistTemp_count) % pointlistTemp_count;
		fpointlist
				.setimgidx(pointlistTemp[idx].x, pointlistTemp[idx].y, imgidx);
		fpointlist.setstat(pointlistTemp[idx].x, pointlistTemp[idx].y, 0);
		fpointlist.setValue(pointlistTemp[idx].x, pointlistTemp[idx].y, true);
		for (int i = idx; i < pointlistTemp_count - 1; i++)
		{
			pointlistTemp[i].x = pointlistTemp[i + 1].x;
			pointlistTemp[i].y = pointlistTemp[i + 1].y;
		}
		pointlistTemp_count = pointlistTemp_count - 1;
	}

	private void beginlevel(int l)
	{
		if (l >= 10)
		{
			flevel = 1;
		}
		else
		{
			flevel = l;
		}
		setispause(false);

		hintpt1.x = -1;
		hintpt1.y = -1;
		hintpt2.x = -1;
		hintpt2.y = -1;
		int idx = 0;
		for (int x = 1; x <= fpointlist.fxcount; x++)
		{
			for (int y = 1; y <= fpointlist.fycount; y++)
			{
				pointlistTemp[idx].x = x;
				pointlistTemp[idx].y = y;
				idx = idx + 1;
			}
		}
		pointlistTemp_count = idx;
		int imgcount = 16 + flevel * 2;
		int imgidx = 0;
		for (int i = 1; i <= fpointlist.fxcount * fpointlist.fycount / 2; i++)
		{
			imgidx = random.nextInt(imgcount) % imgcount;
			setimgidx(imgidx);
			setimgidx(imgidx);
		}
		timecount = timecountper_l;
		isstart = true;
		drawall();
		invalidate();
	}

	private void changecolor()
	{
		for (int k = 0; k < 10; k++)
		{
			int idx = 0;
			for (int x = 1; x <= fpointlist.fxcount; x++)
			{
				for (int y = 1; y <= fpointlist.fycount; y++)
				{
					if ((fpointlist.getstat(x, y) >= 0)
							&& (fpointlist.getValue(x, y) == true)
							&& (fpointlist.getimgidx(x, y) >= 0))
					{
						pointlistTemp[idx].x = x;
						pointlistTemp[idx].y = y;
						idx = idx + 1;
					}
				}
			}
			pointlistTemp_count = idx;
			int imgcount = 16 + flevel * 2;
			int imgidx = 0;
			int c = pointlistTemp_count / 2;
			for (int i = 1; i <= c; i++)
			{
				imgidx = random.nextInt(imgcount) % imgcount;
				setimgidx(imgidx);
				setimgidx(imgidx);
			}
			if (fpointlist.getPHint(hintpt1, hintpt2))
			{
				break;
			}
		}
	}

	private void dobtn_1()
	{
		if (isstart)
		{
			if (ispause)
			{
				setispause(false);
			}
			else
			{
				setispause(true);
			}
		}
		else
		{
			beginlevel(1);

		}
	}

	private void dobtn_2()
	{
		if (ffen > 200)
		{
			ffen = ffen - 200;
		}
		if (!fpointlist.getPHint(hintpt1, hintpt2))
		{
			hintpt1.x = -1;
			hintpt1.y = -1;
			hintpt2.x = -1;
			hintpt1.y = -1;
			changecolor();
			ontimercount = 1;
		}
		else
		{

 
			ontimercount = 1;
			if (timecount > 80)
			{
				timecount = timecount - 80;
			}
			else
			{
				timecount = 40;
			}

		}
	}

	private int changtor_x(int x, Rect r)
	{
		if (x < r.left + 2)
		{
			return r.left + 2;
		}
		if (x > r.right - 2)
		{
			return r.right - 2;
		}
		return x;
	}

	private int changtor_y(int y, Rect r)
	{
		if (y < r.top + 2)
		{
			return r.top + 2;
		}
		if (y > r.bottom - 2)
		{
			return r.bottom - 2;
		}
		return y;
	}

	private void drawlinerect(Point p1, Point p2)
	{

		Rect r1 = fpointlist.getrect(p1.x, p1.y);
		Rect r2 = fpointlist.getrect(p2.x, p2.y);
		int x1 = changtor_x((r1.left + r1.right) / 2, clientRect);
		int y1 = changtor_y((r1.top + r1.bottom) / 2, clientRect);
		int x2 = changtor_x((r2.left + r2.right) / 2, clientRect);
		int y2 = changtor_y((r2.top + r2.bottom) / 2, clientRect);

		buffCanvas.drawLine(x1, y1, x2, y2, paintLine);
		if (x1 == x2)
		{
			buffCanvas.drawLine(x1 - 1, y1, x2 - 1, y2, paintLine);
			buffCanvas.drawLine(x1 + 1, y1, x2 + 1, y2, paintLine);
		}
		if (y1 == y2)
		{
			buffCanvas.drawLine(x1, y1 - 1, x2, y2 - 1, paintLine);
			buffCanvas.drawLine(x1, y1 + 1, x2, y2 + 1, paintLine);
		}

	}

	private void dopoint()
	{

		if (fpointlist.getstat(mousept.x, mousept.y) < 0)
		{

			return;
		}

		fpointlist.findstat(1, lastpt);
		if (lastpt.x >= 0)
		{
			fpointlist.setstat(lastpt.x, lastpt.y, 0);
		}

		if (fpointlist.getstat(mousept.x, mousept.y) >= 0)
		{
			fpointlist.setstat(mousept.x, mousept.y, 1);
		}
		hintpt1.x = -1;
		hintpt1.y = -1;
		hintpt2.x = -1;
		hintpt2.y = -1;
		drawPoint();
		if ((lastpt.x >= 0)
				&& ((lastpt.x != mousept.x) || (lastpt.y != mousept.y)))
		{
			int lastimgidx = fpointlist.getimgidx(lastpt.x, lastpt.y);
			if ((fpointlist.getstat(mousept.x, mousept.y) >= 0)
					&& (fpointlist.getstat(lastpt.x, lastpt.y) >= 0)
					&& (lastimgidx >= 0)
					&& (lastimgidx == fpointlist
							.getimgidx(mousept.x, mousept.y)))
			{
				if (fpointlist
						.getpath(lastpt.x, lastpt.y, mousept.x, mousept.y))
				{
					ffen = ffen + 100;
					if (timecount < timecountper_l - 40)
					{
						timecount = timecount + 40;
					}
					else
					{
						timecount = timecountper_l;
					}
					if (fpointlist.pcount == 0)
					{
						drawlinerect(lastpt, mousept);
					}
					if (fpointlist.pcount == 1)
					{
						drawlinerect(lastpt, fpointlist.pathlist[0]);
						drawlinerect(fpointlist.pathlist[0], mousept);

					}
					if (fpointlist.pcount == 2)
					{
						drawlinerect(lastpt, fpointlist.pathlist[0]);
						drawlinerect(fpointlist.pathlist[0],
								fpointlist.pathlist[1]);
						drawlinerect(fpointlist.pathlist[1], mousept);
					}

					fpointlist.setstat(lastpt.x, lastpt.y, -1);
					fpointlist.setstat(mousept.x, mousept.y, -1);
					fpointlist.setValue(lastpt.x, lastpt.y, false);
					fpointlist.setValue(mousept.x, mousept.y, false);
					ontimercount = 3;
					if (fpointlist.isallok())
					{
						timecount = timecountper_l;
						beginlevel(flevel + 1);
					}
					else
					{
						fpointlist.changeplace(flevel);

					}
				}
			}
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		if (isonmousedown)
			return super.onTouchEvent(event);

		isontimering = true;
		isonmousedown = true;

		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			int ax = (int) event.getX();
			int ay = (int) event.getY();
 
			if (btnRectClose.contains(ax, ay))
			{
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			if (btnRect1.contains(ax, ay))
			{
				dobtn_1();
			}

			if (clientRect.contains(ax, ay))
			{
				fpointlist.mousetoxy(ax, ay, mousept);
				if (mousept.x >= 1)
				{
					dopoint();

				}
			}

		}
		isonmousedown = false;
		isontimering = false;
		return super.onTouchEvent(event);
	}
}
