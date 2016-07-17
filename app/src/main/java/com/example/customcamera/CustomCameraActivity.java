package com.example.customcamera;

import android.hardware.Camera;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by jaspinder on 7/17/16.
 */
public class CustomCameraActivity extends AppCompatActivity implements OnClickListener {

  private CameraPreview mCameraPreview;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom_camera);
  }

  @Override
  public void onResume() {
    super.onResume();
    startCamera();
  }

  private void startCamera() {
    RelativeLayout cameraPreviewLayout = (RelativeLayout) findViewById(R.id.rlCameraPreview);
    cameraPreviewLayout.removeAllViews();
    mCameraPreview = new CameraPreview(this, getCameraInstance());
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

        break;
    }
  }
}
