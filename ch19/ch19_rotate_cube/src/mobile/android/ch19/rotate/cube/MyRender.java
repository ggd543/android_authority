package mobile.android.ch19.rotate.cube;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView.Renderer;

public class MyRender implements Renderer
{

	float rotateTri, rotateQuad;

	int one = 0x10000;
	private IntBuffer colorBuffer;
	private int[] colors = new int[]
	{

	one / 2, one, 0, one, one / 2, one, 0, one, one / 2, one, 0, one, one / 2,
			one, 0, one,

			one, one / 2, 0, one, one, one / 2, 0, one, one, one / 2, 0, one,
			one, one / 2, 0, one, one, one, 0, one, one, one, 0, one, one, one,
			0, one, one, one, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0,
			0, one, one, 0, 0, one,

			0, 0, one, one, 0, 0, one, one, 0, 0, one, one, 0, 0, one, one,

			one, 0, one, one, one, 0, one, one, one, 0, one, one, one, 0, one,
			one, };
	
	private IntBuffer quaterBuffer;
	private int[] quaterVertices = new int[]
	{ one, one, -one, -one, one, -one, one, one, one, -one, one, one,

	one, -one, one, -one, -one, one, one, -one, -one, -one, -one, -one,

	one, one, one, -one, one, one, one, -one, one, -one, -one, one,

	one, -one, -one, -one, -one, -one, one, one, -one, -one, one, -one,

	-one, one, one, -one, one, -one, -one, -one, one, -one, -one, -one,

	one, one, -one, one, one, one, one, -one, -one, one, -one, one, };

	@Override
	public void onDrawFrame(GL10 gl)
	{

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glLoadIdentity();

		gl.glTranslatef(0.0f, 0.0f, -6.0f);

	
		gl.glRotatef(rotateQuad, 1.0f, 0.0f, 0.0f);

		gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, quaterBuffer);

	
		for (int i = 0; i < 6; i++)
		{
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 4, 4);
		}

	
		gl.glFinish();

	
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

	
		rotateTri += 1.0f;
		rotateQuad -= 1.0f;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		
		float ratio = (float) width / height;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		gl.glLoadIdentity();
		
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		
		gl.glShadeModel(GL10.GL_SMOOTH);

		
		gl.glClearColor(0, 0, 0, 0);

		
		gl.glClearDepthf(1.0f);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		gl.glDepthFunc(GL10.GL_LEQUAL);

		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(colors.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		colorBuffer = byteBuffer.asIntBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);

		byteBuffer = ByteBuffer.allocateDirect(quaterVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		quaterBuffer = byteBuffer.asIntBuffer();
		quaterBuffer.put(quaterVertices);
		quaterBuffer.position(0);
	}
}