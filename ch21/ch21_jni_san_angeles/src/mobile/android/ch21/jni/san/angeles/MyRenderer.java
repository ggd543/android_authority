package mobile.android.ch21.jni.san.angeles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class MyRenderer implements GLSurfaceView.Renderer
{
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		nativeInit();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		nativeResize(w, h);
	}

	public void onDrawFrame(GL10 gl)
	{
		nativeRender();
	}

	private static native void nativeInit();

	private static native void nativeResize(int w, int h);

	private static native void nativeRender();

}