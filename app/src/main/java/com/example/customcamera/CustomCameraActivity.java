package com.example.customcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.customcamera.helper.CameraHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jaspinder on 7/17/16.
 */
public class CustomCameraActivity extends AppCompatActivity implements OnClickListener {

  private CameraPreview mCameraPreview;
  private ImageView mFlash;
  private ImageView mGrid;
  private ImageView mGallery;
  private ImageView mCameraInfo;
  private ImageView mCancel;
  /**
   * grid view parameters
   */
  private RelativeLayout mGridLayout;

  private Camera mCamera;
  /**
   * Flag to avoid taking picture while a previous one is being saved.
   */
  private boolean mSafeToTakePicture = true;
  private final String TAG = "CustomCameraActivity";
  private int mFlashMode = 0;

  private static final String FLASH_AUTO = "auto";
  private static final String FLASH_ON = "on";
  private static final String FLASH_OFF = "off";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom_camera);

    mCancel = (ImageView) findViewById(R.id.ivCancel);
    mGallery = (ImageView) findViewById(R.id.ivGallery);
    mFlash = (ImageView) findViewById(R.id.ivFlash);
    mGrid = (ImageView) findViewById(R.id.ivGrid);
    mCameraInfo = (ImageView) findViewById(R.id.ivCaptureInfo);
    mGridLayout = (RelativeLayout) findViewById(R.id.rlGrid);

    //Set up click listeners
    mGallery.setOnClickListener(this);
    mCancel.setOnClickListener(this);
    mFlash.setOnClickListener(this);
    mGrid.setOnClickListener(this);
    mCameraInfo.setOnClickListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    startCamera();
  }

  private void startCamera() {
    FrameLayout cameraPreviewLayout = (FrameLayout) findViewById(R.id.flCameraPreview);
    cameraPreviewLayout.removeAllViews();
    mCamera = getCameraInstance();
    mCameraPreview = new CameraPreview(this, mCamera);
    cameraPreviewLayout.addView(mCameraPreview);
    findViewById(R.id.bButtonCapture).setOnClickListener(this);
  }

  /**
   * A safe way to get an instance of the Camera object.
   */
  public static Camera getCameraInstance() {
    Camera c = null;
    try {
      c = Camera.open(); // attempt to get a Camera instance
    } catch (Exception e) {
      // Camera is not available (in use or does not exist)
    }
    return c; // returns null if camera is unavailable
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.bButtonCapture:
        if (mSafeToTakePicture) {
          mSafeToTakePicture = false;
          mCamera.takePicture(null, null, mPicture);
        }
        break;
      case R.id.ivCancel:
        finish();
        break;
      case R.id.ivFlash:
        if (mCameraPreview.isCameraReady() && mSafeToTakePicture) {
          if (mFlash.getTag().equals(FLASH_AUTO)) {
            mFlash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.capture_flash_on));
            mFlash.setTag(FLASH_ON);
            mFlashMode = 1;
          } else if (mFlash.getTag().equals(FLASH_ON)) {
            mFlash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.capture_flash_off));
            mFlash.setTag(FLASH_OFF);
            mFlashMode = 2;
          } else {
            mFlash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.capture_flash_on_auto));
            mFlash.setTag(FLASH_AUTO);
            mFlashMode = 0;
          }

          mCameraPreview.setFlashMode(mFlashMode);
        }
        break;
      case R.id.ivGrid:
        if (mGrid.getTag().equals(FLASH_ON)) {
          mGrid.setTag(FLASH_OFF);
          mGridLayout.setVisibility(View.GONE);
          mGrid.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.capture_grid_off));
        } else {
          mGrid.setTag(FLASH_ON);
          mGridLayout.setVisibility(View.VISIBLE);
          mGrid.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.capture_grid_on));
        }
        break;
      case R.id.ivGallery:
        finish();
        break;
    }
  }

  private PictureCallback mPicture = new PictureCallback() {

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

      File pictureFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_IMAGE);
      if (pictureFile == null) {
        Log.d(TAG, "Error creating media file, check storage permissions");
        return;
      }

      try {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(data);
        fos.close();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        mGallery.setImageBitmap(bitmap);
        mSafeToTakePicture = true;
        try {
          mCamera.stopPreview();
          mCamera.setPreviewDisplay(null);
        } catch (Exception e) {
          // ignore: tried to stop a non-existent preview
        }
        if (safeCameraOpen(0)) {
          startCamera();
        }
      } catch (FileNotFoundException e) {
        Log.d(TAG, "File not found: " + e.getMessage());
      } catch (IOException e) {
        Log.d(TAG, "Error accessing file: " + e.getMessage());
      }
    }
  };

  private boolean safeCameraOpen(int id) {
    boolean qOpened = false;

    try {
      releaseCameraAndPreview();
      mCamera = Camera.open(id);
      qOpened = (mCamera != null);
    } catch (Exception e) {
      Log.e(getString(R.string.app_name), "failed to open Camera");
      e.printStackTrace();
    }

    return qOpened;
  }

  private void releaseCameraAndPreview() {
    mCameraPreview.setCamera(null);
    if (mCamera != null) {
      mCamera.release();
      mCamera = null;
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    releaseCameraAndPreview();              // release the camera immediately on pause event
  }
}
