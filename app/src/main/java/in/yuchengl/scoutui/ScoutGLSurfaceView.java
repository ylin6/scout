package in.yuchengl.scoutui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class ScoutGLSurfaceView extends GLSurfaceView {
    private final ScoutGLRenderer mRenderer;

    public ScoutGLSurfaceView(Context context, AttributeSet attrs) {
        super(context);
        setEGLContextClientVersion(2);
        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        mRenderer = new ScoutGLRenderer();
        setRenderer(mRenderer);
    }
}
