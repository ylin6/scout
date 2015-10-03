package in.yuchengl.scoutui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.hardware.Camera;
import android.view.Surface;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private int mCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        /* open back camera */
        int num = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < num; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = i;
                mCamera = Camera.open(mCameraId);
            }
        }

        if (mCamera == null) {
            Log.d("myError", "Failed to open back camera");
        }

        setCameraDisplayOrientation(this, mCameraId, mCamera);

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }




}
