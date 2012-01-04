package mobile.android.ch19.rotate.triangle;

import static android.opengl.GLES10.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer
{

	public interface TextureLoader
	{

		void load(GL10 gl);
	}

	public MyRenderer(Context context)
	{
		init(context, new RobotTextureLoader());
	}

	public MyRenderer(Context context, TextureLoader loader)
	{
		init(context, loader);
	}

	private void init(Context context, TextureLoader loader)
	{
		mContext = context;
		mTriangle = new Triangle();
		mTextureLoader = loader;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{

		glDisable(GL_DITHER);

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

		glClearColor(.0f, .0f, .0f, 1);
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);

		int[] textures = new int[1];
		glGenTextures(1, textures, 0);

		mTextureID = textures[0];
		glBindTexture(GL_TEXTURE_2D, mTextureID);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
		mTextureLoader.load(gl);
	}

	public void onDrawFrame(GL10 gl)
	{

		glDisable(GL_DITHER);

		glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mTextureID);
		glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		long time = SystemClock.uptimeMillis() % 4000L;
		float angle = 0.090f * ((int) time);

		glRotatef(angle, 0, 0, 1.0f);

		mTriangle.draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		glViewport(0, 0, w, h);

		float ratio = (float) w / h;
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glFrustumf(-ratio, ratio, -1, 1, 3, 7);
	}

	private Context mContext;
	private Triangle mTriangle;
	private int mTextureID;
	private TextureLoader mTextureLoader;

	private class RobotTextureLoader implements TextureLoader
	{
		public void load(GL10 gl)
		{
			InputStream is = mContext.getResources().openRawResource(
					R.raw.robot);
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

			GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
			bitmap.recycle();
		}
	}

	static class Triangle
	{
		public Triangle()
		{

			ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
			vbb.order(ByteOrder.nativeOrder());
			mFVertexBuffer = vbb.asFloatBuffer();

			ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
			tbb.order(ByteOrder.nativeOrder());
			mTexBuffer = tbb.asFloatBuffer();

			ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();

			float[] coords =
			{
					// X, Y, Z
					-0.5f, -0.25f, 0, 0.5f, -0.25f, 0, 0.0f, 0.559016994f, 0 };

			for (int i = 0; i < VERTS; i++)
			{
				for (int j = 0; j < 3; j++)
				{
					mFVertexBuffer.put(coords[i * 3 + j] * 2.0f);
				}
			}

			for (int i = 0; i < VERTS; i++)
			{
				for (int j = 0; j < 2; j++)
				{
					mTexBuffer.put(coords[i * 3 + j] * 2.0f + 0.5f);
				}
			}

			for (int i = 0; i < VERTS; i++)
			{
				mIndexBuffer.put((short) i);
			}

			mFVertexBuffer.position(0);
			mTexBuffer.position(0);
			mIndexBuffer.position(0);
		}

		public void draw(GL10 gl)
		{
			glFrontFace(GL_CCW);
			glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
			glEnable(GL_TEXTURE_2D);
			glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);
			glDrawElements(GL_TRIANGLE_STRIP, VERTS, GL_UNSIGNED_SHORT,
					mIndexBuffer);
		}

		private final static int VERTS = 3;

		private FloatBuffer mFVertexBuffer;
		private FloatBuffer mTexBuffer;
		private ShortBuffer mIndexBuffer;
	}
}
