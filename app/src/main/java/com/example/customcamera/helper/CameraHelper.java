package com.example.customcamera.helper;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jaspinder on 7/17/16.
 */
public class CameraHelper {
  public static final int MEDIA_TYPE_IMAGE = 1;
  private static final int IMAGE_WIDTH = 900;

  /**
   * Create a File for saving an image or video
   */
  public static File getOutputMediaFile(int type) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), "MyCameraApp");
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    } else {
      return null;
    }

    return mediaFile;
  }

  public static int getInSampleSize(BitmapFactory.Options options) {

    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;

    int reqWidth = IMAGE_WIDTH;
    int reqHeight = (int) (reqWidth * 1.0f / width * height);
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > reqHeight
          && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  public static int getImageOrientation(final String imageFilePath) {
    // to correct the orientation of the captured image
    int rotate = 0;
    try {
      ExifInterface exif = new ExifInterface(imageFilePath);
      int orientation = exif.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_270:
          rotate = 270;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          rotate = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          rotate = 90;
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rotate;
  }
}
