package in.yuchengl.scoutui;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class CameraActivity extends Activity implements SensorEventListener {
    private Camera mCamera;
    private MyGLSurfaceView mGLView;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private int mCameraId;
    private boolean mInPreview;
    public final String TAG = "SCOUT";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;
    private float[] mRotationMatrix;
    private float[] mInclinationMatrix;
    private float[] mMatrixValues;
    private float[] mGravityValues;
    private float[] mGeomagneticValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        openCamera();

        /* Set up surface view for camera feed */
        mInPreview = false;
        mPreview = (SurfaceView) findViewById(R.id.camera_preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(previewSurfaceCallback);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotationMatrix = new float[9];
        mInclinationMatrix = new float[9];
        mMatrixValues = new float[3];
        mGravityValues = new float[3];
        mGeomagneticValues = new float[3];
        mGLView = (MyGLSurfaceView)findViewById(R.id.overlay);
    }

    @Override
    public void onResume() {
        mSensorManager.registerListener(this, mAccelerometer, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, mSensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mInPreview) {
            mCamera.stopPreview();
        }
        mInPreview = false;
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagneticField);

        super.onPause();
    }

    @Override
    public void onStop() {
        mCamera.release();
        super.onStop();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        openCamera();
    }

    private Camera.Size getPreviewSize(int width, int height, Camera.Parameters params) {
        Camera.Size result = null;

        for (Camera.Size size : params.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                }
                else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return(result);
    }

    private void openCamera() {
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

    SurfaceHolder.Callback previewSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters params = mCamera.getParameters();

            Camera.Size size = getPreviewSize(width, height, params);
            if (size != null) {
                params.setPreviewSize(size.width, size.height);
                mCamera.setParameters(params);
                mCamera.startPreview();
                mInPreview = true;

                List<String> focusModes = params.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.setParameters(params);
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {}
                    });
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "sensor was changed");
        int i;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for(i=0; i<3; i++) mGravityValues[i] = event.values[i];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for(i=0; i<3; i++) mGeomagneticValues[i] = event.values[i];
                break;
        }

        if(mSensorManager.getRotationMatrix(mRotationMatrix, mInclinationMatrix, mGravityValues,
                mGeomagneticValues)) {
            mSensorManager.getOrientation(mRotationMatrix, mMatrixValues);
        }

        double degrees = Math.toDegrees(mMatrixValues[0]);
        mGLView.update((float)degrees);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
