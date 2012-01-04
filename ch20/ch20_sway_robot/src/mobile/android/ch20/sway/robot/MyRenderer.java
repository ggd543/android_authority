package mobile.android.ch20.sway.robot;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer
{
	private Context mContext;
	private Grid mGrid;
	private int mTextureID;


	public MyRenderer(Context context)
	{
		mContext = context;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{

		gl.glDisable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0f, 0f, 0f, 1);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);


		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);

		mTextureID = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);

		InputStream is = mContext.getResources().openRawResource(R.raw.robot);
		Bitmap bitmap;
		try
		{
			bitmap = BitmapFactory.decodeStream(is);
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{

			}
		}

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();

		mGrid = generateWeightedGrid(gl);
	}

	public void onDrawFrame(GL10 gl)
	{

		gl.glDisable(GL10.GL_DITHER);

		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glEnable(GL10.GL_CULL_FACE);


		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		long time = SystemClock.uptimeMillis() % 4000L;

		double animationUnit = ((double) time) / 4000;
		float unitAngle = (float) Math.cos(animationUnit * 2 * Math.PI);
		float angle = unitAngle * 135f;

		gl.glEnable(GL11Ext.GL_MATRIX_PALETTE_OES);
		gl.glMatrixMode(GL11Ext.GL_MATRIX_PALETTE_OES);

		GL11Ext gl11Ext = (GL11Ext) gl;

		gl11Ext.glCurrentPaletteMatrixOES(0);
		gl11Ext.glLoadPaletteFromModelViewMatrixOES();

		gl.glRotatef(angle, 0, 0, 1.0f);

		gl11Ext.glCurrentPaletteMatrixOES(1);
		gl11Ext.glLoadPaletteFromModelViewMatrixOES();

		mGrid.draw(gl);

		gl.glDisable(GL11Ext.GL_MATRIX_PALETTE_OES);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		gl.glViewport(0, 0, w, h);

		float ratio = (float) w / h;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
	}

	private Grid generateWeightedGrid(GL gl)
	{
		final int uSteps = 20;
		final int vSteps = 20;

		float radius = 0.25f;
		float height = 2.0f;
		Grid grid = new Grid(uSteps + 1, vSteps + 1);

		for (int j = 0; j <= vSteps; j++)
		{
			for (int i = 0; i <= uSteps; i++)
			{
				double angle = Math.PI * 2 * i / uSteps;
				float x = radius * (float) Math.cos(angle);
				float y = height * ((float) j / vSteps - 0.5f);
				float z = radius * (float) Math.sin(angle);
				float u = -4.0f * (float) i / uSteps;
				float v = -4.0f * (float) j / vSteps;
				float w0 = (float) j / vSteps;
				float w1 = 1.0f - w0;
				grid.set(i, j, x, y, z, u, v, w0, w1, 0, 1);
			}
		}

		grid.createBufferObjects(gl);
		return grid;
	}
}
