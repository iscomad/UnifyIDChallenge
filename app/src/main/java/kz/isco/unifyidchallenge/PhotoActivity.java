package kz.isco.unifyidchallenge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends Activity {
    public final static String TAG = PhotoActivity.class.getSimpleName();

    private static final String FOLDER_NAME_DATE_PATTERN = "yyyymmdd_hhmmss.SSS";
    private static final int REQUEST_CAMERA_AND_STORAGE = 1;

    private Camera mCamera;
    @NonNull
    private Handler mHandler = new Handler();
    private int mPhotoCount;
    private View mainLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mainLayout = findViewById(R.id.activity_photo_preview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                showCameraPreview();
            } else {
                requestCameraPermission();
            }
        } else {
            showCameraPreview();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_AND_STORAGE) {
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the permission has been granted
            if (containsPermission(grantResults, PackageManager.PERMISSION_GRANTED)) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(mainLayout, "Camera is available",
                        Snackbar.LENGTH_SHORT).show();
                showCameraPreview();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mainLayout, "Permission not granted",
                        Snackbar.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClick(View view) {
        if (mCamera == null) {
            return;
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(FOLDER_NAME_DATE_PATTERN);
        final String groupName = dateFormat.format(new Date());

        mCamera.startPreview();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCamera.takePicture(null, null, new PhotoHandler(groupName, mPhotoCount));
                mPhotoCount++;
                if (mPhotoCount < 10) {
                    mHandler.postDelayed(this, 500);
                } else {
                    onJobFinished();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

    private boolean containsPermission(int[] grantResults, int permissionGranted) {
        for (int i : grantResults) {
            if (i == permissionGranted) {
                return true;
            }
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG, "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mainLayout, "Please, allow us take pictures of you",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_CAMERA_AND_STORAGE);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_AND_STORAGE);
        }
    }

    private void setCameraPreview() {
        ViewGroup cameraContainer = findViewById(R.id.activity_photo_preview);
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            cameraContainer.addView(new CameraView(this, mCamera));
        }
    }

    private void showCameraPreview() {
        // do we have a camera?
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
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
}