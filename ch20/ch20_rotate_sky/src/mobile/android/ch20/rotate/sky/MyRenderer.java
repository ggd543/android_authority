package mobile.android.ch20.rotate.sky;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import mobile.android.ch20.wheel.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer {
    private boolean mContextSupportsCubeMap;
    private Grid mGrid;
    private int mCubeMapTextureID;
    private boolean mUseTexGen = false;
    private float mAngle;
    private Context mContext;
    public MyRenderer(Context context)
    {
    	mContext = context;
    }
    public void onDrawFrame(GL10 gl) {
      

        gl.glClearColor(0,0,0,0);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
       
        gl.glRotatef(mAngle,        0, 1, 0);
        gl.glRotatef(mAngle*0.25f,  1, 0, 0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        

        if (mContextSupportsCubeMap) {
            gl.glActiveTexture(GL10.GL_TEXTURE0);
            
            gl.glEnable(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP);
            
            gl.glBindTexture(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP, mCubeMapTextureID);
            
            GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;
            gl11ep.glTexGeni(GL11ExtensionPack.GL_TEXTURE_GEN_STR,
                    GL11ExtensionPack.GL_TEXTURE_GEN_MODE,
                    GL11ExtensionPack.GL_REFLECTION_MAP);
          
            gl.glEnable(GL11ExtensionPack.GL_TEXTURE_GEN_STR);
           
            gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_DECAL);
        }

   
        mGrid.draw(gl);

        if (mContextSupportsCubeMap) {
            gl.glDisable(GL11ExtensionPack.GL_TEXTURE_GEN_STR);
        }


        mAngle += 1.2f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        
        gl.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        
        // This test needs to be done each time a context is created,
        // because different contexts may support different extensions.
        mContextSupportsCubeMap = checkIfContextSupportsCubeMap(gl);

        mGrid = generateTorusGrid(gl, 60, 60, 3.0f, 0.75f);

        if (mContextSupportsCubeMap) {
            int[] cubeMapResourceIds = new int[]{
                    R.raw.skycubemap0, R.raw.skycubemap1, R.raw.skycubemap2,
                    R.raw.skycubemap3, R.raw.skycubemap4, R.raw.skycubemap5};
            mCubeMapTextureID = generateCubeMap(gl, cubeMapResourceIds);
        }
        
    }

    private int generateCubeMap(GL10 gl, int[] resourceIds) {
        
        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        int cubeMapTextureId = ids[0];
        gl.glBindTexture(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP, cubeMapTextureId);
        gl.glTexParameterf(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        for (int face = 0; face < 6; face++) {
            InputStream is = mContext.getResources().openRawResource(resourceIds[face]);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } finally {
                try {
                    is.close();
                } catch(IOException e) {
                   
                }
            }
            GLUtils.texImage2D(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,
                    bitmap, 0);
            bitmap.recycle();
        }
        
        return cubeMapTextureId;
    }

    private Grid generateTorusGrid(GL gl, int uSteps, int vSteps, float majorRadius, float minorRadius) {
        Grid grid = new Grid(uSteps + 1, vSteps + 1);
        for (int j = 0; j <= vSteps; j++) {
            double angleV = Math.PI * 2 * j / vSteps;
            float cosV = (float) Math.cos(angleV);
            float sinV = (float) Math.sin(angleV);
            for (int i = 0; i <= uSteps; i++) {
                double angleU = Math.PI * 2 * i / uSteps;
                float cosU = (float) Math.cos(angleU);
                float sinU = (float) Math.sin(angleU);
                float d = majorRadius+minorRadius*cosU;
                float x = d*cosV;
                float y = d*(-sinV);
                float z = minorRadius * sinU;

                float nx = cosV * cosU;
                float ny = -sinV * cosU;
                float nz = sinU;

                float length = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
                nx /= length;
                ny /= length;
                nz /= length;

                grid.set(i, j, x, y, z, nx, ny, nz);
            }
        }
        grid.createBufferObjects(gl);
        return grid;
    }

    private boolean checkIfContextSupportsCubeMap(GL10 gl) {
        return checkIfContextSupportsExtension(gl, "GL_OES_texture_cube_map");
    }

    /**
     * This is not the fastest way to check for an extension, but fine if
     * we are only checking for a few extensions each time a context is created.
     * @param gl
     * @param extension
     * @return true if the extension is present in the current context.
     */
    private boolean checkIfContextSupportsExtension(GL10 gl, String extension) {
        String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS) + " ";
        // The extensions string is padded with spaces between extensions, but not
        // necessarily at the beginning or end. For simplicity, add spaces at the
        // beginning and end of the extensions string and the extension string.
        // This means we can avoid special-case checks for the first or last
        // extension, as well as avoid special-case checks when an extension name
        // is the same as the first part of another extension name.
        return extensions.indexOf(" " + extension + " ") >= 0;
    }
}