package mobile.android.ch20.combination.rotate;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer
{
	private boolean mContextSupportsFrameBufferObject;
	private int mTargetTexture;
	private int mFramebuffer;
	private int mFramebufferWidth = 256;
	private int mFramebufferHeight = 256;
	private int mSurfaceWidth;
	private int mSurfaceHeight;

	private Triangle mTriangle;
	private Cube mCube;
	private float mAngle;

	private static final boolean DEBUG_RENDER_OFFSCREEN_ONSCREEN = false;

	public void onDrawFrame(GL10 gl)
	{

		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				mFramebuffer);
		drawOffscreenImage(gl, mFramebufferWidth, mFramebufferHeight);
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
		drawOnscreen(gl, mSurfaceWidth, mSurfaceHeight);

	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{

		mSurfaceWidth = width;
		mSurfaceHeight = height;
		gl.glViewport(0, 0, width, height);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		mContextSupportsFrameBufferObject = checkIfContextSupportsFrameBufferObject(gl);
		if (mContextSupportsFrameBufferObject)
		{
			mTargetTexture = createTargetTexture(gl, mFramebufferWidth,
					mFramebufferHeight);
			mFramebuffer = createFrameBuffer(gl, mFramebufferWidth,
					mFramebufferHeight, mTargetTexture);

			mCube = new Cube();
			mTriangle = new Triangle();
		}
	}

	private void drawOnscreen(GL10 gl, int width, int height)
	{
		gl.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);

		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTargetTexture);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glActiveTexture(GL10.GL_TEXTURE0);

		long time = SystemClock.uptimeMillis() % 4000L;
		float angle = 0.090f * ((int) time);

		gl.glRotatef(angle, 0, 0, 1.0f);

		mTriangle.draw(gl);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	private void drawOffscreenImage(GL10 gl, int width, int height)
	{
		gl.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glClearColor(0, 0, 1, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -3.0f);

		gl.glRotatef(mAngle, 0, 1, 0);
		gl.glRotatef(mAngle * 0.25f, 1, 0, 0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		mCube.draw(gl);

		gl.glRotatef(mAngle * 2.0f, 0, 1, 1);
		gl.glTranslatef(0.5f, 0.5f, 0.5f);

		mCube.draw(gl);

		mAngle += 1.2f;

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}

	private int createTargetTexture(GL10 gl, int width, int height)
	{
		int texture;
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		texture = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,
				GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);
		
		return texture;
	}

	private int createFrameBuffer(GL10 gl, int width, int height,
			int targetTextureId)
	{
		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;
		int framebuffer;
		int[] framebuffers = new int[1];
		gl11ep.glGenFramebuffersOES(1, framebuffers, 0);
		framebuffer = framebuffers[0];
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				framebuffer);

		int depthbuffer;
		int[] renderbuffers = new int[1];
		gl11ep.glGenRenderbuffersOES(1, renderbuffers, 0);
		depthbuffer = renderbuffers[0];

		gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
				depthbuffer);
		gl11ep.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
				GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
		gl11ep.glFramebufferRenderbufferOES(
				GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,
				GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);

		gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
				targetTextureId, 0);
		int status = gl11ep
				.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
		if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES)
		{
			throw new RuntimeException("Framebuffer is not complete: "
					+ Integer.toHexString(status));
		}
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
		return framebuffer;
	}

	private boolean checkIfContextSupportsFrameBufferObject(GL10 gl)
	{
		return checkIfContextSupportsExtension(gl, "GL_OES_framebuffer_object");
	}


	private boolean checkIfContextSupportsExtension(GL10 gl, String extension)
	{
		String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS) + " ";

		return extensions.indexOf(" " + extension + " ") >= 0;
	}
}
