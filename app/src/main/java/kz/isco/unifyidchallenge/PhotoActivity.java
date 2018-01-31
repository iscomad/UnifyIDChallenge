package kz.isco.unifyidchallenge;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PhotoActivity extends Activity {
    public final static String TAG = PhotoActivity.class.getSimpleName();

    private Camera mCamera;
    @NonNull
    private Handler mHandler = new Handler();
    private int mPhotoCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // do we have a camera?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                mCamera = Camera.open(cameraId);
                setCameraPreview();
            }
        }
    }

    private void setCameraPreview() {
        ViewGroup cameraContainer = findViewById(R.id.activity_photo_preview);
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            cameraContainer.addView(new CameraView(this, mCamera));
        }
    }

    public void onClick(View view) {
        if (mCamera == null) {
            return;
        }

        final String groupName = "Group 1";

        mCamera.startPreview();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCamera.takePicture(null, null, new PhotoHandler(getApplicationContext(), groupName));
                mPhotoCount++;
                if (mPhotoCount < 10) {
                    mHandler.postDelayed(this, 500);
                } else {
                    onJobFinished();
                }
            }
        });
    }

    private void onJobFinished() {
        finish();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

}