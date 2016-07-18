package com.example.customcamera.helper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

/**
 * Created by jaspinder on 7/17/16.
 */
public abstract class RuntimePermissionsActivity extends AppCompatActivity {
  private SparseIntArray mErrorString;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mErrorString = new SparseIntArray();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    int permissionCheck = PackageManager.PERMISSION_GRANTED;
    for (int permission : grantResults) {
      permissionCheck = permissionCheck + permission;
    }
    if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
      onPermissionsGranted(requestCode);
    } else {
      Snackbar.make(findViewById(android.R.id.content), mErrorString.get(requestCode),
          Snackbar.LENGTH_SHORT).show();
    }
  }

  public void requestAppPermissions(final String[] requestedPermissions,
                                    final int stringId, final int requestCode) {
    mErrorString.put(requestCode, stringId);
    int permissionCheck = PackageManager.PERMISSION_GRANTED;
    boolean shouldShowRequestPermissionRationale = false;
    for (String permission : requestedPermissions) {
      permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
      shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(RuntimePermissionsActivity.this, requestedPermissions, requestCode);
    } else {
      onPermissionsGranted(requestCode);
    }
  }

  public abstract void onPermissionsGranted(int requestCode);
}
