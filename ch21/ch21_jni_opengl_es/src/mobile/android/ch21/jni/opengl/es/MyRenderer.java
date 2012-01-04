package mobile.android.ch21.jni.opengl.es;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class MyRenderer implements GLSurfaceView.Renderer
{
	public void onDrawFrame(GL10 gl)
	{
		GL2JNILib.step();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		GL2JNILib.init(width, height);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{

	}
}