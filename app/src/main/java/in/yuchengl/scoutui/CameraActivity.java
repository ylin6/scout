package in.yuchengl.scoutui;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.ServiceConfigurationError;

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
    private String mFriendID;
    private float mFriendLat;
    private float mFriendLong;
    private Thread mFriendThread;
    private Boolean mAlive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Toast.makeText(getApplicationContext(), "Waiting for location data",
                Toast.LENGTH_SHORT).show();

        ((Scout) getApplication()).startListening();
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
        mGLView = (MyGLSurfaceView) findViewById(R.id.overlay);

        /* Get friend's location */
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            mFriendID = null;
        } else {
            mFriendID = extras.getString("id");
        }

        mAlive = true;
        mFriendThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(mAlive) {
                        sleep(5000);
                        getFriendsLatLong();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mFriendThread.start();
    }

    @Override
    public void onResume() {
        ((Scout) getApplication()).startListening();
        mSensorManager.registerListener(this, mAccelerometer, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, mSensorManager.SENSOR_DELAY_NORMAL);

        mAlive = true;
        mFriendThread.start();

        super.onResume();
    }

    @Override
    public void onPause() {
        if (mInPreview) {
            mCamera.stopPreview();
        }
        mInPreview = false;
        ((Scout) getApplication()).stopListening();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagneticField);

        mAlive = false;

        super.onPause();
    }

    @Override
    public void onStop() {
        mCamera.release();
        ((Scout) getApplication()).stopListening();
        super.onStop();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        ((Scout) getApplication()).startListening();
        openCamera();
    }

    private Camera.Size getPreviewSize(int width, int height, Camera.Parameters params) {
        Camera.Size result = null;

        for (Camera.Size size : params.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
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
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
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
            } catch (Exception e) {
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
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    private void getFriendsLatLong() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", mFriendID);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mFriendLat = (float) object.getDouble("latitude");
                    mFriendLong = (float) object.getDouble("longitude");
                    Log.d("Friends", "Getting Friend data");
                } else {
                    Log.d("mFriendLocation", "Error: " + e.getMessage());
                }
            }
        });
    }

    private double getDirection(double azimuth, double startLat, double startLong, double destLat,
                                double destLong) {
        double dLong = destLong - startLong;
        double y = Math.sin(dLong) * Math.cos(destLat);
        double x = Math.cos(startLat) * Math.sin(destLat) - Math.sin(startLat) * Math.cos(destLat) *
                Math.cos(dLong);
        double bearing = Math.atan2(y, x);

        bearing = Math.toDegrees(bearing);
        bearing = (bearing + 360) % 360;
        return bearing + azimuth;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int i;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (i = 0; i < 3; i++) mGravityValues[i] = event.values[i];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (i = 0; i < 3; i++) mGeomagneticValues[i] = event.values[i];
                break;
        }

        if (mSensorManager.getRotationMatrix(mRotationMatrix, mInclinationMatrix, mGravityValues,
                mGeomagneticValues)) {
            mSensorManager.getOrientation(mRotationMatrix, mMatrixValues);

            double azimuth = Math.toDegrees(mMatrixValues[0]);
            if (azimuth < 0) azimuth = 360 + azimuth;

            double pitch = Math.toDegrees(mMatrixValues[1]);

            Location myLoc = ((Scout) getApplication()).getLocation();
            if (myLoc != null) {
                Log.d("loc", "CamLat: " + myLoc.getLatitude() + " CamLong: " +
                        myLoc.getLongitude());
                double direction = getDirection(azimuth, myLoc.getLatitude(), myLoc.getLongitude(),
                        41.703027, -86.239073);
/*
                double direction = getDirection(azimuth, 41.703682, -86.233733,
                        41.703027, -86.239073);
*/
                mGLView.update((float) pitch, (float) direction);
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
