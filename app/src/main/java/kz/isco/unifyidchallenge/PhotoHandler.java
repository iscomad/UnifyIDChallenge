package kz.isco.unifyidchallenge;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import static kz.isco.unifyidchallenge.PhotoActivity.TAG;

public class PhotoHandler implements Camera.PictureCallback {

    private final String mGroupName;
    private final int mPhotoNumber;

    PhotoHandler(String groupName, int photoNumber) {
        this.mGroupName = groupName;
        this.mPhotoNumber = photoNumber;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.e(TAG, "Can't create directory to save image.");
            return;
        }

        String photoFile = mPhotoNumber + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (Exception error) {
            Log.d(TAG, "File" + filename + "not saved: " + error.getMessage(), error);
        }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return new File(sdDir, "UnifyIdChallenge" + File.separator + mGroupName);
    }
}