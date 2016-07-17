package com.example.customcamera;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.customcamera.helper.CameraHelper;
import com.example.customcamera.helper.RuntimePermissionsActivity;

import java.io.File;

public class MainActivity extends RuntimePermissionsActivity {

  private LinearLayout mGallery;

  private static final int REQUEST_PERMISSIONS = 20;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MainActivity.super.requestAppPermissions(new
                String[]{permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string
                .camera_permissions_denied
            , REQUEST_PERMISSIONS);
      }
    });
    setImageGallery();
  }

  private void setImageGallery() {
    mGallery = (LinearLayout) findViewById(R.id.llGallery);

    String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES).getAbsolutePath();

    String targetPath = ExternalStorageDirectoryPath + "/MyCameraApp/";

    Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
    File targetDirector = new File(targetPath);

    File[] files = targetDirector.listFiles();
    if (files != null) {
      for (File file : files) {
        mGallery.addView(CameraHelper.insertPhoto(file.getAbsolutePath(),getApplicationContext()));
      }
    }
  }

  @Override
  public void onPermissionsGranted(int requestCode) {
    Intent cameraIntent = new Intent(MainActivity.this, CustomCameraActivity.class);
    startActivity(cameraIntent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
