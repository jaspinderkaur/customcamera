package com.example.customcamera.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by jaspinder on 7/17/16.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

  private SurfaceHolder mHolder;
  protected Camera mCamera;
  protected List<Camera.Size> mPreviewSizeList;
  protected List<Camera.Size> mPictureSizeList;
  protected Camera.Size mPreviewSize;
  protected Camera.Size mPictureSize;

  /**
   * State flag: true when surface's layout size is set and surfaceChanged()
   * process has not been completed.
   */
  protected boolean mSurfaceConfiguring = false;

  public CameraPreview(Context context, Camera camera, int flashMode) {
    super(context); // Always necessary
    mCamera = camera;
    Camera.Parameters params = mCamera.getParameters();
    if (flashMode == 0) {
      params.setFlashMode(Parameters.FLASH_MODE_AUTO);
    } else if (flashMode == 1) {
      params.setFlashMode(Parameters.FLASH_MODE_TORCH);
    } else {
      params.setFlashMode(Parameters.FLASH_MODE_OFF);
    }
    List<String> focus = params.getSupportedFocusModes();
    if (focus.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
      params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    mPreviewSizeList = params.getSupportedPreviewSizes();
    mPictureSizeList = params.getSupportedPictureSizes();
    mCamera.setParameters(params);
    mHolder = getHolder();
    mHolder.addCallback(this);
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCamera.setPreviewDisplay(mHolder);
    } catch (IOException e) {
      mCamera.release();
      mCamera = null;
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    if (holder != null) {
      doSurfaceChanged(width, height);
    }
  }

  private void doSurfaceChanged(int width, int height) {
    mCamera.stopPreview();
    Camera.Parameters cameraParams = mCamera.getParameters();
    boolean portrait = isPortrait();
    // The code in this if-statement is prevented from executed again when surfaceChanged is
    // called again due to the change of the layout size in this if-statement.
    if (!mSurfaceConfiguring) {
      Camera.Size previewSize = determinePreviewSize(portrait, width, height);
      Camera.Size pictureSize = determinePictureSize(previewSize);

      mPreviewSize = previewSize;
      mPictureSize = pictureSize;
    }
    configureCameraParameters(mCamera, cameraParams);
    mSurfaceConfiguring = false;

    try {
      mCamera.startPreview();
    } catch (Exception e) {
      Log.v("CameraPreview","Failed to start preview: " + e.getMessage());
    }
      // Remove failed size
      mPreviewSizeList.remove(mPreviewSize);
      mPreviewSize = null;

      // Reconfigure
      if (mPreviewSizeList.size() > 0) { // prevent infinite loop
        surfaceChanged(null, 0, width, height);
      } else {
        Toast.makeText(getContext(), "Can't start preview", Toast.LENGTH_LONG).show();
      }
    }

  /**
   * @param portrait
   * @param reqWidth  must be the value of the parameter passed in surfaceChanged
   * @param reqHeight must be the value of the parameter passed in surfaceChanged
   * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
   */
  protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
    // Meaning of width and height is switched for preview when portrait,
    // while it is the same as user's view for surface and metrics.
    // That is, width must always be larger than height for setPreviewSize.
    int reqPreviewWidth; // requested width in terms of camera hardware
    int reqPreviewHeight; // requested height in terms of camera hardware
    if (portrait) {
      reqPreviewWidth = reqHeight;
      reqPreviewHeight = reqWidth;
    } else {
      reqPreviewWidth = reqWidth;
      reqPreviewHeight = reqHeight;
    }

    Camera.Size retSize = null;
    // Adjust surface size with the closest aspect-ratio
    if (mPreviewSizeList != null) {
      retSize = getOptimalPreviewSize(mPreviewSizeList, reqPreviewWidth, reqPreviewHeight);
    }
    return retSize;
  }

  protected Camera.Size determinePictureSize(Camera.Size previewSize) {
    Camera.Size retSize = null;
    for (Camera.Size size : mPictureSizeList) {
      if (size.equals(previewSize)) {
        return size;
      }
    }
    Log.v("CameraPreview", "Same picture size not found.");

    // if the preview size is not supported as a picture size
    float reqRatio = ((float) previewSize.width) / previewSize.height;
    float curRatio, deltaRatio;
    float deltaRatioMin = Float.MAX_VALUE;
    for (Camera.Size size : mPictureSizeList) {
      curRatio = ((float) size.width) / size.height;
      deltaRatio = Math.abs(reqRatio - curRatio);
      if (deltaRatio < deltaRatioMin) {
        deltaRatioMin = deltaRatio;
        retSize = size;
      }
    }

    return retSize;
  }

  private Camera.Size getOptimalPreviewSize(List<Camera.Size> mSizes, int mTargetWidth, int mTargetHeight) {
    final double ASPECT_TOLERANCE = 0.1;
    double mTargetRatio = (double) mTargetHeight / mTargetWidth;
    if (mSizes == null) {
      return null;
    }
    Camera.Size mOptimalSize = null;
    double minDiff = Double.MAX_VALUE;
    for (Camera.Size size : mSizes) {
      double ratio = (double) size.height / size.width;
      if (Math.abs(ratio - mTargetRatio) > ASPECT_TOLERANCE) {
        continue;
      }
      if (Math.abs(size.height - mTargetHeight) < minDiff) {
        mOptimalSize = size;
        minDiff = Math.abs(size.height - mTargetHeight);
      }
    }
    if (mOptimalSize == null) {
      minDiff = Double.MAX_VALUE;
      for (Camera.Size size : mSizes) {
        if (Math.abs(size.height - mTargetHeight) < minDiff) {
          mOptimalSize = size;
          minDiff = Math.abs(size.height - mTargetHeight);
        }
      }
    }
    return mOptimalSize;
  }

  protected void configureCameraParameters(Camera camera, Camera.Parameters cameraParams) {
    int angle = getCameraDisplayOrientation();
    camera.setDisplayOrientation(angle);
    cameraParams.setRotation(angle);
    cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
    cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);

    Log.v("CustomCamera","Preview Actual Size - w: " + mPreviewSize.width + ", h: " + mPreviewSize.height);
    Log.v("CustomCamera","Picture Actual Size - w: " + mPictureSize.width + ", h: " + mPictureSize.height);
    mCamera.setParameters(cameraParams);
  }

  private int getCameraId() {
    int numberOfCameras = mCamera.getNumberOfCameras();
    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    for (int i = 0; i < numberOfCameras; i++) {
      mCamera.getCameraInfo(i, cameraInfo);
      if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
        return i;
      }
    }
    return 0;
  }

  public int getCameraDisplayOrientation() {
    android.hardware.Camera.CameraInfo info =
        new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(getCameraId(), info);
    Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    int rotation = display.getRotation();
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
    return result;
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    stopCamera();
  }

  public boolean isPortrait() {
    return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
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

  /**
   * Equivalent to takePicture(shutter, raw, null, jpeg).
   *
   * @see Camera#takePicture(Camera.ShutterCallback, Camera.PictureCallback, Camera.PictureCallback)
   */
  public final boolean takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
                                   Camera.PictureCallback jpeg) {
    if (mCamera != null) {
      try {
        mCamera.takePicture(shutter, raw, jpeg);
        return true;
      } catch (RuntimeException e) {
        Log.e("CustomCamera",Log.getStackTraceString(e));
      }
    }
    return false;
  }

  public void stopCamera() {
    if (mHolder != null) {
      mHolder.removeCallback(this);
      mHolder = null;
    }

    if (mCamera != null) {
      mCamera.stopPreview();
      mCamera.setPreviewCallback(null);
      mCamera.release();
      mCamera = null;
    }
  }

  public void setCamera(Camera camera) {
    mCamera = camera;
  }
}