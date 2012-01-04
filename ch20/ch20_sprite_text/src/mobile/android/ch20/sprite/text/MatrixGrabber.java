package mobile.android.ch20.sprite.text;

import javax.microedition.khronos.opengles.GL10;

class MatrixGrabber {
    public MatrixGrabber() {
        mModelView = new float[16];
        mProjection = new float[16];
    }

    public void getCurrentState(GL10 gl) {
        getCurrentProjection(gl);
        getCurrentModelView(gl);
    }

  
    public void getCurrentModelView(GL10 gl) {
        getMatrix(gl, GL10.GL_MODELVIEW, mModelView);
    }

    public void getCurrentProjection(GL10 gl) {
        getMatrix(gl, GL10.GL_PROJECTION, mProjection);
    }

    private void getMatrix(GL10 gl, int mode, float[] mat) {
        MatrixTrackingGL gl2 = (MatrixTrackingGL) gl;
        gl2.glMatrixMode(mode);
        gl2.getMatrix(mat, 0);
    }

    public float[] mModelView;
    public float[] mProjection;
}
