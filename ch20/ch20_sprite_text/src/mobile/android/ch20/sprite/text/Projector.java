package mobile.android.ch20.sprite.text;

import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

class Projector
{

	private MatrixGrabber mGrabber;
	private boolean mMVPComputed;
	private float[] mMVP;
	private float[] mV;
	private int mX;
	private int mY;
	private int mViewWidth;
	private int mViewHeight;

	public Projector()
	{
		mMVP = new float[16];
		mV = new float[4];
		mGrabber = new MatrixGrabber();
	}

	public void setCurrentView(int x, int y, int width, int height)
	{
		mX = x;
		mY = y;
		mViewWidth = width;
		mViewHeight = height;
	}

	public void project(float[] obj, int objOffset, float[] win, int winOffset)
	{
		if (!mMVPComputed)
		{
			Matrix.multiplyMM(mMVP, 0, mGrabber.mProjection, 0,
					mGrabber.mModelView, 0);
			mMVPComputed = true;
		}

		Matrix.multiplyMV(mV, 0, mMVP, 0, obj, objOffset);

		float rw = 1.0f / mV[3];

		win[winOffset] = mX + mViewWidth * (mV[0] * rw + 1.0f) * 0.5f;
		win[winOffset + 1] = mY + mViewHeight * (mV[1] * rw + 1.0f) * 0.5f;
		win[winOffset + 2] = (mV[2] * rw + 1.0f) * 0.5f;
	}

	public void getCurrentProjection(GL10 gl)
	{
		mGrabber.getCurrentProjection(gl);
		mMVPComputed = false;
	}

	public void getCurrentModelView(GL10 gl)
	{
		mGrabber.getCurrentModelView(gl);
		mMVPComputed = false;
	}

}
