package mobile.android.ch19.polygon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class MyRender implements Renderer
{

	int one = 0x10000;

	private IntBuffer triangleBuffer;
	private int[] triangleVertices = new int[]
	{ 0, one, 0, -one, -one, 0, one, -one, 0 };
	private IntBuffer quaterBuffer;
	private int[] quaterVertices = new int[]
	{ one, one, 0, -one, one, 0, one, -one, 0, -one, -one, 0 };

	@Override
	public void onDrawFrame(GL10 gl)
	{

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glLoadIdentity();

		gl.glTranslatef(1.5f, 0.0f, -6.0f);

		gl.glVertexPointer(3, GL10.GL_FIXED, 0, triangleBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

		gl.glLoadIdentity();

		gl.glTranslatef(-2.0f, 0.0f, -6.0f);

		gl.glVertexPointer(3, GL10.GL_FIXED, 0, quaterBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

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
		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(triangleVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());

		triangleBuffer = byteBuffer.asIntBuffer();
		triangleBuffer.put(triangleVertices);
		triangleBuffer.position(0);
		byteBuffer = ByteBuffer.allocateDirect(quaterVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		quaterBuffer = byteBuffer.asIntBuffer();
		quaterBuffer.put(quaterVertices);
		quaterBuffer.position(0);
	}

}
