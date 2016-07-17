package com.example.customcamera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by jaspinder on 7/17/16.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
  private SurfaceHolder mHolder;
  private Camera mCamera;

  private final String TAG = "CameraPreview.class";

  public CameraPreview(Context context, Camera camera) {
    super(context);
    mCamera = camera;
    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);
  }

  public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      mCamera.setPreviewDisplay(holder);
      mCamera.startPreview();
    } catch (IOException e) {
      Log.d(TAG, "Error setting camera preview: " + e.getMessage());
    }
  }

  public boolean isCameraReady() {
    return mCamera != null;
  }

  public void setFlashMode(int mFlashMode) {
    if (mCamera != null) {
      Camera.Parameters mParameters = mCamera.getParameters();
      if (mFlashMode == 0) {
        mParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
      } else if (mFlashMode == 1) {
        mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
      } else {
        mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
      }
      mCamera.setParameters(mParameters);
    }
  }

  public void setCamera(Camera camera) {
    if (mCamera == camera) {
      return;
    }

    stopPreviewAndFreeCamera();

    mCamera = camera;

    if (mCamera != null) {
      List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
      //mSupportedPreviewSizes = localSizes;
      requestLayout();

      try {
        mCamera.setPreviewDisplay(mHolder);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Important: Call startPreview() to start updating the preview
      // surface. Preview must be started before you can take a picture.
      mCamera.startPreview();
    }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    // If your preview can change or rotate, take care of those events here.
    // Make sure to stop the preview before resizing or reformatting it.

    if (mHolder.getSurface() == null) {
      // preview surface does not exist
      return;
    }

    // stop preview before making changes
    try {
      mCamera.stopPreview();
    } catch (Exception e) {
      // ignore: tried to stop a non-existent preview
    }

    // set preview size and make any resize, rotate or
    // reformatting changes here

    // start preview with new settings
    try {
      mCamera.setPreviewDisplay(mHolder);
      mCamera.startPreview();

    } catch (Exception e) {
      Log.d(TAG, "Error starting camera preview: " + e.getMessage());
    }
    // Now that the size is known, set up the camera parameters and begin
    // the preview.
    Camera.Parameters parameters = mCamera.getParameters();
    //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
    requestLayout();
    mCamera.setParameters(parameters);

    // Important: Call startPreview() to start updating the preview surface.
    // Preview must be started before you can take a picture.
    mCamera.startPreview();
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // Surface will be destroyed when we return, so stop the preview.
    if (mCamera != null) {
      // Call stopPreview() to stop updating the preview surface.
      mCamera.stopPreview();
    }
  }

  /**
   * When this function returns, mCamera will be null.
   */
  private void stopPreviewAndFreeCamera() {

    if (mCamera != null) {
      // Call stopPreview() to stop updating the preview surface.
      mCamera.stopPreview();

      // Important: Call release() to release the camera for use by other
      // applications. Applications should release the camera immediately
      // during onPause() and re-open() it during onResume()).
      mCamera.release();

      mCamera = null;
    }
  }

}
