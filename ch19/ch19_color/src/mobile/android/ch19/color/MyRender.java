package mobile.android.ch19.color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView.Renderer;

public class MyRender implements Renderer
{

	private FloatBuffer triangleBuffer;
	private float[] triangleVertices = new float[]
	{ -0.15f, 0.0f, 0.0f, 0.0f, 0.3f, 0.0f, 0.15f, 0.0f, 0.0f };

	private FloatBuffer quaterBuffer;
	private float[] quaterVertices = new float[]
	{ -0.3f, 0.3f, 0.0f, 0.3f, 0.3f, 0.0f, 0.3f, -0.3f, 0.0f, -0.3f, -0.3f, 0 };

	int one = 0x10000;
	private IntBuffer colorBuffer;
	private int[] colorVertices = new int[]
	{ one, 0, 0, one, 0, one, 0, one, 0, 0, one, one };


	@Override
	public void onDrawFrame(GL10 gl)
	{

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();

		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);

		gl.glLoadIdentity();
		gl.glTranslatef(-0.8f, 0.6f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleBuffer);		
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

		// 由于给的顶点顺序错误，无法画出矩形
		gl.glLoadIdentity();
		gl.glTranslatef(-0.2f, 0.6f, 0.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quaterBuffer);
	
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		// 画矩形

		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
		gl.glLoadIdentity();
		gl.glTranslatef(0.6f, 0.6f, 0.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quaterBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		// 画多边形
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, -0.4f, 0.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);



		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(triangleVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());

		triangleBuffer = byteBuffer.asFloatBuffer();
		triangleBuffer.put(triangleVertices);
		triangleBuffer.position(0);
		byteBuffer = ByteBuffer.allocateDirect(quaterVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		quaterBuffer = byteBuffer.asFloatBuffer();
		quaterBuffer.put(quaterVertices);
		quaterBuffer.position(0);
		
		byteBuffer = ByteBuffer.allocateDirect(quaterVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		colorBuffer = byteBuffer.asIntBuffer();
		colorBuffer.put(colorVertices);
		colorBuffer.position(0);
		
		
	}

}
